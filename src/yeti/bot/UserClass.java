package yeti.bot;

/**
 * Created by Z on 7/23/2014.
 */
public enum UserClass
{
   NEWB, ROGUE, FIGHTER, RANGER, ADEPT, CLERIC, ALCHEMIST;

   public String name;
   public String[] levelNames;
   public static float[] levelThresholds;

   static
   {
      levelThresholds = new float[]{0, 50, 150, 300, 500};

      NEWB.name = "Newb";
      NEWB.levelNames = new String[]{"Noob", "N00b", "Newb", "Newbie", "Nub"};

      ROGUE.name = "Rogue";
      ROGUE.levelNames = new String[]{"Rogue", "Bandit", "Thief", "Duelist", "Assassin"};

      FIGHTER.name = "Fighter";
      FIGHTER.levelNames = new String[]{"Fighter", "Soldier", "Warrior", "Knight", "Warlord"};

      RANGER.name = "Ranger";
      RANGER.levelNames = new String[]{"Ranger", "Hunter", "Marksman", "Sniper", "Deadshot"};

      ADEPT.name = "Adept";
      ADEPT.levelNames = new String[]{"Adept", "Illusionist", "Mage", "Sorcerer", "Great Wizard"};

      CLERIC.name = "Cleric";
      CLERIC.levelNames = new String[]{"Acolyte", "Cleric", "Prophet", "Exalted", "Transcendant"};

      ALCHEMIST.name = "Alchemist";
      ALCHEMIST.levelNames = new String[]{"Herbalist", "Technician", "Chemist", "Thaumaturge", "Alchemist"};
   }

   public static UserClass getByName(String facName)
   {
      switch(facName.toLowerCase())
      {
         case "rogue": return ROGUE;
         case "fighter": return FIGHTER;
         case "ranger": return RANGER;
         case "adept": return ADEPT;
         case "cleric": return CLERIC;
         case "alchemist": return ALCHEMIST;
         default: return NEWB;
      }
   }

   public String getLevelName(float xp)
   {
      for (int i = levelThresholds.length - 1; i >= 0; i--)
         if (xp >= levelThresholds[i])
            return levelNames[i];
      return levelNames[0];
   }

   public String getName()
   {
      return name;
   }
}
