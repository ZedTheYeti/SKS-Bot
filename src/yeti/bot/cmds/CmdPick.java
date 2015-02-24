package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.UserClass;

import java.util.HashMap;

/**
 * Created by Z on 7/23/2014.
 */
public class CmdPick extends Command
{
   private static long COOLDOWN = 60 * 60 * 1000;
   private HashMap<String, Long> cooling = new HashMap<String, Long>();

   public boolean check(String user, String cmd, boolean isSub)
   {
      return isEnabled() && isSub && !cooling.containsKey(user) && cmd.startsWith("!pick");
   }

   @Override
   public void process(String name, String msg)
   {
      msg = msg.trim();
      int index = msg.indexOf(' ');
      if (index == -1)
         return;
      String userClass = msg.substring(index).toLowerCase();

      User user = Globals.getOnlineUser(name);

      // || user.userClass != UserClass.NEWB
      if (user == null)
         return;

      if (userClass.contains("rogue"))
         user.setUserClass(UserClass.ROGUE);
      else if (userClass.contains("fighter"))
         user.setUserClass(UserClass.FIGHTER);
      else if (userClass.contains("ranger"))
         user.setUserClass(UserClass.RANGER);
      else if (userClass.contains("adept"))
         user.setUserClass(UserClass.ADEPT);
      else if (userClass.contains("cleric"))
         user.setUserClass(UserClass.CLERIC);
      else if (userClass.contains("alchemist"))
         user.setUserClass(UserClass.ALCHEMIST);
      else
         return;

      cooling.put(user.getName(), System.currentTimeMillis());
      String str = "/me " + user.getName() + " has become a " + user.getUserClass().getName() + "!";

      JIRC.sendMessage("#sourkoolaidshow", str);
   }

   @Override
   public String getUsage()
   {
      return "!pick class";
   }
}
