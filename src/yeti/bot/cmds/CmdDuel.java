package yeti.bot.cmds;

import jdk.nashorn.internal.objects.Global;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;
import yeti.bot.util.Util;

import java.util.HashMap;

/**
 * Created by Z on 1/15/2015.
 */
public class CmdDuel extends Command
{
   private HashMap<String, DuelInfo> duels = new HashMap<String, DuelInfo>();

   class DuelInfo
   {
      protected User challenger, opponent;
      protected int xpAmount;
      protected long time;

      public DuelInfo(User challenger, User opponent, int xpAmount, long time)
      {
         this.challenger = challenger;
         this.opponent = opponent;
         this.xpAmount = xpAmount;
         this.time = time;
      }
   }

   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(user);
      return isEnabled() && usr != null && (cmd.startsWith("!duel") || cmd.startsWith("!accept") || cmd.startsWith("!decline"));
   }

   @Override
   public void process(String user, String msg)
   {
      if(msg.startsWith("!duel"))
      {
         Logger.logDebug("DUEL " + msg);
         String[] parts = msg.split(" ");

         if (parts.length < 3)
            return;

         User challenger = Globals.users.get(user),
               opponent = Globals.users.get(parts[1].toLowerCase());

         if (challenger == null || opponent == null)
         {
            Logger.logDebug("DUEL user not found");
            return;
         }

         int amount;
         try
         {
            amount = Integer.parseInt(parts[2]);
         } catch (NumberFormatException exc)
         {
            exc.printStackTrace();
            Logger.logDebug("DUEL incorrect number format");
            return;
         }

         if (challenger.exp < amount)
         {
            JIRC.sendMessage(Globals.channel, "You don't have enough xp for that wager " + challenger.name + "!");
            return;
         } else if (opponent.exp < amount)
         {
            JIRC.sendMessage(Globals.channel, opponent.name + " doesn't have enough xp for that wager, " + challenger.name + ".");
            return;
         }

         duels.put(opponent.name, new DuelInfo(challenger, opponent, amount, System.currentTimeMillis()));
         JIRC.sendMessage(Globals.channel, "/me " + challenger.name + " has challenged " + opponent.name + " to a duel for " + amount + " xp! " + opponent.name + ", use !accept or !decline to answer the chalenge.");
      } else if (msg.startsWith("!accept"))
      {
         User usr = Globals.users.get(user);

         System.err.println("accept");

         if(usr == null)
            return;

         System.err.println("boop");

         DuelInfo info = duels.remove(usr.name);
         if(info != null)
         {
            System.err.println("bedoop");

            int challengerRoll = Util.rollDie(20), opponentRoll = Util.rollDie(20);

            JIRC.sendMessage(Globals.channel, "/me " + info.opponent.name + " has accepted " + info.challenger.name + "'s challenge! A vicious battle ensues.");

            StringBuilder bldr = new StringBuilder("/me ");
            bldr.append(info.challenger.name).append(" rolls a ");
            if(challengerRoll == 20)
               bldr.append("koolCRIT");
            else if(challengerRoll == 1)
               bldr.append("koolFAIL");
            else
               bldr.append(challengerRoll);
            bldr.append(", ");

            bldr.append(info.opponent.name).append(" rolls a ");
            if(opponentRoll == 20)
               bldr.append("koolCRIT");
            else if(opponentRoll == 1)
               bldr.append("koolFAIL");
            else
               bldr.append(opponentRoll);
            bldr.append(". ");

            if(challengerRoll > opponentRoll)
            {
               bldr.append(info.challenger.name).append(" wins ").append(info.xpAmount).append(" xp!");
               info.challenger.exp += info.xpAmount;
               info.opponent.exp -= info.xpAmount;
            } else if(opponentRoll > challengerRoll)
            {
               bldr.append(info.opponent.name).append(" wins ").append(info.xpAmount).append(" xp!");
               info.opponent.exp += info.xpAmount;
               info.challenger.exp -= info.xpAmount;
            }
            else
            {
               bldr.append("It's a tie!");
            }

            // Need to push this waiting off to a new thread
            // so it doesn't eat time on the main thread
            // Maybe a TimerTask
            Thread.yield();
            try
            {
               Thread.sleep(5000);
            } catch (InterruptedException e)
            {
               e.printStackTrace();
            }

            JIRC.sendMessage(Globals.channel, bldr.toString());
         }
      } else if(msg.startsWith("!decline"))
      {
         User usr = Globals.users.get(user);

         if(usr == null)
            return;

         DuelInfo info = duels.remove(usr);
         if(info != null)
            JIRC.sendMessage(Globals.channel, info.opponent + " has declined " + info.challenger + "'s challenge.");
      }
   }

   @Override
   public String getUsage()
   {
      return null;
   }
}
