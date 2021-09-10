package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.*;
import com.github.jacekpoz.common.Util;
import com.github.jacekpoz.common.sendables.*;
import com.github.jacekpoz.common.sendables.database.queries.*;
import com.github.jacekpoz.common.sendables.database.results.ChatResult;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(MessageScreen.class.getName());

    private transient final XnorWindow window;

    private transient JPanel messageScreen;
    private transient JButton chatsButton;
    private transient MessageSender messageSender;
    private transient JButton friendsButton;
    private transient MessageContainer messages;
    private transient JScrollPane messagesScrollPane;
    private transient ChatsContainer chats;
    private transient JScrollPane chatsScrollPane;
    private transient JButton settingsButton;
    private JButton logoutButton;
    private JScrollPane messageAreaScrollPane;

    private List<Chat> usersChats;
    private Map<Chat, List<User>> usersInChats;
    private Map<Message, User> messageAuthors;

    public MessageScreen(XnorWindow w) {
        window = w;
        usersChats = new ArrayList<>();
        usersInChats = new HashMap<>();
        messageAuthors = new HashMap<>();

        $$$setupUI$$$();

        Runnable sendMessageRunnable = () -> {
            Message m = messageSender.getMessage();
            if (m == null) return;
            Chat c = window.getClient().getChat();
            c.setMessageCounter(c.getMessageCounter() + 1);
            sendMessage(m);
            addMessage(window.getClient().getUser(), m);
            LOGGER.log(Level.INFO, "Sent message", m);
            JScrollBar bar = messagesScrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        };

        messageSender.addListener(sendMessageRunnable);
        friendsButton.addActionListener(e -> window.setScreen(window.getFriendsScreen()));
        chatsButton.addActionListener(e -> window.setScreen(window.getCreateChatsScreen()));
        settingsButton.addActionListener(e -> {
            window.setScreen(window.getSettingsScreen());
            window.setLastScreen(this);
        });
        logoutButton.addActionListener(e -> {
            LOGGER.log(Level.INFO, "Logged out", window.getClient().getUser());
            window.logout();
        });
    }

    public void addChat(Chat c) {
        ChatPanel cp = new ChatPanel(this, chats, c);
        List<User> users = usersInChats.get(c);
        if (users != null) cp.setToolTipText(Util.userListToString(users));
        else update();
        chats.addChat(cp);
        chats.revalidate();
    }

    private void sendMessage(Message m) {
        window.send(m);
        MessageQuery send = new MessageQuery(
                false,
                getScreenID(),
                MessageQueryEnum.INSERT_MESSAGE
        );
        send.putValue("messageID", m.getMessageID());
        send.putValue("chatID", m.getChatID());
        send.putValue("authorID", m.getAuthorID());
        send.putValue("content", m.getContent());
        window.send(send);
    }

    public void setChat(Chat c) {
        window.getClient().setChat(c);
        window.send(c);
        messages.removeAllMessages();
        c.getMessages().forEach(message -> {
            User u = messageAuthors.get(message);
            if (u == null) update();
            else addMessage(u, message);
        });
        messageSender.setEnabled(true);
        messages.revalidate();
    }

    public void addMessage(User author, Message message) {
        messages.addMessage(new MessagePanel(
                window.getClient().getUser(),
                author,
                message
        ));
    }

    @Override
    public JPanel getPanel() {
        return messageScreen;
    }

    private void updateUsersChats() {
        for (Chat c : usersChats) {
            UserQuery getUsersInChat = new UserQuery(
                    false,
                    getScreenID(),
                    UserQueryEnum.GET_USERS_IN_CHAT
            );
            getUsersInChat.putValue("chatID", c.getChatID());
            window.send(getUsersInChat);
            for (Message m : c.getMessages()) {
                UserQuery messageAuthor = new UserQuery(
                        false,
                        getScreenID(),
                        UserQueryEnum.GET_MESSAGE_AUTHOR
                );
                messageAuthor.putValue("messageID", m.getMessageID());
                messageAuthor.putValue("chatID", m.getChatID());
                window.send(messageAuthor);
            }
        }
    }

    @Override
    public void update() {
        try {
            usersInChats.forEach((c, lu) -> usersInChats.remove(c, lu));
        } catch (ConcurrentModificationException ignored) {
        }
        if (window.getClient().isLoggedIn()) {
            ChatQuery usersChats = new ChatQuery(
                    false,
                    getScreenID(),
                    ChatQueryEnum.GET_USERS_CHATS
            );
            usersChats.putValue("userID", window.getClient().getUser().getUserID());
            window.send(usersChats);
        }
        updateUsersChats();
    }

    @Override
    public void updateUI() {
        chats.removeAllChats();
        for (Chat c : usersChats)
            addChat(c);
    }

    @Override
    public void handleSendable(Sendable s) {
        if (s instanceof ChatResult) {
            ChatResult cr = (ChatResult) s;
            if (cr.getQuery().getQueryType() == ChatQueryEnum.GET_USERS_CHATS) {
                usersChats = cr.get();
                updateUI();
                updateUsersChats();
            }
        } else if (s instanceof UserResult) {
            UserResult ur = (UserResult) s;
            UserQuery uq = ur.getQuery();
            if (uq.getQueryType() == UserQueryEnum.GET_MESSAGE_AUTHOR) {
                for (Chat c : usersChats)
                    if (c.getChatID() == uq.getValue("chatID", long.class))
                        for (Message m : c.getMessages())
                            if (m.getMessageID() == uq.getValue("messageID", long.class))
                                messageAuthors.put(m, ur.get(0));
            } else if (uq.getQueryType() == UserQueryEnum.GET_USERS_IN_CHAT) {
                for (Chat c : usersChats) {
                    if (c.getChatID() == uq.getValue("chatID", long.class)) {
                        usersInChats.put(c, ur.get());
                        return;
                    }
                }
            }
        } else if (s instanceof Message) {
            Message m = (Message) s;
            for (User u : usersInChats.get(window.getClient().getChat())) {
                if (u.getUserID() == m.getAuthorID()) {
                    messageAuthors.put(m, u);
                    addMessage(u, m);
                }
            }
        }
    }

    @Override
    public long getScreenID() {
        return 2;
    }

    @Override
    public void changeLanguage() {
        messageSender.changeLanguage();
    }


    private void createUIComponents() {
        messages = new MessageContainer(window);
        chats = new ChatsContainer();
        messageSender = new MessageSender(window);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        messageScreen = new JPanel();
        messageScreen.setLayout(new GridLayoutManager(3, 7, new Insets(0, 0, 0, 0), 5, 5));
        messageScreen.setBackground(new Color(-12829636));
        messageScreen.setForeground(new Color(-1));
        messagesScrollPane = new JScrollPane();
        messagesScrollPane.setBackground(new Color(-12829636));
        messagesScrollPane.setForeground(new Color(-1));
        messagesScrollPane.setHorizontalScrollBarPolicy(31);
        messagesScrollPane.setVerticalScrollBarPolicy(22);
        messageScreen.add(messagesScrollPane, new GridConstraints(0, 4, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        messages.setBackground(new Color(-12829636));
        messages.setForeground(new Color(-1));
        messagesScrollPane.setViewportView(messages);
        chatsScrollPane = new JScrollPane();
        chatsScrollPane.setBackground(new Color(-12829636));
        chatsScrollPane.setForeground(new Color(-1));
        chatsScrollPane.setVerticalScrollBarPolicy(20);
        messageScreen.add(chatsScrollPane, new GridConstraints(1, 0, 2, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        chatsScrollPane.setViewportView(chats);
        friendsButton = new JButton();
        friendsButton.setBackground(new Color(-12829636));
        friendsButton.setBorderPainted(false);
        friendsButton.setFocusPainted(false);
        friendsButton.setForeground(new Color(-1));
        friendsButton.setIcon(new ImageIcon(getClass().getResource("/images/friends.png")));
        friendsButton.setText("");
        messageScreen.add(friendsButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), null, new Dimension(30, 30), 0, false));
        settingsButton = new JButton();
        settingsButton.setAlignmentX(0.0f);
        settingsButton.setAlignmentY(0.5f);
        settingsButton.setBackground(new Color(-12829636));
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setForeground(new Color(-1));
        settingsButton.setHorizontalTextPosition(0);
        settingsButton.setIcon(new ImageIcon(getClass().getResource("/images/settings.png")));
        settingsButton.setText("");
        messageScreen.add(settingsButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), null, new Dimension(30, 30), 0, false));
        logoutButton = new JButton();
        logoutButton.setBackground(new Color(-12829636));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setForeground(new Color(-1));
        logoutButton.setIcon(new ImageIcon(getClass().getResource("/images/logout.png")));
        logoutButton.setText("");
        messageScreen.add(logoutButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), null, new Dimension(30, 30), 0, false));
        chatsButton = new JButton();
        chatsButton.setBackground(new Color(-12829636));
        chatsButton.setBorderPainted(false);
        chatsButton.setFocusPainted(false);
        chatsButton.setForeground(new Color(-1));
        chatsButton.setIcon(new ImageIcon(getClass().getResource("/images/create_chat.png")));
        chatsButton.setText("");
        messageScreen.add(chatsButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), null, new Dimension(30, 30), 0, false));
        messageAreaScrollPane = new JScrollPane();
        messageAreaScrollPane.setBackground(new Color(-12829636));
        messageAreaScrollPane.setForeground(new Color(-1));
        messageAreaScrollPane.setHorizontalScrollBarPolicy(30);
        messageAreaScrollPane.setVerticalScrollBarPolicy(20);
        messageScreen.add(messageAreaScrollPane, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        messageSender.setBackground(new Color(-12829636));
        messageSender.setForeground(new Color(-1));
        messageAreaScrollPane.setViewportView(messageSender);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return messageScreen;
    }

}
