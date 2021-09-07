package com.github.jacekpoz.client.desktop.gui;

import com.github.jacekpoz.common.Util;
import com.github.jacekpoz.common.sendables.Message;
import com.github.jacekpoz.common.sendables.User;
import lombok.Getter;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;

public class MessagePanel extends JTextPane {

    @Getter
    private final boolean isCurrentUserAuthor;

    public MessagePanel(User currentUser, User author, Message m) {
        setEnabled(false);
        setBorder(null);
        setOpaque(false);
        setContentType("text/html");

        // default font trick taken from https://explodingpixels.wordpress.com/2008/10/28/make-jeditorpane-use-the-system-font/
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                "font-size: " + font.getSize() + "pt; color: white; }";
        ((HTMLDocument) getDocument()).getStyleSheet().addRule(bodyRule);

        setText("<html>" + author.getUsername() + ": " + m.getContent() + "</html>");
        setToolTipText(Util.localDateTimeToString(m.getSentDate()));
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);

        isCurrentUserAuthor = currentUser.equals(author);
    }

}