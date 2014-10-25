package yeti.bot.cmds;

import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Z on 7/24/2014.
 */
public class CmdVote implements Command
{
   public boolean check(String user, String cmd, boolean isSub)
   {
      if (!Globals.voting)
      {
         User sub = Globals.users.get(user);
         if (sub != null && sub.captain && cmd.startsWith("!vote"))
            return true;
      }
      return false;
   }

   public void process(String name, String cmd)
   {
      String[] parts = cmd.toLowerCase().split(" ");

      cmd = cmd.trim();
      int index = cmd.indexOf(' ');
      if (index == -1)
         return;

      Faction fac = Faction.COUNCIL;
      String faction = cmd.substring(index).toLowerCase();

      if (faction.contains("guild") || faction.contains("koolbreath"))
         fac = Faction.GUILD;
      else if (faction.contains("knights") || faction.contains("koolknights"))
         fac = Faction.KNIGHTS;
      else if (faction.contains("school") || faction.contains("koolschool"))
         fac = Faction.SCHOOL;
      else if (faction.contains("rockbiter") || faction.contains("koolclan"))
         fac = Faction.ROCKBITER;
      else if (faction.contains("council"))
         fac = Faction.COUNCIL;
      else
         return;

      Globals.votingFaction = fac;
      Globals.yays = 0;
      Globals.nays = 0;
      Globals.voting = true;
      Globals.voted.clear();

      String msg = "/me A vote has been started for " + fac.getName() + ", members vote !yay or !nay";
      JIRC.sendMessage(Globals.channel, msg);
      Timer timer = new Timer();
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            int total = Globals.yays + Globals.nays;

            String string = "/me The vote has ended. The results are " + Globals.yays + " yays and " + Globals.nays + " nays. ";

            JIRC.sendMessage(Globals.channel, string);
            Globals.voting = false;
         }
      };
      timer.schedule(task, Globals.VOTE_TIME);
   }

   public String getUsage()
   {
      return "!vote faction";
   }
}
