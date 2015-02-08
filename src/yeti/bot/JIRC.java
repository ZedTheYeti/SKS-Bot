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

import yeti.bot.cmds.Command;
import yeti.bot.gui.ChatFrame;
import yeti.bot.gui.SendListener;
import yeti.bot.gui.StartDialog;
import yeti.bot.util.Logger;
import yeti.bot.util.Options;
import yeti.bot.util.Util;
import yeti.irc.IRCServer;
import yeti.irc.Parser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import static yeti.bot.Globals.*;

public class JIRC
{
   private static ChatFrame frame;
   private static Options save;
   private static boolean saved = false;
   private static boolean saving = false;

   public static void main(String args[])
   {
      try
      {
         String logging = System.getProperty("logging", "none");
         switch(logging.toLowerCase())
         {
            case "error": Logger.setLevel(Logger.ERROR); break;
            case "debug": Logger.setLevel(Logger.DEBUG); break;
            case "all": Logger.setLevel(Logger.ALL); break;
            default: Logger.setLevel(Logger.NONE); break;
         }

         main();
      } catch (Exception ex)
      {
         Logger.logError("An unhandled exception has occurred: " + ex.getMessage());
         ex.printStackTrace();
         if (save != null)
            saveAll();
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
         e.printStackTrace();
      }

      for (User usr : offlineUsers.values())
         save.set(usr.name, usr.getInfo());
      for (User usr : users.values())
         save.set(usr.name, usr.getInfo());
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

      username = options.get("username").toLowerCase();
      String oauth = options.get("oauth");
      serverName = options.get("server");
      port = Integer.parseInt(options.get("port"));
      channel = options.get("channel").toLowerCase();
      if (channel.charAt(0) != '#')
         channel = "#" + channel;

      server = new IRCServer(serverName, port);
      server.setNick(username);
      server.setIdent(username);
      server.setRealName(username);
      server.setServerPass(oauth);

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
               Logger.logDebug(sub.name + " is a mod " + sub.captain);
            } else if (parts[1].equalsIgnoreCase("-o"))
            {
               User sub = users.get(parts[2]);
               if (sub != null)
               {
                  sub.captain = false;
                  Logger.logDebug(sub.name + " is no longer a mod " + sub.captain);
               }
            }
         } else if (input.contains(" PRIVMSG " + channel))
         {
            String name = input.substring(1, input.indexOf("!"));
            String msg = input.substring(input.indexOf(':', 1) + 1);
            frame.addText(name + ": " + msg + "\n");

            if (msg.length() == 0 || msg.charAt(0) != '!')
               return;

            User user = users.get(name);
            if (user != null && user.captain)
               if (msg.startsWith("!beta"))
               {
                  sendMessage(channel,
                        "/me The faction portion of this bot is still in a VERY early beta. So expect hiccups ;) Zedtheyeti is monitoring things and will do his best to fix any bugs that pop up. Most importantly just have fun and don't worry about it koolWALLY");
                  return;
               }

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
            Logger.logDebug(name + " joined");

            User user = getOrCreateUser(name);

            user.inChannel = true;

            long time = System.currentTimeMillis();
            // No idea how this would happen, but oh well
            if (user.joinTime > time || user.joinTime == 0)
               user.joinTime = time;
         } else if (input.contains(" PART "))
         {
            String name = input.substring(1, input.indexOf('!'));
            Logger.logDebug(name + " left");
            User user = users.remove(name);
            if (user != null)
            {
               Logger.logDebug("Moving " + name + " from online users to offline users");
               user.inChannel = false;
               offlineUsers.put(name, user);
            }
         }
         // 353 wallythewizard = #sourkoolaidshow :
         else if (input.contains("353 " + Globals.username + " = " + channel + " :"))
         {
            int index = input.indexOf(':', 1);
            String[] names = input.substring(index + 1).split(" ");
            for (String name : names)
            {
               User usr = getOrCreateUser(name);
               usr.inChannel = true;
               usr.joinTime = System.currentTimeMillis();
            }
         }

      });

      server.addCmdParser(line -> {
         String[] parts = line.split(" ");
         if (parts.length >= 2 && parts[0].equalsIgnoreCase("join"))
            server.sendLine("JOIN " + parts[1]);
         else if (parts.length >= 2 && parts[0].equalsIgnoreCase("leave"))
            server.sendLine("PART " + parts[1]);
         else if (parts.length >= 2 && parts[0].equalsIgnoreCase("pm"))
            server.sendMessage(parts[1], parts[2]);
         else if (parts[0].equalsIgnoreCase("exit"))
            System.exit(0);
         else if (parts[0].equalsIgnoreCase("raw"))
            server.sendLine(line.substring(line.indexOf(' ') + 1));
      });

      Logger.logDebug("Loading user info..");
      save = new Options("sksbot/user.info");
      save.load();
      frame.addText(save.getPath() + "\n");
      for (Entry<String, String> entry : save.getAllOptions())
      {
         //Logger.logDebug(entry.getValue());
         String name = entry.getKey().toLowerCase();
         User sub = new User(name);
         String[] parts = entry.getValue().split(",");
         sub.faction = Faction.valueOf(parts[0]);
         sub.level = Integer.parseInt(parts[1]);
         sub.exp = Float.valueOf(parts[2]);
         if (parts.length > 3)
            sub.userClass = UserClass.valueOf(parts[3]);
         // sub.exp = 0f;
         offlineUsers.put(name, sub);
      }
      Logger.logDebug("Done.");

      Logger.logDebug("Connecting to " + server + ":" + port + "...");
      server.connect();

      server.joinChannel(channel);
      server.sendLine("TWITCHCLIENT 1");
      sendMessage(channel, "/me has returned from the astral plane!");


      Timer timer = new Timer();
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            if (!trackXp)
            {
               if (!saved)
                  saveAll();
               saved = !saved;
               return;
            }

            if (!saved)
               for (User usr : offlineUsers.values())
                  save.set(usr.name, usr.getInfo());

            long time = System.currentTimeMillis();
            for (User sub : users.values())
            {
               if (sub.joinTime == 0)
               {
                  if (sub.inChannel)
                  {
                     Logger.logError("0 JOIN TIME " + sub.name);
                     sub.joinTime = time;
                  } else
                     Logger.logError("NOT IN CHANNEL " + sub.name);
                  continue;
               }

               long diff = time - sub.joinTime;
               if (diff >= XP_AWARD_TIME)
               {
                  Logger.logDebug(sub.name + " joined at " + sub.joinTime + " the difference between then and now is " + diff + " award time is " + XP_AWARD_TIME);
                  Logger.logDebug(sub.name + " is at " + sub.exp + "10xp");
                  Logger.logDebug("We are " + (diff - XP_AWARD_TIME) + " behind");
                  sub.exp += XP_AWARD_AMOUNT;
                  sub.joinTime += XP_AWARD_TIME;
               }

               if (!saved)
                  save.set(sub.name, sub.getInfo());
            }

            if (xpTrackTime != 0)
            {
               if (time - xpStartTime >= xpTrackTime)
               {
                  trackXp = false;
                  JIRC.sendMessage(Globals.channel, "XP tracking has been turned off.");
               }
            }

            if (!saved)
            {
               Logger.logDebug("Saving...");
               try
               {
                  Files.copy(save.getFile().toPath(), new File(save.getPath() + ".bkp").toPath(), StandardCopyOption.REPLACE_EXISTING);
               } catch (IOException e)
               {
                  e.printStackTrace();
               }

               save.save();
               Logger.logDebug("Done.");
            }
            saved = !saved;
         }
      };
      timer.scheduleAtFixedRate(task, 4 * 60 * 1000, 1 * 60 * 1000);
   }
}
