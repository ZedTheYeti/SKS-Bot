package yeti.bot.cmds;

import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

import java.awt.*;
import java.util.HashSet;

/**
 * Created by Z on 7/26/2014.
 */
public class CmdCards extends Command{
   private final class Votes
   {
      public int card1 = 0;
      public int card2 = 0;

      public void clear()
      {
         card1 = card2 = 0;
      }
   }

   private boolean voting;
   private Votes guildVotes = new Votes(), schoolVotes = new Votes(), knightVotes = new Votes(), clanVotes = new Votes();
   private HashSet<String> voted = new HashSet<String>();

   public boolean check(String name, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(name);

      if(!isEnabled() || usr == null)
         return false;

      if (!voting)
         return usr.captain && cmd.startsWith("!cardvote");
      else
      {
         if(cmd.startsWith("!endvote"))
            return usr.captain;
         else if(cmd.startsWith("!card1") || cmd.startsWith("!card2"))
            return usr.faction != Faction.COUNCIL &&!voted.contains(name);
      }
      return false;
   }

   public void process(String name, String cmd)
   {
      if(cmd.startsWith("!cardvote"))
      {
         voting = true;
         guildVotes.clear();
         schoolVotes.clear();
         knightVotes.clear();
         clanVotes.clear();
         voted.clear();
         JIRC.sendMessage(Globals.channel, "/me A round of voting has started for Factions Against Humanity. Dan Paul will show each faction their cards, faction members may vote !card1 or !card2. All factions may vote at once and a mod will end the vote when Dan Paul wants the results.");
      }else if(cmd.startsWith("!endvote"))
      {
         voting = false;

         StringBuilder bldr = new StringBuilder("This round of Factions Against Humanity has ended. ");

         appendMsg(bldr, "Guild", guildVotes);
         appendMsg(bldr, "School", schoolVotes);
         appendMsg(bldr, "Knights", knightVotes);
         appendMsg(bldr, "Rockbiters", clanVotes);
         bldr.append("Make sure to let DP and Shawn known.");

         JIRC.sendMessage(Globals.channel, bldr.toString());
      }else
      {
         User usr = Globals.users.get(name);

         if(cmd.startsWith("!card1"))
         {
            switch(usr.faction)
            {
               case GUILD: guildVotes.card1++; break;
               case SCHOOL: schoolVotes.card1++; break;
               case KNIGHTS: knightVotes.card1++; break;
               case ROCKBITER: clanVotes.card1++; break;
            }
         }else
         {
            switch(usr.faction)
            {
               case GUILD: guildVotes.card2++; break;
               case SCHOOL: schoolVotes.card2++; break;
               case KNIGHTS: knightVotes.card2++; break;
               case ROCKBITER: clanVotes.card2++; break;
            }
         }
      }
   }

   private void appendMsg(StringBuilder bldr, String facName, Votes votes)
   {
      bldr.append("The ").append(facName);
      if(votes.card1 > votes.card2)
         bldr.append(" has choosen card 1. ");
      else if(votes.card2 > votes.card1)
         bldr.append(" has choosen card 2. ");
      else
         bldr.append(" could not decide on a card. ");
   }


   public String getUsage()
   {
      // Need a better way to do this..
       // !card1, !card2 will be excluded

      return "!cardvote, !endvote";
   }
}
