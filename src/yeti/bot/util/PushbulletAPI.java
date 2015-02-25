package yeti.bot.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushbulletAPI
{
   public static boolean allowPushes = true;
   public static int pushesPerMinute = 10;

   private static long start = 0;
   private static int pushes = 0;
   private static final String apiUrl = "https://api.pushbullet.com/v2/pushes";

   public static void sendPush(String auth, String title, String message)
   {
      if (!allowPushes)
         return;

      if ((auth == null || auth.isEmpty()) || title == null || message == null)
         return;

      if (start == 0)
         start = System.nanoTime();
      else
      {
         long time = System.nanoTime();
         if (time - start >= 60l * 1000000000l)
         {
            start = time;
            pushes = 0;
         } else if (pushes >= pushesPerMinute)
            return;
      }

      pushes++;

      try
      {
         URL url = new URL(apiUrl);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setDoOutput(true);
         connection.setInstanceFollowRedirects(false);
         connection.setRequestMethod("POST");
         connection.setRequestProperty("Content-Type", "application/json");
         connection.setRequestProperty("Authorization", "Bearer " + auth);

         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
         connection.connect();

         String json = "{\"type\": \"note\", \"title\": \"" + title + "\", \"body\": \"" + message + "\"}";
         out.write(json);
         out.flush();

         int response = connection.getResponseCode();
         if(response != 200)
            Logger.logError("Error sending pushbullet notification, non-200 HTTP Response code. Code: " + connection.getResponseCode());

         out.close();
      }catch(IOException ioe)
      {
         Logger.logError("Error sending Pushbullet notification. Message: " + ioe.getMessage());
         ioe.printStackTrace();
      }
   }
}
