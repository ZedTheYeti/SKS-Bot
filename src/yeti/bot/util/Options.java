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
package yeti.bot.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class Options
{
   private static final String s = Util.FILE_SEPARATOR;
   private static final String nl = Util.NEW_LINE;

   private File optionsFile;
   private HashMap<String, String> options = new HashMap<String, String>();

   public Options(File path)
   {
      optionsFile = path;
   }

   public Options(String path)
   {
      setPath(path);
   }

   public String get(String name)
   {
      return options.get(name);
   }

   public Set<Entry<String, String>> getAllOptions()
   {
      return options.entrySet();
   }

   public void set(String name, String value)
   {
      options.put(name, value);
   }

   public File getFile()
   {
      return optionsFile;
   }

   public String getPath()
   {
      return optionsFile.getPath();
   }

   public void setPath(String pathStr)
   {
      if (!pathStr.startsWith(s) && pathStr.indexOf(":" + s) != 1)
         optionsFile = new File(Util.DATA_FOLDER, pathStr);
      else
         optionsFile = new File(pathStr);

      if (!optionsFile.exists())
      {
         if (!optionsFile.getParentFile().exists())
            optionsFile.getParentFile().mkdirs();

         if (optionsFile.getParentFile().isDirectory())
         {
            Util.hideFile(optionsFile.getParentFile());

            try
            {
               optionsFile.createNewFile();
            } catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   public void load()
   {
      if (!optionsFile.exists()) try
      {
         optionsFile.createNewFile();
      } catch (IOException ioe)
      {
         Logger.logError("Options file does not exist, and one could not be created.", true);
         ioe.printStackTrace();
      }

      BufferedReader br = null;

      try
      {
         br = new BufferedReader(new FileReader(optionsFile));

         String line = null;
         while ((line = br.readLine()) != null)
         {
            line = line.trim();
            if (line.isEmpty())
               continue;

            int index = line.indexOf('=');
            if (index > 0)
            {
               String name = line.substring(0, index);
               String value = line.substring(index + 1);
               options.put(name, value);
            }
         }
      } catch (IOException ioe)
      {
         Logger.logError("An error has occurred while loading the options file.");
         ioe.printStackTrace();
      } finally
      {
         if (br != null)
            try
            {
               br.close();
            } catch (IOException e)
            {
               e.printStackTrace();
            }
      }
   }

   public void clear()
   {
      options.clear();
   }

   public void save()
   {
      if (!optionsFile.exists())
         try
         {
            optionsFile.createNewFile();
         } catch (IOException ioe)
         {
            Logger.logError("Options file does not exist, and one could not be created.", true);
            ioe.printStackTrace();
         }

      BufferedWriter out = null;

      try
      {
         out = new BufferedWriter(new FileWriter(optionsFile, false));

         for (Entry<String, String> entry : options.entrySet())
            out.write(entry.getKey() + "=" + entry.getValue() + nl);
      } catch (IOException ioe)
      {
         Logger.logError("An error has occurred while saving the options file.");
         ioe.printStackTrace();
      } finally
      {
         if (out != null)
            try
            {
               out.close();
            } catch (IOException e)
            {
               e.printStackTrace();
            }
      }
   }
}
