package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;
import yeti.bot.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Z on 1/15/2015.
 */
public class CmdDuel extends Command
{
   private HashMap<String, ArrayList<DuelInfo>> duels = new HashMap<>();

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
      User usr = Globals.getOnlineUser(user);
      return isEnabled() && Globals.duelEnabled && usr != null && (cmd.startsWith("!duel") || cmd.startsWith("!accept") || cmd.startsWith("!decline"));
   }

   @Override
   public void process(String user, String msg)
   {
      if (msg.startsWith("!duel"))
      {
         Logger.logDebug("DUEL " + msg);
         String[] parts = msg.split(" ");

         if (parts.length < 2)
            return;

         User challenger = Globals.getOnlineUser(user),
               opponent = Globals.getOnlineUser(parts[1]);

         if (challenger == null || opponent == null)
         {
            Logger.logDebug("DUEL user not found");
            return;
         }

         if(challenger == opponent || opponent.getName() == Globals.username)
            return;

         int amount;
         try
         {
            if(parts.length >= 3)
               amount = Integer.parseInt(parts[2]);
            else
               amount = 0;
         } catch (NumberFormatException exc)
         {
            exc.printStackTrace();
            Logger.logDebug("DUEL incorrect number format");
            return;
         }

         if(amount < 0)
            return;

         if (challenger.getExp() < amount)
         {
            JIRC.sendMessage(Globals.channel, "You don't have enough xp for that wager " + challenger.getName() + "!");
            return;
         } else if (opponent.getExp() < amount)
         {
            JIRC.sendMessage(Globals.channel, opponent.getName() + " doesn't have enough xp for that wager, " + challenger.getName() + ".");
            return;
         }

         ArrayList<DuelInfo> infoList = duels.get(opponent.getName());
         if(infoList == null)
         {
            infoList = new ArrayList<>();
            duels.put(opponent.getName(), infoList);
         }
         infoList.add(new DuelInfo(challenger, opponent, amount, System.currentTimeMillis()));
         JIRC.sendMessage(Globals.channel, "/me " + challenger.getName() + " has challenged " + opponent.getName() + " to a duel for " + amount + " xp! " + opponent.getName() + ", use !accept or !decline to answer the challenge.");
      } else if (msg.startsWith("!accept"))
      {
         User usr = Globals.getOnlineUser(user);

         if (usr == null)
            return;

         ArrayList<DuelInfo> infoList = duels.get(usr.getName());
         if(infoList != null)
         {
            DuelInfo info = infoList.remove(0);
            if(infoList.isEmpty())
               duels.remove(usr.getName());

            int challengerRoll = Util.rollDie(20), opponentRoll = Util.rollDie(20);

            JIRC.sendMessage(Globals.channel, "/me " + info.opponent.getName() + " has accepted " + info.challenger.getName() + "'s challenge! A vicious battle ensues.");

            StringBuilder bldr = new StringBuilder("/me ");
            bldr.append(info.challenger.getName()).append(" rolls a ");
            if (challengerRoll == 20)
               bldr.append("koolCRIT");
            else if (challengerRoll == 1)
               bldr.append("koolFAIL");
            else
               bldr.append(challengerRoll);
            bldr.append(", ");

            bldr.append(info.opponent.getName()).append(" rolls a ");
            if (opponentRoll == 20)
               bldr.append("koolCRIT");
            else if (opponentRoll == 1)
               bldr.append("koolFAIL");
            else
               bldr.append(opponentRoll);
            bldr.append(". ");

            if (challengerRoll > opponentRoll)
            {
               bldr.append(info.challenger.getName()).append(" wins ").append(info.xpAmount).append(" xp!");
               info.challenger.setExp(info.challenger.getExp() + info.xpAmount);
               info.opponent.setExp(info.opponent.getExp() - info.xpAmount);
            } else if (opponentRoll > challengerRoll)
            {
               bldr.append(info.opponent.getName()).append(" wins ").append(info.xpAmount).append(" xp!");
               info.opponent.setExp(info.opponent.getExp() + info.xpAmount);
               info.challenger.setExp(info.challenger.getExp() - info.xpAmount);
            } else
            {
               bldr.append("It's a tie!");
            }

            new Thread(() -> {
               Thread.yield();
               try
               {
                  Thread.sleep(5000);
               } catch (InterruptedException e)
               {
                  e.printStackTrace();
               }
               JIRC.sendMessage(Globals.channel, bldr.toString());
            }).start();
         }
      } else if (msg.startsWith("!decline"))
      {
         User usr = Globals.getOnlineUser(user);

         if (usr == null)
            return;

         ArrayList<DuelInfo> infoList = duels.get(usr.getName());
         if(infoList != null)
         {
            DuelInfo info = infoList.remove(0);
            if(infoList.isEmpty())
               duels.remove(usr.getName());

            JIRC.sendMessage(Globals.channel, info.opponent.getName() + " has declined " + info.challenger.getName() + "'s challenge.");
         }
      }
   }

   @Override
   public String getUsage()
   {
      return null;
   }
}
