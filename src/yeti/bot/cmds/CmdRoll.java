package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;
import yeti.bot.util.Util;

/**
 * Created by Z on 7/28/2014.
 */
public class CmdRoll extends Command
{
   // TODO Add ability to d !xdy where x is the number of die and y is the number of sides Ex: !2d4 is 2 four sided die

   @Override
   public boolean check(String name, String cmd, boolean isSub)
   {
      User usr = Globals.getOnlineUser(name);
      int index = cmd.toLowerCase().indexOf('d');
      return isEnabled() && usr != null && (index == 1 || index != cmd.length() - 1) && isNum(cmd.charAt(index + 1));
   }

   private boolean isNum(char c)
   {
      return c >= '0' && c <= '9';
   }

   public void process(String name, String cmd)
   {
      cmd = cmd.trim();
      String[] parts = cmd.split(" ");

      if (parts.length < 1)
         return;

      int index = cmd.toLowerCase().indexOf('d');
      if (index <= 0)
         return;

      if (index == 1)
      {
         int sides;

         try
         {
            sides = Integer.parseInt(parts[0].substring(index + 1));
         } catch (NumberFormatException nfe)
         {
            Logger.logError(nfe.getMessage());
            Logger.logError(Logger.getStackTrace(nfe));
            return;
         }

         if (sides > 0)
         {
            int outcome = Util.rollDie(sides);
            index = cmd.indexOf(' ');

            StringBuilder bldr = new StringBuilder();
            bldr.append("/me rolls a ").append(outcome);
            bldr.append(" on a d").append(sides);

            if (index != -1)
               bldr.append(" for ").append(cmd.substring(index));
            bldr.append('.');

            if (outcome == sides)
               bldr.append(" Critical!");
            else if (outcome == 1)
               bldr.append(" Critical failure!");

            JIRC.sendMessage(Globals.channel, bldr.toString());
         }
      } else
      {
         int times, sides;

         try
         {
            times = Integer.parseInt(parts[0].substring(1, index));
            sides = Integer.parseInt(parts[0].substring(index + 1));
         } catch (NumberFormatException nfe)
         {
            Logger.logError(nfe.getMessage());
            Logger.logError(Logger.getStackTrace(nfe));
            return;
         }

         index = cmd.indexOf(' ');

         if (times > 0 && sides > 0)
         {
            int outcome = 0;
            StringBuilder bldr = new StringBuilder("/me rolls ");

            for (int i = 0; i < times; i++)
            {
               int roll = Util.rollDie(sides);
               if (roll == sides && sides == 20)
                  bldr.append("koolCRIT");
               else if (roll == 1)
                  bldr.append("koolFAIL");
               else
                  bldr.append(roll);
               if (i < times - 1)
                  bldr.append(", ");
               outcome += roll;
            }

            bldr.append(" for a total of ").append(outcome);
            bldr.append(" out of ").append(sides * times).append(" possible");
            bldr.append(" on a ").append(times).append('d').append(sides);
            if (index != -1)
               bldr.append(" for ").append(cmd.substring(index));
            bldr.append('.');

            JIRC.sendMessage(Globals.channel, bldr.toString());
         }
      }
   }

   public String getUsage()
   {
      return "!d4, !d6, !d8, !d10, !d12, !d20, !d100";
   }
}
