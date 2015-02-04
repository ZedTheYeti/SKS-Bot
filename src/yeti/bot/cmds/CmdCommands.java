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

import java.util.Timer;
import java.util.TimerTask;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

public class CmdCommands extends Command
{
   private static long           COOLDOWN = 3 * 60 * 1000;
   private boolean     cooling  = false;
   private Timer       timer    = new Timer();
   private TimerTask   task;

   public CmdCommands()
   {
      task = new TimerTask()
      {
         @Override
         public void run()
         {
            cooling = false;
         }
      };
   }

   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      User usr = Globals.users.get(user);
      return usr != null && usr.captain && cmd.startsWith("!commands");
   }

   @Override
   public void process(String user, String msg)
   {
      cooling = true;
      // timer.schedule(task, COOLDOWN);

      StringBuilder bldr = new StringBuilder("/me ");

      if(Globals.commands.size() > 0)
      {
         bldr.append("General Commands: ");
         for(int i = 0; i < Globals.commands.size(); i++)
         {
            bldr.append(Globals.commands.get(i).getUsage());
            if (i < Globals.commands.size() - 1)
               bldr.append(',').append(' ');
         }
      }

      if(Globals.subCommands.size() > 0)
      {
         bldr.append(" - Subscriber Commands: ");
         for(int i = 0; i < Globals.subCommands.size(); i++)
         {
            bldr.append(Globals.subCommands.get(i).getUsage());
            if (i < Globals.subCommands.size() - 1)
               bldr.append(',').append(' ');
         }
      }

      if(Globals.modCommands.size() > 0)
      {
         bldr.append(" - General Commands: ");
         for(int i = 0; i < Globals.modCommands.size(); i++)
         {
            bldr.append(Globals.modCommands.get(i).getUsage());
            if (i < Globals.modCommands.size() - 1)
               bldr.append(',').append(' ');
         }
      }

      JIRC.sendMessage(Globals.channel, bldr.toString());
   }

   @Override
   public String getUsage()
   {
      return "!commands";
   }
}
