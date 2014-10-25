package yeti.bot;

/**
 * Created by Z on 7/23/2014.
 */
public enum UserClass {
   NEWB, ROGUE, FIGHTER, RANGER, ADEPT, CLERIC;

   public String name;
   public String[] levelNames;
   public static float[] levelThresholds;

   static
   {
      levelThresholds = new float[] {0, 50, 150, 300, 500};

      NEWB.name = "Newb";
      NEWB.levelNames = new String[] {"Noob", "N00b", "Newb", "Newbie", "Nub"};

      ROGUE.name = "Rogue";
      ROGUE.levelNames = new String[] {"Rogue", "Bandit", "Thief", "Duelist", "Assassin"};

      FIGHTER.name = "Fighter";
      FIGHTER.levelNames = new String[] {"Fighter", "Soldier", "Warrior", "Knight", "Warlord"};

      RANGER.name = "Ranger";
      RANGER.levelNames = new String[] {"Ranger", "Hunter", "Marksman", "Sniper", "Deadshot"};

      ADEPT.name = "Adept";
      ADEPT.levelNames = new String[] {"Adept", "Illusionist", "Mage", "Sorcerer", "Grand Wizard"};

      CLERIC.name = "Cleric";
      CLERIC.levelNames = new String[] {"Cleric", "Priest", "Inquisitor", "Enlightened", "Transcendant"};
   }

   public String getLevelName(float xp)
   {
      for(int i = levelThresholds.length - 1; i >= 0; i--)
         if(xp >= levelThresholds[i])
            return levelNames[i];
      return levelNames[0];
   }

   public String getName()
   {
      return name;
   }
}
