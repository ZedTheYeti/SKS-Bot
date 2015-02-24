package yeti.irc;

import yeti.bot.util.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class IRCServer
{
   private String nick, ident, realName;
   private String server, serverPass;
   private int port = 6667;

   private DataOutputStream out;
   private BufferedReader in;
   private Socket connection;
   private boolean running = false;

   private Thread inThread;

   private ArrayList<Parser> cmdParsers = new ArrayList<Parser>();
   private ArrayList<Parser> msgParsers = new ArrayList<Parser>();

   public IRCServer(String serverAddress, int portNum)
   {
      server = serverAddress;
      port = portNum;
   }

   // TODO keep track of channels
   // TODO command processors

   public void connect()
   {
      running = true;

      try
      {
         connection = new Socket(server, port);
         out = new DataOutputStream(connection.getOutputStream());
         in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

         inThread = new Thread(new Runnable()
         {
            @Override
            public void run()
            {
               while (running)
               {
                  // Process
                  readInput();

                  // Wait
                  Thread.yield();
                  try
                  {
                     Thread.sleep(1);
                  } catch (InterruptedException ie)
                  {
                     ie.printStackTrace();
                  }
               }
            }
         });
         inThread.start();

         if (serverPass != null && !serverPass.isEmpty())
            sendLine("PASS " + serverPass);
         sendLine("NICK " + nick);
         sendLine("USER " + ident + " " + server + " bla :" + realName);
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public void joinChannel(String channelName)
   {
      if (!channelName.startsWith("#"))
         channelName = "#" + channelName;

      sendLine("JOIN " + channelName);
   }

   public void sendMessage(String dest, String msg)
   {
      sendLine("PRIVMSG " + dest + " :" + msg);
   }

   public void addCmdParser(Parser parser)
   {
      cmdParsers.add(parser);
   }

   public void processCmd(String cmd)
   {
      for (Parser p : cmdParsers)
         p.parse(cmd);
   }

   public void processMsg(String msg)
   {
      for (Parser p : msgParsers)
         p.parse(msg);
   }

   public void addMsgParser(Parser pasrser)
   {
      msgParsers.add(pasrser);
   }

   private void readInput()
   {
      try
      {
         if (in.ready())
         {
            String line;
            while ((line = in.readLine()) != null)
            {
               Logger.logMsg(line);
               processMsg(line);
            }
         }
      } catch (IOException ioe)
      {
         ioe.printStackTrace();
         // TODO Better handle connection getting reset, attempt to reconnect etc
      }
   }

   public void sendLine(String string)
   {
      Logger.logMsg(string);
      sendRaw(string + "\r\n");
   }

   private void sendRaw(String string)
   {
      try
      {
         out.writeBytes(string);
      } catch (IOException ioe)
      {
         ioe.printStackTrace();
      }
   }

   public String getNick()
   {
      return nick;
   }

   public void setNick(String nick)
   {
      this.nick = nick;
   }

   public String getIdent()
   {
      return ident;
   }

   public void setIdent(String ident)
   {
      this.ident = ident;
   }

   public String getRealName()
   {
      return realName;
   }

   public void setRealName(String realName)
   {
      this.realName = realName;
   }

   public String getServer()
   {
      return server;
   }

   public void setServer(String server)
   {
      this.server = server;
   }

   public String getServerPass()
   {
      return serverPass;
   }

   public void setServerPass(String serverPass)
   {
      this.serverPass = serverPass;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }
}
