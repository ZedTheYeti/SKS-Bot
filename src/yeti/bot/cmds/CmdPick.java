package yeti.bot.cmds;

import yeti.bot.*;

import java.util.HashMap;

/**
 * Created by Z on 7/23/2014.
 */
public class CmdPick implements Command
{
   private static long           COOLDOWN = 60 * 60 * 1000;
   private HashMap<String, Long> cooling  = new HashMap<String, Long>();

   public boolean check(String user, String cmd, boolean isSub)
   {
      return isSub && !cooling.containsKey(user) && cmd.startsWith("!pick");
   }

   @Override
   public void process(String name, String msg)
   {
      msg = msg.trim();
      int index = msg.indexOf(' ');
      if (index == -1)
         return;
      String userClass = msg.substring(index).toLowerCase();

      User user = Globals.users.get(name);

      // || user.userClass != UserClass.NEWB
      if(user == null)
         return;

      if (userClass.contains("rogue"))
         user.userClass = UserClass.ROGUE;
      else if (userClass.contains("fighter"))
         user.userClass = UserClass.FIGHTER;
      else if (userClass.contains("ranger"))
         user.userClass = UserClass.RANGER;
      else if (userClass.contains("adept"))
         user.userClass = UserClass.ADEPT;
      else if (userClass.contains("cleric"))
         user.userClass = UserClass.CLERIC;
      else if (userClass.contains("alchemist"))
         user.userClass = UserClass.ALCHEMIST;
      else
         return;

      cooling.put(user.name, System.currentTimeMillis());
      String str = "/me " + user.name + " has become a " + user.userClass.getName() + "!";

      JIRC.sendMessage("#sourkoolaidshow", str);
   }

   @Override
   public String getUsage()
   {
      return "!pick class";
   }
}
