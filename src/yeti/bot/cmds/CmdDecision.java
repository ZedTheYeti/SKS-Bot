package yeti.bot.cmds;

import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Z on 7/28/2014.
 */
public class CmdDecision extends Command
{
   private boolean voting = false;
   private Faction votingFaction = Faction.ALL;
   private HashSet<String> voted = new HashSet<String>();
   private int[] votes;
   // It's kinda bad practice to have this as global, but it saves some time re-parsing the choice
   private int pick;

   public boolean check(String name, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(name);

      if (voting)
      {
         if (cmd.length() >= 2)
         {
            try
            {
               pick = Integer.parseInt(cmd.trim().substring(1));
            } catch (NumberFormatException nfe)
            {
               return false;
            }

            if (pick > 0 && pick <= votes.length)
            {
               if (votingFaction == Faction.ALL)
                  return isEnabled() && (isSub || (usr != null && usr.captain)) && !voted.contains(name);
               else
                  return isEnabled() && usr != null && usr.faction == votingFaction && !voted.contains(name);
            }
         }
      } else
      {
         return isEnabled() && usr != null && usr.captain && cmd.startsWith("!decision");
      }
      return false;
   }

   public void process(String name, String cmd)
   {
      if (voting)
      {
         // pick gets parsed in check(), it's a Global Var to save time re-parsing it here
         votes[pick - 1]++;
         Logger.logDebug(name + " voted for " + pick);
      } else
      {
         StringBuilder bldr = new StringBuilder("/me ");

         String[] parts = cmd.toLowerCase().split(" ");
         if (parts.length < 3)
            return;

         int choices = Integer.parseInt(parts[2]);
         if (choices <= 0)
            return;
         votes = new int[choices];

         votingFaction = Faction.getByName(parts[1]);
         if (votingFaction == Faction.NONE)
            return;

         if (votingFaction != Faction.ALL)
            bldr.append("The ");
         bldr.append(votingFaction.getName()).append(" has been presented with ").append(choices).append(" choices. Choose wisely! You may vote with ");
         for (int i = 1; i <= choices; i++)
         {
            bldr.append("!").append(i);
            if (i != choices)
               bldr.append(", ");
         }

         voted.clear();
         voting = true;
         JIRC.sendMessage(Globals.channel, bldr.toString());

         Timer timer = new Timer();
         TimerTask task = new TimerTask()
         {
            @Override
            public void run()
            {
               ArrayList<Integer> winners = new ArrayList<>(votes.length);

               Logger.logDebug("Votes: " + votes);

               StringBuilder bldr = new StringBuilder("/me The voting has ended. ");

               int highest = -1;
               for (int i = 0; i < votes.length; i++)
               {
                  if (votes[i] > highest)
                  {
                     highest = votes[i];
                     winners.clear();
                     winners.add(i);
                  } else if(votes[i] == highest)
                  {
                     winners.add(i);
                  }
               }

               if(winners.size() > 1)
               {
                  bldr.append(votingFaction.getName()).append(" have tied voting for ");
                  for (int i = 0; i < winners.size(); i++)
                  {
                     bldr.append(winners.get(i));
                     if(i != winners.size() - 1)
                        bldr.append(", ");
                  }
                  bldr.append('.');
               }else
               {
                  if(winners.isEmpty())
                     bldr.append(" No votes were cast.");
                  else
                     bldr.append(votingFaction.getName()).append(" have agreed upon choice ");
                     bldr.append(winners.get(0)).append('.');
               }

               JIRC.sendMessage(Globals.channel, bldr.toString());
               voting = false;
            }
         };
         timer.schedule(task, Globals.VOTE_TIME);
      }
   }

   public String getUsage()
   {
      return "!decision FactionName NumDecisions";
   }
}
