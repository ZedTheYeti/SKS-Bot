package yeti.bot.util;

import javax.swing.*;
import java.io.*;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by spence6 on 7/30/2014.
 */
public class Logger
{
   public static final int NONE = 0, ERROR = 1, DEBUG = 2, ALL = 3;
   private static boolean outRedirected = false;
   private static boolean errorRedirected = false;
   private static int logLevel = NONE;

   private static DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

   public static void redirectOutput()
   {
      if (!errorRedirected)
      {
         File errorLog = null;
         try
         {
            File codeLoc = new File(Logger.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            errorLog = new File(codeLoc.getParentFile(), "ErrorLog.txt");
            if (!errorLog.exists())
               errorLog.createNewFile();
            System.setErr(new PrintStream(new FileOutputStream(errorLog, true)));
            System.err.println();
            System.err.println("[" + getDateTime() + " -   MSG] " + "Program Start");
            errorRedirected = true;
         } catch (URISyntaxException e)
         {
            logError("Unable to create log file. Error Message:\n" + getStackTrace(e), true);
            e.printStackTrace();
         } catch (IOException ioe)
         {
            logError("Unable to create log file. Error Message:\n" + getStackTrace(ioe), true);
            ioe.printStackTrace();
         }
      }

      if (!outRedirected && logLevel >= DEBUG)
      {
         File outLog = null;
         try
         {
            File codeLoc = new File(Logger.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            outLog = new File(codeLoc.getParentFile(), "DebugLog.txt");
            if (!outLog.exists())
               outLog.createNewFile();
            System.setOut(new PrintStream(new FileOutputStream(outLog, true)));
            System.out.println();
            System.out.println("[" + getDateTime() + " -   MSG] " + "Program Start");
            outRedirected = true;
         } catch (URISyntaxException e)
         {
            logError("Unable to create log file. Error Message:\n" + getStackTrace(e), true);
            e.printStackTrace();
         } catch (IOException ioe)
         {
            logError("Unable to create log file. Error Message:\n" + getStackTrace(ioe), true);
            ioe.printStackTrace();
         }
      }
   }

   public static void setLevel(int level)
   {
      logLevel = level;
   }

   public static void logMsg(String msg)
   {
      if (logLevel >= ALL)
         System.out.println("[" + getDateTime() + " -   MSG] " + msg);
   }

   public static void logDebug(String msg)
   {
      if (logLevel >= DEBUG)
         System.out.println("[" + getDateTime() + " - DEBUG] " + msg);
   }

   public static void logError(String errorMsg)
   {
      if (logLevel >= ERROR)
         System.err.println("[" + getDateTime() + " - ERROR] " + errorMsg);
   }

   public static void logError(String errorMsg, boolean showDialog)
   {
      if (logLevel >= ERROR)
      {
         if (showDialog)
            JOptionPane.showMessageDialog(null, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
         System.err.println("ERROR:  " + errorMsg);
      }
   }

   public static String getStackTrace(Throwable t)
   {
      StringWriter sw = new StringWriter();
      t.printStackTrace(new PrintWriter(sw));
      return sw.toString();
   }

   public static String getDateTime()
   {
      return format.format(Calendar.getInstance().getTime());
   }
}
