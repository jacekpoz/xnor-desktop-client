package com.github.jacekpoz.client.desktop.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class ChatsContainer extends JPanel {

    @Getter
    private final List<ChatPanel> chats;

    public ChatsContainer() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);
        chats = new ArrayList<>();
    }

    public void addChat(ChatPanel chat) {
        add(chat);
        chats.add(chat);
        revalidate();
    }

    public void removeAllChats() {
        chats.clear();
        removeAll();
        revalidate();
    }
}
