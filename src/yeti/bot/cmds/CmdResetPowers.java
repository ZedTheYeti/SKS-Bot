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

public class CmdResetPowers extends Command
{
   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {

      User sub = Globals.users.get(user);
      if (sub != null && sub.captain && cmd.startsWith("!resetpowers"))
         return true;
      return false;
   }

   @Override
   public void process(String user, String msg)
   {
      Globals.sneakUsed = false;
      Globals.damnUsed = false;
      Globals.doubleUsed = false;
      Globals.vetoUsed = false;
      Globals.draws = 0;
      JIRC.sendMessage(Globals.channel, "/me All powers have been reset, they may be used again.");
   }

   @Override
   public String getUsage()
   {
      return "!resetpowers";
   }
}
