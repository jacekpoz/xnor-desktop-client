package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.ChatWindow;
import com.github.jacekpoz.client.desktop.gui.Screen;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.User;
import com.github.jacekpoz.common.sendables.database.queries.chat.InsertChatQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetFriendsQuery;
import com.github.jacekpoz.common.sendables.database.results.ChatResult;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CreateChatsScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(CreateChatsScreen.class.getName());

    private transient final ChatWindow window;

    private List<User> friends;

    private transient JPanel createChatsScreen;
    private transient JButton backToMessagesButton;
    private transient JScrollPane friendsScrollPane;
    private transient JScrollPane addedFriendsScrollPane;
    private transient JList<User> friendsList;
    private transient JList<User> addedFriendsList;
    private transient JButton addButton;
    private transient JButton deleteButton;
    private transient JButton createChatButton;
    private transient JTextField chatNameTextField;
    private transient JLabel chatNameLabel;

    private transient final DefaultListModel<User> friendsListModel;
    private transient final DefaultListModel<User> addedFriendsListModel;

    public CreateChatsScreen(ChatWindow w) {
        window = w;

        friends = new ArrayList<>();

        friendsListModel = new DefaultListModel<>();
        friendsList.setModel(friendsListModel);
        addedFriendsListModel = new DefaultListModel<>();
        addedFriendsList.setModel(addedFriendsListModel);

        backToMessagesButton.addActionListener(e -> window.setScreen(window.getMessageScreen()));

        addButton.addActionListener(e -> {
            List<User> selected = friendsList.getSelectedValuesList();
            if (selected.isEmpty()) return;

            for (User u : selected) {
                friendsListModel.removeElement(u);
                addedFriendsListModel.addElement(u);
            }

            createChatButton.setEnabled(true);
            deleteButton.setEnabled(true);
            if (friendsListModel.isEmpty()) addButton.setEnabled(false);
        });

        deleteButton.addActionListener(e -> {
            List<User> selected = addedFriendsList.getSelectedValuesList();
            if (selected.isEmpty()) return;

            for (User u : selected) {
                addedFriendsListModel.removeElement(u);
                friendsListModel.addElement(u);
            }

            addButton.setEnabled(true);
            if (addedFriendsListModel.isEmpty()) {
                deleteButton.setEnabled(false);
                createChatButton.setEnabled(false);
            }
        });

        createChatButton.addActionListener(e -> {
            String inputName = chatNameTextField.getText();
            List<User> users = new ArrayList<>();
            users.add(window.getClient().getUser());
            for (int i = 0; i < addedFriendsListModel.size(); i++)
                users.add(addedFriendsListModel.get(i));

            String chatName = inputName;
            if (inputName.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < users.size(); i++) {
                    sb.append(users.get(i).getNickname());
                    if (i < users.size() - 1)
                        sb.append(", ");
                }
                chatName = sb.toString();
            }

            window.send(new InsertChatQuery(
                    chatName,
                    users.stream()
                            .map(User::getId)
                            .collect(Collectors.toList()),
                    getScreenID())
            );

        });
    }

    @Override
    public JPanel getPanel() {
        return createChatsScreen;
    }

    @Override
    public void update() {
        window.send(new GetFriendsQuery(window.getClient().getUser().getId(), getScreenID()));
    }

    @Override
    public void updateUI() {
        friendsListModel.clear();
        addedFriendsListModel.clear();
        chatNameTextField.setText("");
        for (User u : friends)
            friendsListModel.addElement(u);
        window.getMessageScreen().updateUI();
    }

    @Override
    public void handleSendable(Sendable s) {
        if (s instanceof UserResult) {
            UserResult ur = (UserResult) s;
            if (ur.getQuery() instanceof GetFriendsQuery) friends = ur.get();
        } else if (s instanceof ChatResult) {
            ChatResult cr = (ChatResult) s;
            if (cr.getQuery() instanceof InsertChatQuery) {
                InsertChatQuery icq = (InsertChatQuery) cr.getQuery();
                if (cr.success()) {
                    LOGGER.log(Level.INFO, "Created chat", icq.getChatName());
                    window.getMessageScreen().addChat(cr.get(0));
                    window.setScreen(window.getMessageScreen());
                }
            }
        }

    }

    @Override
    public long getScreenID() {
        return 4;
    }

    @Override
    public void changeLanguage() {
        backToMessagesButton.setText(window.getLangString("app.go_back"));
        chatNameLabel.setText(window.getLangString("app.chat_name"));
        addButton.setText(window.getLangString("app.add"));
        deleteButton.setText(window.getLangString("app.delete"));
        createChatButton.setText(window.getLangString("app.create_chat"));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createChatsScreen = new JPanel();
        createChatsScreen.setLayout(new GridLayoutManager(5, 3, new Insets(0, 0, 0, 0), -1, -1));
        createChatsScreen.setBackground(new Color(-12829636));
        createChatsScreen.setForeground(new Color(-1));
        backToMessagesButton = new JButton();
        backToMessagesButton.setBackground(new Color(-12829636));
        backToMessagesButton.setBorderPainted(false);
        backToMessagesButton.setFocusPainted(false);
        backToMessagesButton.setForeground(new Color(-1));
        backToMessagesButton.setOpaque(true);
        backToMessagesButton.setRolloverEnabled(false);
        backToMessagesButton.setSelected(false);
        this.$$$loadButtonText$$$(backToMessagesButton, this.$$$getMessageFromBundle$$$("lang", "app.go_back"));
        createChatsScreen.add(backToMessagesButton, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        friendsScrollPane = new JScrollPane();
        friendsScrollPane.setBackground(new Color(-12829636));
        friendsScrollPane.setForeground(new Color(-1));
        friendsScrollPane.setHorizontalScrollBarPolicy(31);
        createChatsScreen.add(friendsScrollPane, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        friendsList = new JList();
        friendsList.setBackground(new Color(-12829636));
        friendsList.setForeground(new Color(-1));
        friendsScrollPane.setViewportView(friendsList);
        addedFriendsScrollPane = new JScrollPane();
        addedFriendsScrollPane.setBackground(new Color(-12829636));
        addedFriendsScrollPane.setForeground(new Color(-1));
        addedFriendsScrollPane.setHorizontalScrollBarPolicy(31);
        createChatsScreen.add(addedFriendsScrollPane, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        addedFriendsList = new JList();
        addedFriendsList.setBackground(new Color(-12829636));
        addedFriendsList.setForeground(new Color(-1));
        addedFriendsScrollPane.setViewportView(addedFriendsList);
        addButton = new JButton();
        addButton.setBackground(new Color(-12829636));
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(addButton, this.$$$getMessageFromBundle$$$("lang", "app.add"));
        createChatsScreen.add(addButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setBackground(new Color(-12829636));
        deleteButton.setBorderPainted(false);
        deleteButton.setEnabled(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(deleteButton, this.$$$getMessageFromBundle$$$("lang", "app.delete"));
        createChatsScreen.add(deleteButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        createChatButton = new JButton();
        createChatButton.setBackground(new Color(-12829636));
        createChatButton.setBorderPainted(false);
        createChatButton.setEnabled(false);
        createChatButton.setFocusPainted(false);
        createChatButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(createChatButton, this.$$$getMessageFromBundle$$$("lang", "app.create_chat"));
        createChatsScreen.add(createChatButton, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chatNameTextField = new JTextField();
        chatNameTextField.setBackground(new Color(-12829636));
        chatNameTextField.setCaretColor(new Color(-1));
        chatNameTextField.setForeground(new Color(-1));
        chatNameTextField.setText("");
        createChatsScreen.add(chatNameTextField, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        chatNameLabel = new JLabel();
        chatNameLabel.setBackground(new Color(-12829636));
        chatNameLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(chatNameLabel, this.$$$getMessageFromBundle$$$("lang", "app.chat_name"));
        createChatsScreen.add(chatNameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        chatNameLabel.setLabelFor(chatNameTextField);
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
    private void $$$loadLabelText$$$(JLabel component, String text) {
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
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
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
        return createChatsScreen;
    }

}
