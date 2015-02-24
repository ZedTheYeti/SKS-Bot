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

import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

import java.util.Timer;
import java.util.TimerTask;

public class CmdSneak extends Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      if (isEnabled() && !Globals.voting && !Globals.sneakUsed)
      {
         User sub = Globals.getOnlineUser(user);
         if (sub != null && sub.captain && cmd.startsWith("!sneakattack"))
            return true;
      }
      return false;
   }

   @Override
   public void process(String user, String msg)
   {
      Globals.votingFaction = Faction.GUILD;
      Globals.voting = true;
      Globals.yays = 0;
      Globals.nays = 0;
      Globals.voted.clear();

      JIRC.sendMessage(Globals.channel, "/me A vote to sneak attack Shawn and DP has started! Only members of the Guild for Wasted Breath may vote, !yay for yes and !nay for no. The vote will end in 2 minutes.");

      Timer timer = new Timer();
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            int total = Globals.yays + Globals.nays;

            String string = "/me The vote to sneak attack Shawn and DP has ended. The results are " + Globals.yays + " yays and " + Globals.nays + " nays. ";
            if (Globals.yays > 0 && (float) Globals.yays / total >= 0.6f)
            {
               string += "The yays have it! The Guild for Wasted Breath emerges from the shadows and strikes Shawn and DP. Make sure you let Shawn and DP know.";
               Globals.sneakUsed = true;
            } else if (Globals.yays != Globals.nays)
               string += "The nays have it. The Guild for Wasted Breath's attack misses, they retreat back into the shadows to plot their next move.";
            else
               string += "A tie! The Guild for Wasted Breath's attack barley misses, they retreat back into the shadows to plot their next move.";

            JIRC.sendMessage(Globals.channel, string);
            Globals.voting = false;
         }
      };
      timer.schedule(task, Globals.VOTE_TIME);

   }

   @Override
   public String getUsage()
   {
      return "!sneakattack";
   }
}
