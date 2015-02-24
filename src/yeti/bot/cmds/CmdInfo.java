package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

/**
 * Created by Z on 7/23/2014.
 */
public class CmdInfo extends Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User usr = Globals.getOnlineUser(user);
      return isEnabled() && usr != null && usr.captain && cmd.startsWith("!info");
   }

   public void process(String name, String cmd)
   {
      String[] parts = cmd.toLowerCase().split(" ");

      if (parts.length < 2)
         return;

      if (parts[1].contains("class"))
         JIRC.sendMessage(Globals.channel, "/me The age of adventurers has begun! Subscribers, choose a class today with !pick Rogue, !pick Fighter, !pick Ranger, !pick Cleric, !pick Alchemist or !pick Adept");
      else if (parts[1].contains("faction"))
         JIRC.sendMessage(Globals.channel, "/me Subscribers, if you haven't joined a faction then join one today! Use !join Guild, !join Knights, !join School, !join Rockbiters, or !join InsertFactionEmoteHere");
      else if (parts[1].contains("db"))
         JIRC.sendMessage(Globals.channel, "/me This is the first night that I'm testing the new database for Wally during a stream. Switching to the database involved a lot of code changes so there may be some bugs in the bot tonight, but it'll be worth it going forward. ZedTheYeti will be monitoring the bot, but let him know if you see an issue or if Wally isn't working. Thanks for bearing with me!");
   }

   public String getUsage()
   {
      return "!info subject";
   }
}
