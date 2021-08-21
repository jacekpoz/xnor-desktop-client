package com.github.jacekpoz.client.desktop.gui;

import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;


public class MessageContainer extends JPanel {

    private final ChatWindow window;

    @Getter
    private final JLabel noMessages;

    public MessageContainer(ChatWindow w) {
        window = w;
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        noMessages = new JLabel(window.getLangString("app.no_messages_in_chat"));
        noMessages.setBackground(new Color(60, 60, 60));
        noMessages.setForeground(Color.WHITE);
        noMessages.setHorizontalAlignment(SwingConstants.CENTER);
        add(noMessages);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int eventWidth = e.getComponent().getWidth();
                for (Component c : getComponents()) {
                    c.setMaximumSize(new Dimension(eventWidth, c.getHeight()));
                    c.revalidate();
                }
                revalidate();
            }
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });
    }

    public void addMessage(MessagePanel mp) {
        remove(noMessages);
        mp.setMaximumSize(new Dimension(getWidth(), mp.getHeight()));
        if (mp.isCurrentUserAuthor()) mp.setHorizontalAlignment(SwingConstants.RIGHT);

        add(mp);
        revalidate();
    }

    public void removeAllMessages() {
        removeAll();
        revalidate();
    }
}
