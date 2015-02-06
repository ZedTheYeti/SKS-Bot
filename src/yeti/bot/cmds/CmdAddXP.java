package yeti.bot.cmds;

import jdk.nashorn.internal.objects.Global;
import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;

/**
 * Created by Z on 1/14/2015.
 */
public class CmdAddXP extends Command
{

   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(user);
      return isEnabled() && usr != null && (usr.captain || usr.name.equalsIgnoreCase("sourkoolaidshow")) && (cmd.startsWith("!addxp"));
   }

   @Override
   public void process(String user, String msg)
   {
      String parts[] = msg.split(" ");

      if(parts.length < 3)
      {
         Logger.logDebug("Incorrect usage of !addxp: \"" + msg + "\"");
         return;
      }

      int amount = Integer.MIN_VALUE;
      try
      {
         amount = Integer.parseInt(parts[2]);
      }catch(NumberFormatException exc)
      {
         Logger.logDebug("Incorrect usage of !addxp: \"" + msg + "\"");
         return;
      }

      User targetUsr = Globals.users.get(parts[1].toLowerCase());
      if(targetUsr == null)
      {
         if(parts[1].equalsIgnoreCase("rockbitters"))
         {
            awardFactionXP(Faction.ROCKBITER, amount);
            return;
         } else if(parts[1].equalsIgnoreCase("school"))
         {
            awardFactionXP(Faction.SCHOOL, amount);
            return;
         } else if(parts[1].equalsIgnoreCase("knights"))
         {
            awardFactionXP(Faction.KNIGHTS, amount);
            return;
         } else if(parts[1].equalsIgnoreCase("guild"))
         {
            awardFactionXP(Faction.GUILD, amount);
            return;
         }
         // Print to stream "No user named \"" + targetUsr.getName() + "\" was found."
         return;
      }

      targetUsr.exp += amount;
      JIRC.sendMessage(Globals.channel, "/me " + targetUsr.name + " has been given " + amount + " xp point(s)");
   }

   private void awardFactionXP(final Faction faction, final int amount)
   {
      new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            for(User usr : Globals.users.values())
            {
               if(usr.faction == faction)
                  usr.exp += amount;
            }
            JIRC.sendMessage(Globals.channel, "/me All members of " + faction.getName() + " have been given " + amount + " xp point(s)");
         }
      }).start();
   }

   @Override
   public String getUsage()
   {
      return "!addxp <user/faction> <amount>";
   }
}
