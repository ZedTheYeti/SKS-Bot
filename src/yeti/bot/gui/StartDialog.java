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

import yeti.bot.util.Options;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartDialog extends JDialog
{
   private Options options;
   private JTextField txtUsername;
   private JTextField txtOauth;
   private JTextField txtServer;
   private JTextField txtChannel;
   private JTextField txtPort;
   private JButton btnReset;
   private JButton btnOk;
   private JButton btnClose;
   private JTextField txtPushbullet;

   /**
    * Create the dialog.
    */
   public StartDialog(JFrame parent, boolean modal, Options opt)
   {
      super(parent, modal);

      options = opt;

      createGUI();

      setLocationRelativeTo(parent);

      String username = options.get("username");
      String oauth = options.get("oauth");
      String serverName = options.get("server");
      String port = options.get("port");
      String channel = options.get("channel");
      String pushbullet = options.get("pushbullet");

      if (username != null)
         txtUsername.setText(username.toLowerCase());
      if (oauth != null)
         txtOauth.setText(oauth);
      if (serverName != null)
         txtServer.setText(serverName);
      if (port != null)
         txtPort.setText(port);
      if (channel != null)
         txtChannel.setText(channel.toLowerCase());
      if (pushbullet != null)
         txtPushbullet.setText(pushbullet);
   }

   /**
    *
    */
   private void createGUI()
   {
      setTitle("Startup");
      setBounds(100, 100, 250, 240);

      JLabel lblUsername = new JLabel("Username:");

      JLabel lblOauthToken = new JLabel("OAuth Token:");

      txtUsername = new JTextField();
      txtUsername.setColumns(10);

      txtOauth = new JTextField();
      txtOauth.setColumns(10);

      txtServer = new JTextField();
      txtServer.setText("irc.twitch.tv");
      txtServer.setColumns(10);

      JLabel lblIrcServer = new JLabel("IRC Server:");

      txtChannel = new JTextField();
      txtChannel.setText("SourKoolaidShow");
      txtChannel.setColumns(10);

      JLabel lblChannel = new JLabel("Channel:");

      txtPort = new JTextField();
      txtPort.setText("6667");
      txtPort.setColumns(10);

      JLabel lblPort = new JLabel("Port:");

      btnReset = new JButton("Reset");
      btnReset.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent ae)
         {

         }
      });

      btnOk = new JButton("Start");
      btnOk.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent arg0)
         {
            options.set("username", txtUsername.getText().trim());
            options.set("oauth", txtOauth.getText().trim());
            options.set("server", txtServer.getText().trim());
            options.set("channel", txtChannel.getText().trim());
            options.set("port", txtPort.getText().trim());
            options.set("pushbullet", txtPushbullet.getText().trim());
            options.save();

            setVisible(false);
            dispose();
         }
      });

      btnClose = new JButton("Close");
      btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            System.exit(0);
         }
      });

      JLabel lblPushbullet = new JLabel("PushBullet:");

      txtPushbullet = new JTextField();
      txtPushbullet.setColumns(10);
      GroupLayout groupLayout = new GroupLayout(getContentPane());
      groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
            groupLayout
                  .createSequentialGroup()
                  .addGroup(
                        groupLayout
                              .createParallelGroup(Alignment.LEADING)
                              .addGroup(
                                    groupLayout
                                          .createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(
                                                groupLayout
                                                      .createParallelGroup(Alignment.LEADING)
                                                      .addGroup(groupLayout.createSequentialGroup().addComponent(lblUsername).addGap(19).addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                                                      .addGroup(
                                                            groupLayout.createSequentialGroup().addComponent(lblOauthToken).addPreferredGap(ComponentPlacement.RELATED).addComponent(txtOauth, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                                                      .addGroup(groupLayout.createSequentialGroup().addComponent(lblIrcServer).addGap(14).addComponent(txtServer, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                                                      .addGroup(groupLayout.createSequentialGroup().addComponent(lblChannel).addGap(28).addComponent(txtChannel, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                                                      .addGroup(groupLayout.createSequentialGroup().addComponent(lblPort).addGap(47).addComponent(txtPort, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                                                      .addGroup(groupLayout.createSequentialGroup().addComponent(lblPushbullet).addGap(18).addComponent(txtPushbullet, GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))))
                              .addGroup(
                                    groupLayout.createSequentialGroup().addGap(19).addComponent(btnReset).addPreferredGap(ComponentPlacement.UNRELATED).addComponent(btnClose).addPreferredGap(ComponentPlacement.UNRELATED)
                                          .addComponent(btnOk))).addContainerGap()));
      groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
            groupLayout
                  .createSequentialGroup()
                  .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(14).addComponent(lblUsername))
                              .addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(9).addComponent(lblOauthToken))
                              .addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(txtOauth, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(9).addComponent(lblIrcServer))
                              .addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(txtServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(9).addComponent(lblChannel))
                              .addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(txtChannel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                  .addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(
                        groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(9).addComponent(lblPort))
                              .addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(txtPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))).addPreferredGap(ComponentPlacement.RELATED)
                  .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(lblPushbullet).addComponent(txtPushbullet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)).addGap(11)
                  .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnReset).addComponent(btnClose).addComponent(btnOk)).addContainerGap(26, Short.MAX_VALUE)));
      getContentPane().setLayout(groupLayout);
   }
}
