package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;
import yeti.bot.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Z on 1/15/2015.
 */
public class CmdDuel extends Command
{
   private static final long DUEL_TIMEOUT = 2 * 60 * 1000; // 2 minutes in milliseconds

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

   public CmdDuel()
   {
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            for(ArrayList<DuelInfo> list : duels.values())
            {
               if(list == null)
                  continue;

               long currTime = System.currentTimeMillis();

               for(int i = 0; i < list.size(); i++)
               {
                  DuelInfo info = list.get(i);

                  if(currTime - info.time >= DUEL_TIMEOUT)
                  {
                     Logger.logDebug("Clearing duel between " + info.challenger.getName() + " and " + info.opponent.getName());
                     list.remove(i);
                     i--;
                  }
               }
            }
         }
      };
      Timer t = new Timer();
      // Wait DUEL_TIMEOUT, then execute every 30 seconds
      t.scheduleAtFixedRate(task, DUEL_TIMEOUT, 30 * 1000);
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
      User usr = Globals.getOnlineUser(user);
      String[] parts = msg.split(" ");

      if(parts.length == 0 || usr == null)
         return;

      parts[0] = parts[0].trim().toLowerCase();

      switch(parts[0])
      {
         case "!duel": processDuel(usr, parts); break;
         case "!accept": processAnswer(usr, true, parts); break;
         case "!decline": processAnswer(usr, false, parts); break;
      }
   }

   private void processDuel(User user, String[] parts)
   {
      if (parts.length < 2)
         return;

      User challenger = user,
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
            amount = Math.max(0, Integer.parseInt(parts[2]));
         else
            amount = 0;
      } catch (NumberFormatException exc)
      {
         Logger.logDebug("DUEL incorrect number format, using 0xp");
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

      ArrayList<DuelInfo> infoList = duels.get(opponent.getName().toLowerCase());
      if(infoList == null)
      {
         infoList = new ArrayList<>();
         duels.put(opponent.getName().toLowerCase(), infoList);
      }
      infoList.add(new DuelInfo(challenger, opponent, amount, System.currentTimeMillis()));
      JIRC.sendMessage(Globals.channel, "/me " + challenger.getName() + " has challenged " + opponent.getName() + " to a duel for " + amount + " xp! " + opponent.getName() + ", use !accept or !decline to answer the challenge.");
   }

   private void processAnswer(User usr, boolean accepted, String parts[])
   {
      if (usr == null)
         return;

      ArrayList<DuelInfo> infoList = duels.get(usr.getName().toLowerCase());
      if(infoList != null)
      {
         DuelInfo info = infoList.remove(0);
         if (infoList.isEmpty())
            duels.remove(usr.getName().toLowerCase());
         if (info == null || info.challenger == null || info.opponent == null)
         {
            Logger.logDebug("ERROR DUEL !accept/!decline: " + parts + "\n" + info + "\n" + info.challenger + "\n" + info.opponent);
            return;
         }

         if(accepted)
         {
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
         else
            JIRC.sendMessage(Globals.channel, info.opponent.getName() + " has declined " + info.challenger.getName() + "'s challenge.");
      }
   }

   @Override
   public String getUsage()
   {
      return null;
   }
}
