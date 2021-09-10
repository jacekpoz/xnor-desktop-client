package com.github.jacekpoz.client.desktop.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class MessageContainer extends JPanel {

    private final XnorWindow window;

    @Getter
    private final JLabel noMessages;

    public MessageContainer(XnorWindow w) {
        window = w;
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        noMessages = new JLabel(window.getLangString("app.no_messages_in_chat"));
        noMessages.setBackground(new Color(60, 60, 60));
        noMessages.setForeground(Color.WHITE);
        noMessages.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(noMessages);
    }

    public void addMessage(MessagePanel mp) {
        if (Arrays.asList(getComponents()).contains(noMessages)) {
            remove(noMessages);
            repaint();
        }
        if (mp.isCurrentUserAuthor()) {
            mp.setAlignmentX(Component.RIGHT_ALIGNMENT);
//            mp.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            mp.setAlignmentX(Component.LEFT_ALIGNMENT);
//            mp.setHorizontalAlignment(SwingConstants.LEFT);
        }

        add(mp);
        revalidate();
    }

    public void removeAllMessages() {
        removeAll();
        revalidate();
        repaint();
    }
}
