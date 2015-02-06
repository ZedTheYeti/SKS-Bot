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

public enum Faction
{
   COUNCIL, GUILD, SCHOOL, KNIGHTS, ROCKBITER, ALL, NONE;

   private String name;

   static
   {
      COUNCIL.name = "Council";
      GUILD.name = "Guild for Wasted Breath";
      SCHOOL.name = "School of Bloodcraft";
      ROCKBITER.name = "Rockbiter Clan";
      KNIGHTS.name = "Knights of Dragonhaven";
      ALL.name = "Everyone";
      NONE.name = "Nobody";
   }

   public static Faction getByName(String facName)
   {
      if (facName.contains("all"))
         return Faction.ALL;
      else if (facName.contains("guild") || facName.contains("koolbreath"))
         return Faction.GUILD;
      else if (facName.contains("knights") || facName.contains("koolknights"))
         return Faction.KNIGHTS;
      else if (facName.contains("school") || facName.contains("koolschool"))
         return Faction.SCHOOL;
      else if (facName.contains("rockbiter") || facName.contains("koolclan"))
         return Faction.ROCKBITER;
      else if (facName.contains("council"))
         return Faction.COUNCIL;
      else
         return NONE;
   }

   public String getName()
   {
      return name;
   }

   @Override
   public String toString()
   {
      return super.name();
   }
}
