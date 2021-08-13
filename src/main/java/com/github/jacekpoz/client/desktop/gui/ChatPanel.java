package com.github.jacekpoz.client.desktop.gui;

import com.github.jacekpoz.client.desktop.gui.screens.MessageScreen;
import com.github.jacekpoz.common.sendables.Chat;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JButton {

    private final JLabel label;

    public ChatPanel(MessageScreen m, ChatsContainer parent, Chat c) {
        setMaximumSize(new Dimension(250, 40));
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);
        setBorderPainted(false);
        label = new JLabel(c.getName());
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBackground(new Color(60, 60, 60));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        add(label);
        addActionListener(e -> {
            m.setChat(c);
            for (ChatPanel cp : parent.getChats())
                cp.deselectChat();
            selectChat();
        });
    }

    public void selectChat() {
        label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
    }

    public void deselectChat() {
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
    }

}
