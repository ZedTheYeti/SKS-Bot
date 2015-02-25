/* Copyright (c) 2014-Onwards, Yeti Games
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list
 *   of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice, this list
 *   of conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * * Neither the name Yeti Games nor the names of its contributors may be used to
 *   endorse or promote products derived from this software without specific prior written
 *   permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package yeti.bot;

import sun.rmi.runtime.Log;
import yeti.bot.cmds.Command;
import yeti.bot.gui.ChatFrame;
import yeti.bot.gui.StartDialog;
import yeti.bot.util.*;
import yeti.irc.IRCServer;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static yeti.bot.Globals.*;

public class JIRC
{
   private static ChatFrame frame;
   private static Options save;
   private static boolean saved = false;
   private static boolean saving = false;
   private static boolean saveToDb = true;
   private static boolean autoStart = false;

   public static void main(String args[])
   {
      try
      {
         String logging = System.getProperty("logging", "error");
         switch(logging.toLowerCase())
         {
            case "debug": Logger.setLevel(Logger.DEBUG); break;
            case "all": Logger.setLevel(Logger.ALL); break;
            case "none": Logger.setLevel(Logger.NONE); break;
            default: Logger.setLevel(Logger.ERROR); break;
         }

         String redirect = System.getProperty("redirect.output", "true");
         switch(redirect.toLowerCase())
         {
            case "false": break;
            default: Logger.redirectOutput();
         }

         if(args.length > 0)
            autoStart = args[0].contains("autostart");
         if(args.length > 1)
            numRestarts = Integer.valueOf(args[1]);

         main();
      } catch (Exception ex)
      {
         Logger.logError("An error occurred in the program, please check the error log. Error Message:\n" + ex.getMessage(), true);
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", ex.getMessage());
         ex.printStackTrace();
         if (save != null)
            saveAll();
         Util.restart();
         throw ex;
      } catch(Error e)
      {
         Logger.logError("An error occurred in the program, please check the error log. Error Message:\n" + e.getMessage(), true);
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", e.getMessage());
         e.printStackTrace();
         if(save != null)
            saveAll();
         Util.restart();
         throw e;
      }
   }

   public static void saveAll()
   {
      if (saving)
         return;

      saving = true;
      try
      {
         Files.copy(save.getFile().toPath(), new File(save.getPath() + ".bkp").toPath(), StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e)
      {
         Logger.logError("Error backing up user.info file to " + save.getPath() + ".bkp\n" + e.getMessage(), true);
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", "Error backing up user.info file. " + e.getMessage());
         e.printStackTrace();
      }

      for (User usr : Globals.getOfflineUsers())
         save.set(usr.getName(), usr.getInfo());
      for (User usr : Globals.getOnlineUsers())
         save.set(usr.getName(), usr.getInfo());
      save.save();

      Logger.logDebug("Finished saving all users");
      saving = false;
   }

   public static void sendMessage(String channel, String msg)
   {
      if (server != null)
         server.sendMessage(channel, msg);
      if (frame != null)
         frame.addText(username + ": " + msg + "\n");
   }

   public static boolean streamIsLive(String channel)
   {
      String httpsUrl = "https://api.twitch.tv/kraken/streams/" + channel;
      boolean live = false;
      try
      {
         URL url = new URL(httpsUrl);
         HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line;

         while((line = in.readLine()) != null)
         {
            if(line.contains("\"stream\":null"))
            {
               live = false;
               break;
            }else if(line.contains("\"stream\":{"))
            {
               live = true;
               break;
            }
         }

         in.close();
      }catch(MalformedURLException muex)
      {
         muex.printStackTrace();
      }catch(IOException ioex)
      {
         ioex.printStackTrace();
      }

      return live;
   }

   public static void startLiveTimer()
   {
      Timer timer = new Timer();
      TimerTask timerTask = new TimerTask()
      {
         @Override
         public void run()
         {
            Logger.logDebug("Live check for " + Globals.channel.substring(1));
            if(streamIsLive(Globals.channel.substring(1)))
            {
               Logger.logDebug(Globals.channel + " is live");
               if(Globals.xpAwardAmount != Globals.XP_LIVE_AWARD_AMOUNT)
               {
                  Globals.xpAwardAmount = Globals.XP_LIVE_AWARD_AMOUNT;
                  JIRC.sendMessage(Globals.channel, "/me SKS is live! The amount of XP awarded per hour has been set to full.");
               }
            }
            else
            {
               Logger.logDebug(Globals.channel + " is offline");
               if(Globals.xpAwardAmount != Globals.XP_OFFLINE_AWARD_AMOUNT)
               {
                  Globals.xpAwardAmount = Globals.XP_OFFLINE_AWARD_AMOUNT;
                  JIRC.sendMessage(Globals.channel, "/me SKS is offline. The amount of XP awarded per hour has be set to half.");
               }
            }
         }
      };
      timer.scheduleAtFixedRate(timerTask, 0, 5 * 60 * 1000);
   }

   public static void main()
   {
      Util.setSystemLAF();

      try
      {
         EventQueue.invokeAndWait(() -> {
            try
            {
               frame = new ChatFrame();
               frame.setVisible(true);
               frame.addWindowListener(new WindowAdapter()
               {
                  @Override
                  public void windowClosing(WindowEvent e)
                  {
                     Logger.logDebug("Saving on close.");
                     saveAll();
                  }
               });
               frame.setSendListener(text -> {
                  if (text.startsWith("/") && !text.startsWith("/me"))
                     server.processCmd(text.substring(1));
                  else
                     sendMessage(Globals.channel, text);
               });
            } catch (Exception e)
            {
               e.printStackTrace();
            }
         });
      } catch (Exception e1)
      {
         e1.printStackTrace();
      }

      final Options options = new Options("sksbot/settings.opt");
      frame.addText(options.getPath() + "\n");
      options.load();


      if(!autoStart)
      {
         try
         {
            EventQueue.invokeAndWait(() -> {
               try
               {
                  StartDialog dialog = new StartDialog(frame, true, options);
                  dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                  dialog.setVisible(true);
               } catch (Exception e)
               {
                  e.printStackTrace();
               }
            });
         } catch (InvocationTargetException e)
         {
            e.printStackTrace();
         } catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }

      username = options.get("username").toLowerCase();
      Globals.pushbulletKey = options.get("pushbullet_key");
      String oauth = options.get("oauth");
      if(oauth.startsWith("oauth:"))
         oauth = oauth.substring(oauth.indexOf(':') + 1);
      serverName = options.get("server");
      port = Integer.parseInt(options.get("port"));
      channel = options.get("channel").toLowerCase();
      if (channel.charAt(0) != '#')
         channel = "#" + channel;

      server = new IRCServer(serverName, port);
      server.setNick(username);
      server.setIdent(username);
      server.setRealName(username);
      MySQL.setPassword(oauth);
      server.setServerPass("oauth:" + oauth);

      server.addMsgParser(input -> {
         if (input.startsWith("PING "))
            server.sendLine("PONG " + input.substring(5));
         else if (input.contains(":jtv PRIVMSG " + server.getNick() + " :SPECIALUSER"))
         {
            String[] args = input.substring(input.indexOf(':', 1) + 1).split(" ");
            if (args[0].equals("SPECIALUSER"))
            {
               User user = getOrCreateUser(args[1]);
               user.inChannel = true;
               user.isSub = true;
            }
         } else if (input.contains(":jtv MODE "))
         {
            String[] parts = input.substring(input.indexOf("MODE") + "MODE ".length()).split(" ");

            if (parts[1].equalsIgnoreCase("+o"))
            {
               User sub = getOrCreateUser(parts[2]);
               sub.captain = true;
               Logger.logDebug(sub.getName() + " is a mod");
            } else if (parts[1].equalsIgnoreCase("-o"))
            {
               User sub = Globals.getOnlineUser(parts[2]);
               if (sub != null)
               {
                  sub.captain = false;
                  Logger.logDebug(sub.getName() + " is no longer a mod");
               }
            }
         } else if (input.contains(" PRIVMSG " + channel))
         {
            String name = input.substring(1, input.indexOf("!"));
            String msg = input.substring(input.indexOf(':', 1) + 1);
            frame.addText(name + ": " + msg + "\n");

            if (msg.length() == 0 || msg.charAt(0) != '!')
               return;

            User user = Globals.getOnlineUser(name);

            boolean isSub = user != null && user.isSub;
            boolean isMod = user != null && user.captain;

            if (isMod)
               for (Command cmd : modCommands)
                  if (cmd.check(name, msg.toLowerCase(), isSub))
                  {
                     cmd.process(name, msg);
                     return;
                  }

            if (isSub || isMod)
               for (Command cmd : subCommands)
                  if (cmd.check(name, msg.toLowerCase(), isSub))
                  {
                     cmd.process(name, msg);
                     return;
                  }

            for (Command cmd : commands)
               if (cmd.check(name, msg.toLowerCase(), isSub))
               {
                  cmd.process(name, msg);
                  break;
               }
         } else if (input.contains(" JOIN "))
         {
            String name = input.substring(1, input.indexOf('!'));
            Logger.logMsg(name + " joined");

            User user = Globals.getOnlineUser(name);
            if(user == null)
            {
               user = Globals.removeOfflineUser(name);
               if(user != null)
                  Globals.addOnlineUser(name, user);
            }

            if(user != null)
            {
               user.inChannel = true;

               long time = System.currentTimeMillis();
               // No idea how this would happen, but oh well
               if (user.joinTime > time || user.joinTime == 0)
                  user.joinTime = time;
            }
         } else if (input.contains(" PART "))
         {
            String name = input.substring(1, input.indexOf('!'));
            Logger.logMsg(name + " left");
            User user = removeOnlineUser(name);
            if (user != null)
            {
               Logger.logMsg("Moving " + name + " from online users to offline users");
               user.inChannel = false;
               Globals.addOnlineUser(name, user);
            }
         }
         // 353 wallythewizard = #sourkoolaidshow :
         else if (input.contains("353 " + Globals.username + " = " + channel + " :"))
         {
            int index = input.indexOf(':', 1);
            String[] names = input.substring(index + 1).split(" ");
            for (String name : names)
            {
               User user = Globals.getOnlineUser(name);
               if(user == null)
               {
                  user = Globals.removeOfflineUser(name);
                  if(user != null)
                     Globals.addOnlineUser(name, user);
               }

               if(user != null)
               {
                  user.inChannel = true;
                  long time = System.currentTimeMillis();
                  if (user.joinTime > time || user.joinTime == 0)
                     user.joinTime = time;
               }
            }
         }

      });

      server.addCmdParser(line -> {
         String[] parts = line.split(" ");
         if(parts.length >= 2)
         {
            if (parts[0].equalsIgnoreCase("join"))
               server.sendLine("JOIN " + parts[1]);
            else if (parts[0].equalsIgnoreCase("leave"))
               server.sendLine("PART " + parts[1]);
            else if (parts[0].equalsIgnoreCase("pm"))
               server.sendMessage(parts[1], parts[2]);
         }
         else if (parts[0].equalsIgnoreCase("exit"))
            System.exit(0);
         else if (parts[0].equalsIgnoreCase("raw"))
            server.sendLine(line.substring(line.indexOf(' ') + 1));
      });

      save = new Options("sksbot/user.info");
      /*frame.addText("Local cache: " + save.getPath() + "\n");
      Logger.logDebug("Loading local user info cache...");
      frame.addText("Loading local user info cache...\n");

      save.load();
      for (Map.Entry<String, String> entry : save.getAllOptions())
      {
         //Logger.logDebug(entry.getValue());
         String name = entry.getKey().toLowerCase();
         User sub = new User(name);
         String[] parts = entry.getValue().split(",");
         sub.setFaction(Faction.valueOf(parts[0]));
         sub.setLevel(Integer.parseInt(parts[1]));
         sub.setExp(Float.valueOf(parts[2]));
         if(sub.getExp() < 0.5 && sub.getFaction() == Faction.COUNCIL)
            continue;
         if (parts.length > 3)
            sub.setUserClass(UserClass.valueOf(parts[3]));
         // sub.exp = 0f;
         Globals.addOfflineUser(name, sub);
      }
      Logger.logDebug("Done.");
      frame.addText("Done.");*/

      //// MySQL DATABASE TESTING ////
      //MySQL.compareUsers();
      frame.addText("Loading users from the database...\n");
      MySQL.loadUsers(Globals.getOfflineMap());
      frame.addText("Done.\n");
      //MySQL.compareUsers();
      //System.exit(0);
      //// MySQL DATABASE TESTING ////

      Logger.logDebug("Connecting to " + server + ":" + port + "...");
      server.connect();

      server.joinChannel(channel);
      server.sendLine("TWITCHCLIENT 1");
      sendMessage(channel, "/me has returned from the astral plane!");

      startLiveTimer();

      Timer timer = new Timer();
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            if(trackXp)
            {
               Logger.logDebug("Beginning xp update...");
               long time = System.currentTimeMillis();
               for (User sub : Globals.getOnlineUsers())
               {
                  if (sub.joinTime == 0)
                  {
                     if (sub.inChannel)
                     {
                        Logger.logError("0 JOIN TIME " + sub.getName());
                        sub.joinTime = time;
                     } else
                        Logger.logError("NOT IN CHANNEL " + sub.getName());
                     continue;
                  }

                  long diff = time - sub.joinTime;
                  if (diff >= XP_AWARD_TIME)
                  {
                     //Logger.logDebug(sub.getName() + " joined at " + sub.joinTime + " the difference between then and now is " + diff + " award time is " + XP_AWARD_TIME);
                     //Logger.logDebug(sub.getName() + " is at " + sub.getExp() + "10xp");
                     //Logger.logDebug("We are " + (diff - XP_AWARD_TIME) + " behind");
                     sub.setExp(sub.getExp() + Globals.xpAwardAmount);
                     sub.joinTime += XP_AWARD_TIME;
                     save.set(sub.getName(), sub.getInfo());
                  }
               }
               Logger.logDebug("Finished xp update.");

               if (xpTrackTime != 0)
               {
                  if (time - xpStartTime >= xpTrackTime)
                  {
                     trackXp = false;
                     JIRC.sendMessage(Globals.channel, "XP tracking has been turned off.");
                  }
               }
            }
         }
      };
      timer.scheduleAtFixedRate(task, 2 * 60 * 1000, 5 * 60 * 1000);

      TimerTask saveTask = new TimerTask()
      {
         @Override
         public void run()
         {
            if(saveToDb)
            {
               frame.addText("Saving users to database.\n");
               Logger.logDebug("Saving users to database.");
               MySQL.updateUsers();
               Logger.logDebug("Done saving users to database.");
               frame.addText("Done saving users to database.\n");
               saveToDb = false;
            } else
            {
               if(!saving)
               {
                  saving = true;
                  Logger.logDebug("Saving users to local cache.");
                  frame.addText("Saving users to local cache.\n");
                  save.save();
                  saving = false;
                  Logger.logDebug("Done saving users to local cache.");
                  frame.addText("Done saving users to local cache.\n");
               }
               saveToDb = true;
            }
         }
      };
      timer.scheduleAtFixedRate(saveTask, 5* 60 * 1000, 5 * 50 * 1000);

      // After a successful start, clear numRestarts
      Globals.numRestarts = 0;
   }
}
