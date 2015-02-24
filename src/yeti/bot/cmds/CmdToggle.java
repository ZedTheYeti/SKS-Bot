/* Copyright (c) 2014-Onwards, Yeti Games
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list
 *   of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice, this list
 *   of conditions and the following disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * * Neither the name Yeti Games nor the names of its contributors may be used to
 *   endorse or promote products derived from this software without specific prior written
 *   permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package yeti.bot.cmds;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;

public class CmdToggle extends Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User sub = Globals.getOnlineUser(user);
      return isEnabled() && sub != null && (cmd.startsWith("!toggle") || cmd.startsWith("!enable") || cmd.startsWith("!disable"));
   }

   @Override
   public void process(String user, String msg)
   {
      String[] parts = msg.split(" ");

      if (parts.length < 2)
         return;

      if (parts[1].equalsIgnoreCase("xp"))
      {
         if (parts[0].startsWith("!toggle"))
         {
            Globals.trackXp = !Globals.trackXp;
            String str = "/me XP tracking has been turned ";
            if (Globals.trackXp)
               str += "on.";
            else
               str += "off.";

            long time = System.currentTimeMillis();
            for (User usr : Globals.getOnlineUsers())
               usr.joinTime = time;

            JIRC.sendMessage(Globals.channel, str);
         } else if (parts[0].startsWith("!enable"))
         {
            if (!Globals.trackXp)
            {
               Logger.logDebug("Resetting join times");
               long time = System.currentTimeMillis();
               for (User usr : Globals.getOnlineUsers())
                  usr.joinTime = time;
            }

            Globals.trackXp = true;
            String str = "/me XP tracking has been turned on.";

            if (parts.length >= 3)
            {
               try
               {
                  int time = Integer.parseInt(parts[2]);
                  Globals.xpStartTime = System.currentTimeMillis();
                  Globals.xpTrackTime = time * 60 * 60 * 1000;
                  str += " XP tracking will be turned off in " + time + " hour(s).";
               } catch (NumberFormatException nfe)
               {
               }
            }

            JIRC.sendMessage(Globals.channel, str);
         } else if (parts[0].startsWith("!disable"))
         {
            Globals.trackXp = false;
            JIRC.sendMessage(Globals.channel, "/me XP tracking has been turned off.");
         }
      } else if (parts[1].equalsIgnoreCase("status"))
      {
         if (parts[0].startsWith("!toggle"))
            Globals.statusEnabled = !Globals.statusEnabled;
         else if (parts[0].startsWith("!enable"))
            Globals.statusEnabled = true;
         else if (parts[0].startsWith("!disable"))
            Globals.statusEnabled = false;

         String str = "/me The !status command has been turned ";
         if (Globals.statusEnabled)
            str += " on.";
         else
            str += " off.";

         JIRC.sendMessage(Globals.channel, str);
      } else if (parts[1].equalsIgnoreCase("duel"))
      {
         if (parts[0].startsWith("!toggle"))
            Globals.duelEnabled = !Globals.duelEnabled;
         else if (parts[0].startsWith("!enable"))
            Globals.duelEnabled = true;
         else if (parts[0].startsWith("!disable"))
            Globals.duelEnabled = false;

         String str = "/me The !duel command has been turned ";
         if (Globals.duelEnabled)
            str += " on.";
         else
            str += " off.";

         JIRC.sendMessage(Globals.channel, str);
      }
   }

   @Override
   public String getUsage()
   {
      return "!toggle command";
   }
}
