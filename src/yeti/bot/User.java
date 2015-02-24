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

import yeti.bot.util.Logger;

public class User
{
   private Faction faction;
   private UserClass userClass;
   private int level;
   private float exp;
   private String name;
   public boolean captain;
   public boolean isSub;
   public boolean inChannel = false;
   public boolean hasChanged = false;
   public boolean isInDb = false;
   public long joinTime;

   public User(String name)
   {
      this(name, Faction.COUNCIL, 0, 0);
   }

   public User(String name, Faction fac)
   {
      this(name, fac, 0, 0);
   }

   public User(String name, Faction fac, UserClass userClass)
   {
      this(name, fac, userClass, 0, 0);
   }

   public User(String name, Faction fac, int level, float exp)
   {
      this(name, fac, UserClass.NEWB, level, exp);
   }

   public User(String name, Faction fac, UserClass userClass, int level, float exp)
   {
      if (name.equalsIgnoreCase("zedtheyeti"))
      {
         captain = true;
         Logger.logDebug(name + " is a mod " + captain);
      }
      this.setName(name.substring(0, 1).toUpperCase() + name.substring(1));
      setFaction(fac);
      this.setUserClass(userClass);
      this.setLevel(level);
      this.setExp(exp);
      hasChanged = false;
   }

   public String getInfo()
   {
      StringBuilder str = new StringBuilder();

      str.append(getFaction()).append(',');
      str.append(getLevel()).append(',');
      str.append(getExp()).append(',');
      str.append(getUserClass());

      return str.toString();
   }

   @Override
   public String toString()
   {
      String str = getName() + " - ";
      if (captain)
         str += "Captain and ";
      if (getName().equalsIgnoreCase("zeldaslullaby"))
         str += "Dragon Demon Raider Person";
      else
         str += getUserClass().getLevelName(getExp());
      str += " of the " + getFaction().getName();

      switch (getFaction())
      {
         case GUILD:
            str += " koolBREATH";
            break;
         case KNIGHTS:
            str += " koolKNIGHTS";
            break;
         case ROCKBITER:
            str += " koolCLAN";
            break;
         case SCHOOL:
            str += " koolSchool";
            break;
      }

      str += " | Exp: " + (int) getExp();

      return str;
   }

   public Faction getFaction()
   {
      return faction;
   }

   public void setFaction(Faction faction)
   {
      hasChanged = hasChanged || (faction != this.faction);
      this.faction = faction;
   }

   public UserClass getUserClass()
   {
      return userClass;
   }

   public void setUserClass(UserClass userClass)
   {
      hasChanged = hasChanged || (userClass != this.userClass);
      this.userClass = userClass;
   }

   public int getLevel()
   {
      return level;
   }

   public void setLevel(int level)
   {
      hasChanged = hasChanged || (level != this.level);
      this.level = level;
   }

   public float getExp()
   {
      return exp;
   }

   public void setExp(float exp)
   {
      hasChanged = hasChanged || (exp != this.exp);
      this.exp = exp;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
}
