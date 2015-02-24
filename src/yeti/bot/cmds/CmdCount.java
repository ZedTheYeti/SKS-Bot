package yeti.bot.cmds;

import yeti.bot.*;

import static yeti.bot.Globals.channel;

/**
 * Created by zed on 2/6/15.
 */
public class CmdCount extends Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User usr = Globals.getOnlineUser(user);
      return isEnabled() && usr != null && usr.captain && cmd.startsWith("!count");
   }

   @Override
   public void process(String user, String msg)
   {
      String parts[] = msg.split(" ");

      if (parts.length < 2)
         return;

      parts[1] = parts[1];

      if (parts[1].contains("faction"))
      {
         int guildies = 0, rockbiters = 0, knights = 0, students = 0, other = 0;

         for (User usr : Globals.getOnlineUsers())
         {
            if (!usr.inChannel)
               continue;

            Faction fac = usr.getFaction();
            switch (fac)
            {
               case GUILD:
                  guildies++;
                  break;
               case KNIGHTS:
                  knights++;
                  break;
               case ROCKBITER:
                  rockbiters++;
                  break;
               case SCHOOL:
                  students++;
                  break;
               default:
                  other++;
                  break;
            }
         }
         JIRC.sendMessage(channel, "Online Factions | koolBREATH Guildies: " + guildies + "   | koolCLAN Rockbiters: " + rockbiters + "   | koolKNIGHTS Knights: " + knights + "   | koolSchool Students: " + students + "   | Unaffiliated: " + other);
      } else if (parts[1].contains("class"))
      {
         int rouges = 0, fighters = 0, adepts = 0, rangers = 0, clerics = 0, other = 0;
         for (User usr : Globals.getOnlineUsers())
         {
            if (!usr.inChannel)
               continue;

            UserClass uClass = usr.getUserClass();
            switch (uClass)
            {
               case ROGUE:
                  rouges++;
                  break;
               case FIGHTER:
                  fighters++;
                  break;
               case ADEPT:
                  adepts++;
                  break;
               case RANGER:
                  rangers++;
                  break;
               case CLERIC:
                  clerics++;
                  break;
               default:
                  other++;
                  break;
            }
         }
         JIRC.sendMessage(channel, "Online Classes | Rogues: " + rouges + "   | Fighters: " + fighters + "   | Adepts: " + adepts + "   | Rangers: " + rangers + "  | Clerics: " + clerics + "  | Undecided: " + other);
      }
   }

   @Override
   public String getUsage()
   {
      return "!count <target>";
   }
}
