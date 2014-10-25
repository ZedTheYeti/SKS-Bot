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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;

public class CmdJoin implements Command
{
   private static long           COOLDOWN = 60 * 60 * 1000;
   private HashMap<String, Long> cooling  = new HashMap<String, Long>();

   public CmdJoin()
   {
      Timer timer = new Timer();
      TimerTask task = new TimerTask()
      {
         @Override
         public void run()
         {
            Iterator<String> it = cooling.keySet().iterator();
            while (it.hasNext())
            {
               String user = it.next();
               long value = cooling.get(user);
               if (System.currentTimeMillis() - value >= COOLDOWN)
                  it.remove();
            }
         }
      };
      timer.scheduleAtFixedRate(task, 10, 60 * 1000);
   }

   @Override
   public boolean check(String user, String cmd, boolean isSub)
   {
      return isSub && !cooling.containsKey(user) && cmd.startsWith("!join");
   }

   @Override
   public void process(String user, String msg)
   {
      msg = msg.trim();
      int index = msg.indexOf(' ');
      if (index == -1)
         return;
      String faction = msg.substring(index).toLowerCase();

      User sub = Globals.users.get(user);

      if (faction.contains("guild") || faction.contains("koolbreath"))
         sub.faction = Faction.GUILD;
      else if (faction.contains("knights") || faction.contains("koolknights"))
         sub.faction = Faction.KNIGHTS;
      else if (faction.contains("school") || faction.contains("koolschool"))
         sub.faction = Faction.SCHOOL;
      else if (faction.contains("rockbiter") || faction.contains("koolclan"))
         sub.faction = Faction.ROCKBITER;
      else
         return;

      cooling.put(sub.name, System.currentTimeMillis());
      String str = "/me " + sub.name + " has joined the " + sub.faction.getName() + "!";
      if (sub.faction == Faction.GUILD)
         str += " koolBREATH Make sure to change the color of your name to green.";
      else if (sub.faction == Faction.KNIGHTS)
         str += " koolKNIGHTS Make sure to change the color of your name to blue.";
      else if (sub.faction == Faction.ROCKBITER)
         str += " koolClan Make sure to change the color of your name to yellow.";
      else if (sub.faction == Faction.SCHOOL)
         str += " koolSchool Make sure to change the color of your name to red.";

      JIRC.sendMessage("#sourkoolaidshow", str);
   }

   @Override
   public String getUsage()
   {
      return "!join faction";
   }
}
