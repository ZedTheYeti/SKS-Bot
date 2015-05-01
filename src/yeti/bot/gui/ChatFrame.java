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
package yeti.bot.gui;

import yeti.bot.Globals;
import yeti.bot.JIRC;
import yeti.bot.User;
import yeti.bot.util.Logger;
import yeti.bot.util.MySQL;
import yeti.bot.util.PushbulletAPI;
import yeti.bot.util.Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// give Mufflinn 1xp


public class ChatFrame extends JFrame
{
   private JPanel contentPane;
   private JTextField textField;
   private JTextPane textPane;
   private SendListener listener;
   private JScrollPane scrollPane;
   private JMenuBar menuBar;
   private JMenu mnFile;
   private JMenuItem mntmSave;
   private JMenuItem mntmExit;
   private JMenu mnDebug;
   private JMenuItem mntmListOnlineUsers;
   private JMenuItem mntmListOfflineUsers;
   private JMenuItem mntmListAllUsers;
   private JMenuItem mntmGeneralDebug;
   private JMenuItem mntmReload;
   private JMenuItem mntmFind;
   private TrayIcon trayIcon;

   private final String debugText = "Shtuff";
   private JMenuItem mntmRestart;
   private JMenuItem mntmSaveDb;
   private JMenuItem mntmTestPushbullet;

   public ChatFrame()
   {
      createGUI();
      setPreferredSize(new Dimension(750, 600));
      setSize(750, 600);

      setDefaultCloseOperation(EXIT_ON_CLOSE);
      BufferedImage icon = null;
      try
      {
         icon = ImageIO.read(ChatFrame.class.getClassLoader().getResource("resources/koolWALLY.png").openStream());
      } catch (IOException ioe)
      {
         Logger.logError("Couldn't load icon image");
      }

      if (icon != null)
      {
         setIconImage(icon);

         if (SystemTray.isSupported())
         {
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

            PopupMenu popup = new PopupMenu();
            popup.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  setVisible(true);
               }
            });

            popup.add(toMenuItem(mntmSave));

            popup.addSeparator();

            for (int i = 0; i < mnDebug.getItemCount(); i++)
               popup.add(toMenuItem(mnDebug.getItem(i)));

            MenuItem mntmShow = new MenuItem("Show");
            mntmShow.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  setVisible(true);
               }
            });
            popup.add(mntmShow);

            MenuItem mtmExit = new MenuItem("Exit");
            mtmExit.addActionListener(new ActionListener()
            {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                  JIRC.saveAll();

                  System.exit(0);
               }
            });
            popup.add(mtmExit);

            trayIcon = new TrayIcon(icon, "SKS Bot", popup);
            try
            {
               SystemTray.getSystemTray().add(trayIcon);
            } catch (Exception e)
            {
            }
         }
      }
   }

   public void addText(String text)
   {
      Globals.msgCount++;
      String areaText = textPane.getText();

      if (text.lastIndexOf('\n') != text.length() - 1)
         text += '\n';

      if (Globals.msgCount > 250)
      {
         int index = areaText.indexOf('\n') + 1;
         if (index > 0 && index < areaText.length())
            textPane.setText(areaText.substring(index) + text);
         Globals.msgCount--;
      } else
         textPane.setText(areaText + text);
      textPane.validate();

      int caret = textPane.getDocument().getLength() - 1;
      if (caret >= 0)
         textPane.setCaretPosition(caret);
   }

   public void setSendListener(SendListener listener)
   {
      this.listener = listener;
   }

   @Override
   public void setVisible(boolean visible)
   {
      super.setVisible(visible);
      if (visible)
         textField.requestFocus();
   }

   private String getDebugText()
   {
      return debugText;
   }

   private void createGUI()
   {
      setTitle("SKS Bot");
      setBounds(100, 100, 450, 400);

      addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            super.windowClosing(e);

            JIRC.saveAll();

            if (trayIcon != null)
               SystemTray.getSystemTray().remove(trayIcon);

            System.exit(0);
         }

         public void windowIconified(WindowEvent e) {
            super.windowIconified(e);

            if (trayIcon != null)
               setVisible(false);
         }
      });

      menuBar = new JMenuBar();
      setJMenuBar(menuBar);

      mnFile = new JMenu("File");
      menuBar.add(mnFile);

      mntmSave = new JMenuItem("Save");
      mntmSave.addActionListener(arg0 -> JIRC.saveAll());
      mnFile.add(mntmSave);

      mntmSaveDb = new JMenuItem("Save to DB");
      mntmSaveDb.addActionListener(arg0 ->
      {
         addText("Saving users to database.\n");
         Logger.logDebug("Saving users to database.");
         MySQL.updateUsers();
         Logger.logDebug("Done saving users to database.");
         addText("Done saving users to database.\n");
      });
      mnFile.add(mntmSaveDb);

      mntmRestart = new JMenuItem("Restart");
      mntmRestart.addActionListener(arg0 ->
      {
         JIRC.saveAll();
         setVisible(false);
         Util.restart();
      });
      mnFile.add(mntmRestart);

      mntmExit = new JMenuItem("Exit");
      mntmExit.addActionListener(arg0 -> {
         JIRC.saveAll();
         setVisible(false);
         System.exit(0);
      });
      mnFile.add(mntmExit);

      mnDebug = new JMenu("Debug");
      menuBar.add(mnDebug);

      final JFrame frame = this;

      mntmListOnlineUsers = new JMenuItem("List Online Users");
      mntmListOnlineUsers.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            StringBuilder bldr = new StringBuilder();

            for (User usr : Globals.getOnlineUsers()) {
               bldr.append(usr.getName());
               bldr.append('=');
               bldr.append(usr.getInfo());
               bldr.append(',');
               bldr.append(usr.inChannel);
               bldr.append(',');
               bldr.append(usr.joinTime);
               bldr.append('\n');
            }

            try {
               DebugDialog dialog = new DebugDialog(frame, true, bldr.toString());
               dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
               dialog.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
      mnDebug.add(mntmListOnlineUsers);

      mntmListOfflineUsers = new JMenuItem("List Offline Users");
      mntmListOfflineUsers.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            StringBuilder bldr = new StringBuilder();

            for (User usr : Globals.getOfflineUsers()) {
               bldr.append(usr.getName());
               bldr.append('=');
               bldr.append(usr.getInfo());
               bldr.append(',');
               bldr.append(usr.inChannel);
               bldr.append(',');
               bldr.append(usr.joinTime);
               bldr.append('\n');
            }

            try {
               DebugDialog dialog = new DebugDialog(frame, true, bldr.toString());
               dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
               dialog.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
      mnDebug.add(mntmListOfflineUsers);

      mntmListAllUsers = new JMenuItem("List All Users");
      mntmListAllUsers.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            StringBuilder bldr = new StringBuilder();

            for (User usr : Globals.getOfflineUsers()) {
               bldr.append(usr.getName());
               bldr.append('=');
               bldr.append(usr.getInfo());
               bldr.append(',');
               bldr.append(usr.inChannel);
               bldr.append(',');
               bldr.append(usr.joinTime);
               bldr.append('\n');
            }

            for (User usr : Globals.getOnlineUsers()) {
               bldr.append(usr.getName());
               bldr.append('=');
               bldr.append(usr.getInfo());
               bldr.append(',');
               bldr.append(usr.inChannel);
               bldr.append(',');
               bldr.append(usr.joinTime);
               bldr.append('\n');
            }

            try {
               DebugDialog dialog = new DebugDialog(frame, true, bldr.toString());
               dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
               dialog.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      });
      mnDebug.add(mntmListAllUsers);

      mntmGeneralDebug = new JMenuItem("General Debug");
      mntmGeneralDebug.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent arg0) {
            try {
               DebugDialog dialog = new DebugDialog(frame, true, Globals.msgCount + " message count");
               dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
               dialog.setVisible(true);
            } catch (Exception e) {
               e.printStackTrace();
            }
            textPane.setText("");
            System.gc();
            Globals.msgCount = 0;
         }
      });
      mnDebug.add(mntmGeneralDebug);


      mntmReload = new JMenuItem("Reload Commands");
      mntmReload.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            Globals.reloadCommands();

            StringBuilder bldr = new StringBuilder("/me ");

            if (Globals.commands.size() > 0) {
               bldr.append("General Commands: ");
               for (int i = 0; i < Globals.commands.size(); i++) {
                  bldr.append(Globals.commands.get(i).getUsage());
                  if (i < Globals.commands.size() - 1)
                     bldr.append(',').append(' ');
               }
            }

            if (Globals.subCommands.size() > 0) {
               bldr.append(" - Subscriber Commands: ");
               for (int i = 0; i < Globals.subCommands.size(); i++) {
                  bldr.append(Globals.subCommands.get(i).getUsage());
                  if (i < Globals.subCommands.size() - 1)
                     bldr.append(',').append(' ');
               }
            }

            if (Globals.modCommands.size() > 0) {
               bldr.append(" - General Commands: ");
               for (int i = 0; i < Globals.modCommands.size(); i++) {
                  bldr.append(Globals.modCommands.get(i).getUsage());
                  if (i < Globals.modCommands.size() - 1)
                     bldr.append(',').append(' ');
               }
            }

            DebugDialog dialog = new DebugDialog(frame, true, bldr.toString());
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
         }
      });
      mnDebug.add(mntmReload);

      mntmFind = new JMenuItem("Find User");
      mntmFind.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(frame, "Username:");
            if (input != null) {
               input = input.trim().toLowerCase();
               StringBuilder bldr = new StringBuilder();

               for (User usr : Globals.getOnlineUsers())
                  if (usr.getName().contains(input))
                     bldr.append(usr.getName()).append(" = ").append(usr.getInfo().replaceAll(",", ", ")).append('\n');

               for (User usr : Globals.getOfflineUsers())
                  if (usr.getName().contains(input))
                     bldr.append(usr.getName()).append(" = ").append(usr.getInfo().replaceAll(",", ", ")).append('\n');

               DebugDialog dialog = new DebugDialog(frame, true, bldr.toString());
               dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
               dialog.setVisible(true);
            }
         }
      });
      mnDebug.add(mntmFind);

      mntmTestPushbullet = new JMenuItem("Test PushBullet");
      mntmTestPushbullet.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            if(Globals.pushbulletKey != null && !Globals.pushbulletKey.isEmpty());
            {
               PushbulletAPI.sendPush(Globals.pushbulletKey, "Test", "SKS-Bot Test");
            }
         }
      });
      mnDebug.add(mntmTestPushbullet);

      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);

      JButton btnSend = new JButton("Send");
      btnSend.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            if (listener != null)
            {
               String text = textField.getText();
               textField.setText("");
               addText(text + "\n");
               listener.send(text);
            }
         }
      });

      textField = new JTextField();
      textField.addKeyListener(new KeyAdapter()
      {
         @Override
         public void keyReleased(KeyEvent arg0)
         {
            if (arg0.getKeyCode() == 10)
               if (listener != null)
               {
                  String text = textField.getText();
                  textField.setText("");
                  addText(text + "\n");
                  listener.send(text);
               }
         }
      });
      textField.setColumns(10);

      scrollPane = new JScrollPane();
      GroupLayout gl_contentPane = new GroupLayout(contentPane);
      gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup().addComponent(textField, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnSend))
            .addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE));
      gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
            Alignment.TRAILING,
            gl_contentPane.createSequentialGroup().addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(btnSend).addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))));

      textPane = new JTextPane();
      textPane.setFont(new Font("Monospaced", Font.PLAIN, 11));
      textPane.setEditable(false);
      scrollPane.setViewportView(textPane);
      contentPane.setLayout(gl_contentPane);
   }

   private static MenuItem toMenuItem(JMenuItem item)
   {
      MenuItem newItem = new MenuItem(item.getLabel());
      for (ActionListener al : item.getActionListeners())
         newItem.addActionListener(al);
      return newItem;
   }
}
