package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.*;
import com.github.jacekpoz.common.Util;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.Message;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.User;
import com.github.jacekpoz.common.sendables.database.queries.chat.GetUsersChatsQuery;
import com.github.jacekpoz.common.sendables.database.queries.message.InsertMessageQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetMessageAuthorQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetUsersInChatQuery;
import com.github.jacekpoz.common.sendables.database.results.ChatResult;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(MessageScreen.class.getName());

    private transient final ChatWindow window;

    private transient JPanel messageScreen;
    private transient JButton chatsButton;
    private transient JTextField messageField;
    private transient JButton sendMessageButton;
    private transient JButton friendsButton;
    private transient MessageContainer messages;
    private transient JScrollPane messagesScrollPane;
    private transient ChatsContainer chats;
    private transient JScrollPane chatsScrollPane;
    private transient JButton settingsButton;
    private JButton logoutButton;

    private List<Chat> usersChats;
    private Map<Chat, List<User>> usersInChats;
    private Map<Message, User> messageAuthors;

    public MessageScreen(ChatWindow w) {
        window = w;
        usersChats = new ArrayList<>();
        usersInChats = new HashMap<>();
        messageAuthors = new HashMap<>();

        $$$setupUI$$$();
        ActionListener sendMessageAction = e -> {
            if (!messageField.getText().isEmpty()) {
                Chat c = window.getClient().getChat();
                Message m = new Message(
                        c.getMessages().size(),
                        c.getId(),
                        window.getClient().getUser().getId(),
                        messageField.getText(),
                        LocalDateTime.now()
                );
                c.getMessages().add(m);
                sendMessage(m);
                messages.addMessage(new MessagePanel(
                        window.getClient().getUser(),
                        window.getClient().getUser(),
                        m
                ));
                LOGGER.log(Level.INFO, "Sent message", m);
                messageField.setText("");
                JScrollBar bar = messagesScrollPane.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            }
        };

        messageField.addActionListener(sendMessageAction);
        sendMessageButton.addActionListener(sendMessageAction);
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
    }

    private void sendMessage(Message message) {
        window.send(message);
        window.send(new InsertMessageQuery(
                message.getMessageID(),
                message.getChatID(),
                message.getAuthorID(),
                message.getContent(),
                getScreenID()
        ));
    }

    public void setChat(Chat c) {
        window.getClient().setChat(c);
        window.send(c);
        messages.removeAllMessages();
        c.getMessages().forEach(message ->
                messages.addMessage(new MessagePanel(
                        window.getClient().getUser(),
                        messageAuthors.get(message),
                        message
                ))
        );
        messageField.setEnabled(true);
        sendMessageButton.setEnabled(true);
        messages.revalidate();
        messages.repaint();
    }

    @Override
    public JPanel getPanel() {
        return messageScreen;
    }

    private void updateUsersChats() {

        for (Chat c : usersChats) {
            window.send(new GetUsersInChatQuery(c.getId(), getScreenID()));
            for (Message m : c.getMessages()) {
                window.send(new GetMessageAuthorQuery(
                        m.getMessageID(),
                        m.getChatID(),
                        m.getAuthorID(),
                        getScreenID()
                ));
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
            window.send(new GetUsersChatsQuery(window.getClient().getUser().getId(), getScreenID()));
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
            if (cr.getQuery() instanceof GetUsersChatsQuery) {
                usersChats = cr.get();
                updateUI();
                updateUsersChats();
            }
        } else if (s instanceof UserResult) {
            UserResult ur = (UserResult) s;
            if (ur.getQuery() instanceof GetMessageAuthorQuery) {
                GetMessageAuthorQuery gmaq = (GetMessageAuthorQuery) ur.getQuery();
                for (Chat c : usersChats)
                    if (c.getId() == gmaq.getChatID())
                        for (Message m : c.getMessages())
                            if (m.getMessageID() == gmaq.getMessageID())
                                messageAuthors.put(m, ur.get(0));
            } else if (ur.getQuery() instanceof GetUsersInChatQuery) {
                GetUsersInChatQuery guicq = (GetUsersInChatQuery) ur.getQuery();
                for (Chat c : usersChats) {
                    if (c.getId() == guicq.getChatID()) {
                        usersInChats.put(c, ur.get());
                        return;
                    }
                }
            }
        } else if (s instanceof Message) {
            Message m = (Message) s;
            for (User u : usersInChats.get(window.getClient().getChat())) {
                if (u.getId() == m.getAuthorID()) {
                    messageAuthors.put(m, u);
                    messages.addMessage(new MessagePanel(
                            window.getClient().getUser(),
                            u,
                            m
                    ));
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
        sendMessageButton.setText(window.getLangString("app.send"));
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
        messageScreen.setLayout(new GridLayoutManager(3, 6, new Insets(0, 0, 0, 0), 5, 5));
        messageScreen.setBackground(new Color(-12829636));
        messageScreen.setForeground(new Color(-1));
        messageField = new JTextField();
        messageField.setBackground(new Color(-12829636));
        messageField.setCaretColor(new Color(-1));
        messageField.setEditable(true);
        messageField.setEnabled(false);
        messageField.setForeground(new Color(-1));
        messageScreen.add(messageField, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, 25), new Dimension(150, 25), null, 0, false));
        sendMessageButton = new JButton();
        sendMessageButton.setBackground(new Color(-12829636));
        sendMessageButton.setBorderPainted(false);
        sendMessageButton.setEnabled(false);
        sendMessageButton.setFocusPainted(false);
        sendMessageButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(sendMessageButton, this.$$$getMessageFromBundle$$$("lang", "app.send"));
        messageScreen.add(sendMessageButton, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(75, 25), new Dimension(75, 25), 0, false));
        messagesScrollPane = new JScrollPane();
        messagesScrollPane.setBackground(new Color(-12829636));
        messagesScrollPane.setDoubleBuffered(false);
        messagesScrollPane.setForeground(new Color(-1));
        messagesScrollPane.setVerticalScrollBarPolicy(22);
        messageScreen.add(messagesScrollPane, new GridConstraints(0, 4, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        messages.setAutoscrolls(false);
        messages.setBackground(new Color(-12829636));
        messages.setDoubleBuffered(true);
        messages.setFocusCycleRoot(false);
        messages.setFocusTraversalPolicyProvider(false);
        messages.setForeground(new Color(-1));
        messages.setInheritsPopupMenu(false);
        messages.setMaximumSize(new Dimension(-1, -1));
        messages.setMinimumSize(new Dimension(250, 100));
        messages.setPreferredSize(new Dimension(350, 150));
        messages.setRequestFocusEnabled(true);
        messagesScrollPane.setViewportView(messages);
        chatsScrollPane = new JScrollPane();
        chatsScrollPane.setBackground(new Color(-12829636));
        chatsScrollPane.setForeground(new Color(-1));
        chatsScrollPane.setVerticalScrollBarPolicy(22);
        messageScreen.add(chatsScrollPane, new GridConstraints(1, 0, 2, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(50, 150), new Dimension(100, 250), null, 0, false));
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
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return messageScreen;
    }

    private void createUIComponents() {
        messages = new MessageContainer(window);
    }
}
