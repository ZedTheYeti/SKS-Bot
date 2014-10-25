package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

/**
 * Created by Z on 7/23/2014.
 */
public class CmdInfo implements Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(user);
      return usr != null && usr.captain && cmd.startsWith("!info");
   }

   public void process(String name, String cmd)
   {
      String[] parts = cmd.toLowerCase().split(" ");

      if(parts.length < 2)
         return;

      if(parts[1].contains("class"))
         JIRC.sendMessage(Globals.channel, "/me The age of adventurers has begun! Subscribers, choose a class today with !pick Rogue, !pick Fighter, !pick Ranger, !pick Cleric or !pick Adept");
      else if(parts[1].contains("faction"))
         JIRC.sendMessage(Globals.channel, "/me Subscribers, if you haven't joined a faction then join one today! Use !join Guild, !join Knights, !join School, !join Rockbiters, or !join InsertFactionEmoteHere");
   }

   public String getUsage()
   {
      return "!info subject";
   }
}
