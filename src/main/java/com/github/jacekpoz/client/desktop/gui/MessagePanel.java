package com.github.jacekpoz.client.desktop.gui;

import com.github.jacekpoz.common.Util;
import com.github.jacekpoz.common.sendables.Message;
import com.github.jacekpoz.common.sendables.User;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class MessagePanel extends JLabel {

    @Getter
    private final boolean isCurrentUserAuthor;

    public MessagePanel(User currentUser, User author, Message m) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
        setText("<html>" + author.getUsername() + ": " + m.getContent() + "</html>");
        setToolTipText(Util.localDateTimeToString(m.getSentDate()));
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);
        setMaximumSize(new Dimension(100, 25));

        isCurrentUserAuthor = currentUser.equals(author);
    }

}