package com.github.jacekpoz.client.desktop.gui;

import com.github.jacekpoz.common.sendables.User;
import com.github.jacekpoz.common.sendables.database.queries.FriendRequestQuery;
import com.github.jacekpoz.common.sendables.database.queries.FriendRequestQueryEnum;
import com.github.jacekpoz.common.sendables.database.queries.UserQuery;
import com.github.jacekpoz.common.sendables.database.queries.UserQueryEnum;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserPanel extends JPanel {

    private final static Logger LOGGER = Logger.getLogger(UserPanel.class.getName());

    public static final int NOT_FRIEND = 0;
    public static final int FRIEND = 1;
    public static final int REQUEST = 2;

    private final XnorWindow window;
    private final JPanel addedTo;
    @Getter
    private final User clientUser;
    @Getter
    private final User panelUser;
    @Getter @Setter
    private int userPanelType;
    private JLabel userLabel;
    private JButton button1;
    private JButton button2;

    public UserPanel(XnorWindow w, JPanel jp, User u, User pU, int type) {
        window = w;
        addedTo = jp;
        clientUser = u;
        panelUser = pU;
        userPanelType = type;
        userLabel = new JLabel(panelUser.getUsername() + "(ID=" + panelUser.getUserID() + ")");
        userLabel.setBackground(new Color(60, 60, 60));
        userLabel.setForeground(Color.WHITE);
        add(userLabel);
        setBackground(new Color(60, 60, 60));
        setForeground(Color.WHITE);
        changeType(userPanelType);
    }

    public void changeType(int type) {
        for (Component c : getComponents())
            if (c instanceof JButton)
                remove(c);

        button1 = null;
        button2 = null;

        switch (type) {
            case NOT_FRIEND:
                button1 = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/add_friend.png"))));
                button1.addActionListener(a -> {
                    FriendRequestQuery send = new FriendRequestQuery(
                            false,
                            window.getFriendsScreen().getScreenID(),
                            FriendRequestQueryEnum.SEND_FRIEND_REQUEST);
                    send.putValue("senderID", clientUser.getUserID());
                    send.putValue("receiverID", panelUser.getUserID());
                    window.send(send);
                    LOGGER.log(Level.INFO, "Sent friend request", panelUser);
                    removeThis();
                });
                break;
            case FRIEND:
                button1 = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/delete_friend.png"))));
                button1.addActionListener(a -> {
                    clientUser.removeFriend(panelUser);
                    UserQuery remove = new UserQuery(
                            true,
                            window.getFriendsScreen().getScreenID(),
                            UserQueryEnum.REMOVE_FRIEND);
                    remove.putValue("userID", clientUser.getUserID());
                    remove.putValue("friendID", panelUser.getUserID());
                    window.send(remove);
                    LOGGER.log(Level.INFO, "Removed friend", panelUser);
                    removeThis();
                });
                break;
            case REQUEST:
                button1 = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/accept.png"))));
                button1.addActionListener(a -> {
                    clientUser.addFriend(panelUser);
                    FriendRequestQuery accept = new FriendRequestQuery(
                            true,
                            window.getFriendsScreen().getScreenID(),
                            FriendRequestQueryEnum.ACCEPT_FRIEND_REQUEST
                    );
                    accept.putValue("senderID", panelUser.getUserID());
                    accept.putValue("receiverID", clientUser.getUserID());
                    window.send(accept);
                    LOGGER.log(Level.INFO, "Accepted friend request", panelUser);
                    removeThis();
                });
                button2 = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/decline.png"))));
                button2.addActionListener(a -> {
                    FriendRequestQuery deny = new FriendRequestQuery(
                            false,
                            window.getFriendsScreen().getScreenID(),
                            FriendRequestQueryEnum.DENY_FRIEND_REQUEST
                    );
                    deny.putValue("senderID", panelUser.getUserID());
                    deny.putValue("receiverID", clientUser.getUserID());
                    window.send(deny);
                    LOGGER.log(Level.INFO, "Denied friend request", panelUser);
                    removeThis();
                });
                break;
            default:
                throw new IllegalArgumentException("You have to pass in NOT_FRIEND, FRIEND or REQUEST");
        }

        if (button1 != null) {
            button1.setBackground(new Color(60, 60, 60));
            button1.setForeground(Color.WHITE);
            button1.setBorderPainted(false);
            add(button1);
        }
        if (button2 != null) {
            button2.setBackground(new Color(60, 60, 60));
            button2.setForeground(Color.WHITE);
            button2.setBorderPainted(false);
            add(button2);
        }

        revalidate();
    }

    private void removeThis() {
        remove(userLabel);
        if (button1 != null) remove(button1);
        if (button2 != null) remove(button2);
        addedTo.remove(this);
        addedTo.revalidate();
        addedTo.repaint();
    }
}
