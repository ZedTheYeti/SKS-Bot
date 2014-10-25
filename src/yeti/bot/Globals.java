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
package yeti.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import yeti.bot.cmds.*;
import yeti.bot.util.Logger;
import yeti.irc.IRCServer;

public class Globals
{
   public static HashMap<String, User> users     = new HashMap<String, User>();
   public static HashMap<String, User> offlineUsers   = new HashMap<String, User>();
   public static IRCServer                server;
   public static ArrayList<Command>    modCommands     = new ArrayList<Command>();
   public static ArrayList<Command>    subCommands     = new ArrayList<Command>();
   public static ArrayList<Command>       commands = new ArrayList<Command>();

   public static String               username, serverName, channel;
   public static int                  port;

   public static HashSet<String>      voted         = new HashSet<String>();
   public static Faction              votingFaction = Faction.COUNCIL;
   public static boolean              voting   = false;
   public static int                  yays     = 0, nays = 0;
   public static boolean              sneakUsed = false;
   public static boolean              damnUsed   = false;
   public static boolean              doubleUsed    = false;
   public static boolean              vetoUsed      = false;
   public static int                   draws           = 0;
   public static final long            VOTE_TIME       = 1 * 60 * 1000;

   public static boolean              trackXp       = false;
   public static long xpStartTime = 0;
   public static long xpTrackTime = 0;
   public static final float           XP_AWARD_AMOUNT = 0.08333333333333333333333333333333f;
   public static final long            XP_AWARD_TIME   = 5 * 60 * 1000;

   public static int msgCount = 0;

   static
   {
      reloadCommands();
   }

   public static void reloadCommands()
   {
      commands.clear();;

      commands.add(new CmdYay());
      commands.add(new CmdNay());
      commands.add(new CmdCards());

      subCommands.add(new CmdJoin());
      subCommands.add(new CmdPick());
      subCommands.add(new CmdStatus());
      subCommands.add(new CmdDecision());

      modCommands.add(new CmdRoll());
      modCommands.add(new CmdVote());
      modCommands.add(new CmdSneak());
      modCommands.add(new CmdDraw());
      modCommands.add(new CmdDouble());
      modCommands.add(new CmdDamn());
      modCommands.add(new CmdVeto());
      modCommands.add(new CmdResetPowers());
      modCommands.add(new CmdCommands());
      modCommands.add(new CmdInfo());
      modCommands.add(new CmdToggle());
   }

   public static User getOrCreateUser(String name)
   {
      User usr = users.get(name);
      if (usr == null)
      {
         usr = offlineUsers.get(name);
         if (usr == null)
         {
            Logger.logDebug("Creating new user named " + name);
            usr = new User(name);
            users.put(name, usr);
         } else
         {
            Logger.logDebug("Moving " + name + " from offline users to online users.");
            users.put(name, usr);
            offlineUsers.remove(name);
         }
      }
      return usr;
   }
}
