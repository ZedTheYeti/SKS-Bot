package yeti.bot.util;

import yeti.bot.Globals;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

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

public class Util
{
   public static final File DATA_FOLDER;
   public static final String FILE_SEPARATOR = System.getProperty("file.separator");
   public static final String NEW_LINE = System.getProperty("line.separator");
   private static final Random rand = new Random();

   static
   {
      String os = System.getProperty("os.name");
      String pathStr = System.getProperty("user.home");

      if (os.startsWith("Windows"))
      {
         if (os.contains("8") || os.contains("7") || os.contains("Vista"))
            pathStr += FILE_SEPARATOR + "AppData" + FILE_SEPARATOR + "Roaming" + FILE_SEPARATOR;
         else if (os.contains("XP"))
            pathStr += FILE_SEPARATOR + "Application Data" + FILE_SEPARATOR;
      } else if (os.startsWith("Mac"))
         pathStr += "/Library/Application Support/";

      DATA_FOLDER = new File(pathStr);

      if (!DATA_FOLDER.exists())
      {
         Logger.logError("The default data path does not exist, or cannot be accessed! Path: \"" + DATA_FOLDER.getPath() + "\"");
         Thread.dumpStack();
         System.exit(0);
      }
   }

   public static void restart()
   {
      // Don't restart more than 3 times in quick succession, if that happens something else is likely wrong
      if(Globals.numRestarts > 3)
      {
         Logger.logError("Too many restarts in a row, disabling restart.", true);
         return;
      }

      File currentExecutable;
      try
      {
         currentExecutable = new File(Util.class.getProtectionDomain().getCodeSource().getLocation().toURI());

         ArrayList<String> command = new ArrayList<>();

         if(currentExecutable.getName().endsWith(".jar"))
         {
            command.add("java");
            command.add("-jar");
            command.add(currentExecutable.getPath());
            command.add("-autostart");
            command.add("" + Globals.numRestarts);
         }
         else if(currentExecutable.getName().endsWith(".exe"))
         {
            command.add(currentExecutable.getPath());
            command.add("-autostart");
            command.add("" + Globals.numRestarts);
         }

         if(!command.isEmpty())
         {
            ProcessBuilder bldr = new ProcessBuilder(command);
            bldr.start();
            System.exit(0);
         }else
            Logger.logError("Could not restart, unsupported run type.");
      } catch (URISyntaxException e)
      {
         e.printStackTrace();
      } catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   public static int rollDie(int sides)
   {
      return rand.nextInt(sides) + 1;
   }

   public static void hideFile(File src)
   {
      if (src == null)
         throw new IllegalArgumentException("src cannot be null");
      else if (!src.exists())
         throw new IllegalArgumentException("src must exist");

      if (System.getProperty("os.name").toLowerCase().contains("win"))
      {
         Process p;
         try
         {
            p = Runtime.getRuntime().exec("attrib +h " + src.getPath());
            p.waitFor();
         } catch (IOException e)
         {
            Logger.logError("Error: Could not set the hidden attribute for \"" + src.getPath() + "\"");
            e.printStackTrace();
         } catch (InterruptedException exc)
         {
         }
      } else if (!src.getName().startsWith(".")) // If the file is already
         // hidden to UNIX systems don't
         // add another dot
         src.renameTo(new File(src.getParentFile().getPath() + "/." + src.getName()));
   }

   public static void setSystemLAF()
   {
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      } catch (InstantiationException e)
      {
         e.printStackTrace();
      } catch (IllegalAccessException e)
      {
         e.printStackTrace();
      } catch (UnsupportedLookAndFeelException e)
      {
         e.printStackTrace();
      }
   }

   public static File saveDialog()
   {
      return saveDialog(new File(System.getProperty("user.home")));
   }

   public static File saveDialog(File currDirectory)
   {
      return saveDialog(currDirectory, null);
   }

   public static File saveDialog(File currDirectory, final String extName, final String... exts)
   {
      Frame frame = new Frame();
      frame.setUndecorated(true);
      // Java 6 doesn't have this frame.setOpacity(0);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
      frame.toFront();
      frame.setVisible(false);
      frame.dispose();

      final JFileChooser chooser = new JFileChooser(currDirectory);
      chooser.setMultiSelectionEnabled(false);
      if (extName != null)
      {
         FileFilter filter = new FileFilter()
         {

            @Override
            public boolean accept(File file)
            {
               if (file.isDirectory())
                  return true;

               String fileName = file.getName();
               for (int i = 0; i < exts.length; i++)
                  if (fileName.endsWith(exts[i]))
                     return true;
               return false;
            }

            @Override
            public String getDescription()
            {
               return extName;
            }
         };
         chooser.setFileFilter(filter);
         chooser.addChoosableFileFilter(filter);
      } else
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int opt = chooser.showSaveDialog(null);
      if (opt == JFileChooser.APPROVE_OPTION)
      {
         File file = chooser.getSelectedFile();
         if (exts.length > 0 && !file.getName().endsWith(exts[0]))
            file = new File(file.getPath() + exts[0]);
         if (!file.exists())
            try
            {
               file.createNewFile();
            } catch (IOException e1)
            {
               e1.printStackTrace();
               Logger.logError("Could not create the file \"" + file.getName() + "\"!");
               return null;
            }
         else
         {
            opt = JOptionPane.showConfirmDialog(null, "The file \"" + file.getName() + "\" already exists. Would you like to overwrite it?", "Overwrite?", JOptionPane.YES_NO_OPTION);
            if (opt != JOptionPane.YES_OPTION)
               return null;
         }
         return file;
      }
      return null;
   }

   public static File openDialog()
   {
      return openDialog(new File(System.getProperty("user.home")), false);
   }

   public static File openDialog(boolean acceptDirectories)
   {
      return openDialog(new File(System.getProperty("user.home")), acceptDirectories);
   }

   public static File openDialog(File currDirectory)
   {
      return openDialog(currDirectory, false);
   }

   public static File openDialog(File currDirectory, boolean acceptDirectories)
   {
      final JFileChooser chooser = new JFileChooser(currDirectory);
      chooser.setMultiSelectionEnabled(false);
      if (acceptDirectories)
         chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      else
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

      int opt = chooser.showOpenDialog(null);
      if (opt == JFileChooser.APPROVE_OPTION)
      {
         File file = chooser.getSelectedFile();
         if (file.exists())
            return file;
         else
            Logger.logError("The selected file doesn't exist. File: " + file);
      }
      return null;
   }

   public static File openDialog(File currDirectory, final String extName, final String... exts)
   {
      final JFileChooser chooser = new JFileChooser(currDirectory);
      chooser.setMultiSelectionEnabled(false);
      if (extName != null)
      {
         FileFilter filter = new FileFilter()
         {

            @Override
            public boolean accept(File file)
            {
               if (file.isDirectory())
                  return true;

               String fileName = file.getName();
               for (int i = 0; i < exts.length; i++)
                  if (fileName.endsWith(exts[i]))
                     return true;
               return false;
            }

            @Override
            public String getDescription()
            {
               return extName;
            }
         };
         chooser.setFileFilter(filter);
         chooser.addChoosableFileFilter(filter);
      } else
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      int opt = chooser.showOpenDialog(null);
      if (opt == JFileChooser.APPROVE_OPTION)
      {
         File file = chooser.getSelectedFile();
         if (file.exists())
            return file;
         else
            Logger.logError("The selected file doesn't exist. File: " + file);
      }
      return null;
   }
}
