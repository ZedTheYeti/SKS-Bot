package yeti.bot.cmds;

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
      User usr = Globals.getOnlineUser(user);
      return isEnabled() && usr != null && (usr.captain || usr.getName().equalsIgnoreCase("sourkoolaidshow")) && (cmd.startsWith("!addxp"));
   }

   @Override
   public void process(String user, String msg)
   {
      String parts[] = msg.split(" ");

      if (parts.length < 3)
      {
         Logger.logDebug("Incorrect usage of !addxp: \"" + msg + "\"");
         return;
      }

      int amount = Integer.MIN_VALUE;
      try
      {
         amount = Integer.parseInt(parts[2]);
      } catch (NumberFormatException exc)
      {
         Logger.logDebug("Incorrect usage of !addxp: \"" + msg + "\"");
         return;
      }

      User targetUsr = Globals.getOnlineUser(parts[1]);
      if (targetUsr == null)
      {
         if (parts[1].equalsIgnoreCase("rockbitters"))
         {
            awardFactionXP(Faction.ROCKBITER, amount);
            return;
         } else if (parts[1].equalsIgnoreCase("school"))
         {
            awardFactionXP(Faction.SCHOOL, amount);
            return;
         } else if (parts[1].equalsIgnoreCase("knights"))
         {
            awardFactionXP(Faction.KNIGHTS, amount);
            return;
         } else if (parts[1].equalsIgnoreCase("guild"))
         {
            awardFactionXP(Faction.GUILD, amount);
            return;
         }
         // Print to stream "No user named \"" + targetUsr.getName() + "\" was found."
         return;
      }

      targetUsr.setExp(targetUsr.getExp() + amount);
      JIRC.sendMessage(Globals.channel, "/me " + targetUsr.getName() + " has been given " + amount + " xp point(s)");
   }

   private void awardFactionXP(final Faction faction, final int amount)
   {
      new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            for (User usr : Globals.getOnlineUsers())
            {
               if (usr.getFaction() == faction)
                  usr.setExp(usr.getExp() + amount);
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
