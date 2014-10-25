package yeti.bot.util;

import javax.swing.*;

/**
 * Created by spence6 on 7/30/2014.
 */
public class Logger
{
   public static final int NONE = 0, ERROR = 1, DEBUG = 2, ALL = 3;
   private static int logLevel = NONE;

   public static void setLevel(int level)
   {
      logLevel = level;
   }

   public static void logMsg(String msg)
   {
      if(logLevel >= ALL)
         System.out.println("OUTPUT: " + msg);
   }

   public static void logDebug(String msg)
   {
      if(logLevel >= DEBUG)
         System.out.println("DEBUG:  " + msg);
   }

   public static void logError(String errorMsg)
   {
      if(logLevel >= ERROR)
         System.err.println("ERROR:  " + errorMsg);
   }

   public static void logError(String errorMsg, boolean showDialog)
   {
      if(logLevel >= ERROR)
      {
         if(showDialog)
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
         System.err.println("ERROR:  " + errorMsg);
      }
   }
}
