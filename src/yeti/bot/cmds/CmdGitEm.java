package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;

/**
 * Created by Z on 2/11/2015.
 */
public class CmdGitEm extends Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      System.out.println("BOOP");
      return isEnabled() && cmd.startsWith("!gitemwally");
   }

   @Override
   public void process(String user, String msg)
   {
      System.out.println("BEDOOP");
      String parts[] = msg.split(" ");

      if (parts.length < 2)
         return;

      StringBuilder bldr = new StringBuilder(".timeout ");
      bldr.append(parts[1].toLowerCase());
      bldr.append(' ');
      if (parts.length >= 3)
         bldr.append(parts[2]);
      else
         bldr.append(10);

      StringBuilder msgBldr = new StringBuilder("I'll put ya down ");
      msgBldr.append(parts[1]).append('!');

      JIRC.sendMessage(Globals.channel, bldr.toString());
      JIRC.sendMessage(Globals.channel, msgBldr.toString());
   }

   @Override
   public String getUsage()
   {
      return "!gitemwally <user> [time]";
   }
}
