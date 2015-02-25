package yeti.bot.util;

import yeti.bot.Faction;
import yeti.bot.Globals;
import yeti.bot.User;
import yeti.bot.UserClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Z on 2/17/2015.
 */
public class MySQL
{
   // Cheap way of obfuscating the database settings from Github
   private static final String url = MySQLSettings.url;
   private static final String dbName = MySQLSettings.dbName;
   private static final String tableName = MySQLSettings.tableName;
   private static final String driver = MySQLSettings.driver;
   private static final String username = MySQLSettings.username;
   private static String password;
   private static final boolean sqlEnabled;

   static
   {
      Object obj = null;
      try
      {
         obj = Class.forName(driver).newInstance();
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
      {
         Logger.logError("Error creating instance of " + driver + ". MySQL functionality has been disabled.");
         e.printStackTrace();
      }
      sqlEnabled = (obj != null);
   }

   public static void setPassword(String pwd)
   {
      password = pwd;
   }

   public static void insertUser(User user, Connection connection)
   {
      if(user.getExp() == 0.0)
         return;

      try
      {
         StringBuilder strBldr = new StringBuilder("INSERT INTO ");
         strBldr.append(tableName).append(" (username, faction, level, exp, class) VALUES ('");
         strBldr.append(user.getName()).append("', '");
         strBldr.append(user.getFaction().name()).append("', '");
         strBldr.append(user.getLevel()).append("', '");
         strBldr.append(user.getExp()).append("', '");
         strBldr.append(user.getUserClass().name()).append("');");

         Statement st = connection.createStatement();
         int val = st.executeUpdate(strBldr.toString());

         if (val != 1)
            Logger.logError("Failed to insert " + user.getName() + " into the database.");
         else
            user.isInDb = true;
      }catch(SQLException sqle)
      {
         Logger.logError("Error inserting user named \"" + user.getName() + "\" into the database.");
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", "An error occurred inserting a user into the database.");
         sqle.printStackTrace();
      }
   }

   public static void updateUser(User user, Connection connection)
   {
      try
      {
         // TODO Test using LOWPRIORITY to speed up
         // TODO Finish implementing DB code, sync with server every 30 minutes to an hour, save locally as normal
         PreparedStatement st = connection.prepareCall("UPDATE innodb.users SET faction = ?, level = ?, exp = ?, class = ? WHERE username = ?");
         st.setString(1, user.getFaction().name());
         st.setInt(2, user.getLevel());
         st.setFloat(3, user.getExp());
         st.setString(4, user.getUserClass().name());
         st.setString(5, user.getName());
         int val = st.executeUpdate();

         if(val != 1)
            Logger.logError("Failed to update user named \"" + user.getName() + "\"");
      }catch(SQLException sqle)
      {
         Logger.logError("Error updating user named \"" + user.getName() + "\" in the database.");
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", "An error occurred updating a user in the database.");
         sqle.printStackTrace();
      }
   }

   public static void pushAllUsers()
   {
      if(!sqlEnabled)
         return;

      Logger.logDebug("Connecting to MySQL database...");

      try (Connection connection = DriverManager.getConnection(url + dbName, username, password))
      {
         Logger.logDebug("Successfully connected to MySQL database.");

         Logger.logDebug("Inserting users into the MySQL database...");

         Iterator<User> it = Globals.getOfflineUsers().iterator();
         while(it.hasNext())
            insertUser(it.next(), connection);

         it = Globals.getOnlineUsers().iterator();
         while(it.hasNext())
            insertUser(it.next(), connection);

         Logger.logDebug("Done inserting users into the MySQL database.");
      }
      catch (SQLException e)
      {
         Logger.logError("Error connecting to MySQL database!");
         e.printStackTrace();
      }
   }

   public static void loadUsers(HashMap<String, User> users)
   {
      Logger.logDebug("Loading users from the database...");
      try (Connection connection = DriverManager.getConnection(url + dbName, username, password))
      {
         Statement st = connection.createStatement();
         ResultSet results;

         results = st.executeQuery("SELECT COUNT(*) FROM innodb.users");
         if(!results.next())
            return;

         int chunkSize = 1000;
         int rows = results.getInt(1);
         Logger.logDebug("Detected " + rows +" rows");

         int offset = 0, numLoaded = 0;

         do
         {
            st = connection.createStatement();
            results = st.executeQuery("SELECT * FROM innodb.users LIMIT " + offset + ", " + chunkSize);

            while(results.next())
            {
               User usr = new User(results.getString("username").toLowerCase(), Faction.getByName(results.getString("faction")), UserClass.getByName(results.getString("class")), results.getInt("level"), results.getFloat("exp"));
               usr.isInDb = true;
               users.put(usr.getName().toLowerCase(), usr);
               numLoaded++;
            }

            offset += chunkSize;
            if(chunkSize > rows - offset)
               chunkSize = rows - offset;
         }while(numLoaded < rows);
         Logger.logDebug("Loaded " + numLoaded + " users from the database.");
      }
      catch (SQLException e)
      {
         Logger.logError("An error occurred while loading users from the database.");
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", "An error occurred while loading users from the database.");
         e.printStackTrace();
      }
   }

   public static void compareUsers()
   {
      HashMap<String, User> dbUsers = new HashMap<>();
      loadUsers(dbUsers);

      try (Connection connection = DriverManager.getConnection(url + dbName, username, password))
      {
         int inserted = 0, updated =0;

         Logger.logDebug("Comparing local user info to database info...");
         for (User user : Globals.getOfflineUsers())
         {
            User dbUser = dbUsers.get(user.getName().toLowerCase());
            if (dbUser == null)
            {
               insertUser(user, connection);
               inserted++;
            }
            else if(user.getExp() > dbUser.getExp() || user.getFaction() != dbUser.getFaction() || user.getUserClass() != dbUser.getUserClass())
            {
               Logger.logDebug(user.getInfo() + " vs " + dbUser.getInfo());
               Logger.logDebug("Updating user " + user.getName() + " in the database");
               updateUser(user, connection);
               updated++;
            }
         }

         Logger.logDebug("Updated " + updated + " users and added " + inserted + " users.");
      }catch(SQLException sqle)
      {
         Logger.logError("An error occurred while comparing users from the database.");
         sqle.printStackTrace();
      }
   }

   public static void updateUsers()
   {
      if(!sqlEnabled)
         return;

      try (Connection connection = DriverManager.getConnection(url + dbName, username, password))
      {
         ArrayList<User> users = Globals.getChangedUsers();
         Logger.logDebug("Found " + users.size() + " users to update in the database.");
         for(User user : users)
         {
            if(!user.isInDb)
            {
               user.hasChanged = false;
               insertUser(user, connection);
            }
            else if(user.hasChanged)
            {
               user.hasChanged = false;
               updateUser(user, connection);
            }
         }
         users.clear();
      }
      catch (SQLException e)
      {
         Logger.logError("Error connecting to MySQL database to update users!");
         PushbulletAPI.sendPush(Globals.pushbulletKey, "WallyBot Error", "Error connecting to MySQL database to update users!");
         e.printStackTrace();
      }
   }
}
