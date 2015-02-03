package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Util;

/**
 * Created by Z on 7/28/2014.
 */
public class CmdRoll implements Command
{
   @Override
   public boolean check(String name, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(name);

      return usr != null && (cmd.startsWith("!d4") || cmd.startsWith("!d6") || cmd.startsWith("!d8") || cmd.startsWith("!d10") || cmd.startsWith("!d12") || cmd.startsWith("!d20") || cmd.startsWith("!d100"));
   }

   public void process(String name, String cmd)
   {
      String[] parts = cmd.trim().split(" ");

      if(parts.length < 1)
         return;

      parts[0] = parts[0].substring(2);
      int sides = Integer.parseInt(parts[0]);
      if(sides > 0)
      {
         int outcome = Util.rollDie(sides);
         int index = cmd.indexOf(' ');

         StringBuilder bldr = new StringBuilder();
         bldr.append("/me rolls a ").append(outcome);
         bldr.append(" on a d").append(sides);

         if(index != -1)
            bldr.append(" for ").append(cmd.substring(index));
         bldr.append('.');

         if(outcome == sides)
            bldr.append(" Critical!");
         else if(outcome == 1)
            bldr.append(" Critical failure!");

         JIRC.sendMessage(Globals.channel, bldr.toString());
      }
   }

   public String getUsage()
   {
      return "!d4, !d6, !d8, !d10, !d12, !d20, !d100";
   }
}
