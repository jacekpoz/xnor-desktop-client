package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.ChatWindow;
import com.github.jacekpoz.client.desktop.gui.Screen;
import com.github.jacekpoz.client.desktop.gui.UserPanel;
import com.github.jacekpoz.common.Util;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.User;
import com.github.jacekpoz.common.sendables.database.queries.user.GetAllUsersQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetFriendRequestsQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetFriendsQuery;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class FriendsScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(FriendsScreen.class.getName());

    private transient final ChatWindow window;

    private transient JPanel friendsScreen;
    private transient JTabbedPane pane;
    private transient JPanel friendsPane;
    private transient JPanel addFriendsPane;
    private transient JPanel friendRequestsPane;
    private transient JTextField searchFriends;
    private transient JButton searchFriendsButton;
    private transient JTextField searchNewFriends;
    private transient JButton searchNewFriendsButton;
    private transient JPanel newFriendsList;
    private transient JPanel friendsList;
    private transient JScrollPane friendsScrollPane;
    private transient JScrollPane newFriendsScrollPane;
    private transient JButton backToMessagesButton;

    private List<User> friends;
    private List<User> friendRequests;
    private List<User> allUsers;

    public FriendsScreen(ChatWindow w) {
        window = w;
        friends = new ArrayList<>();
        friendRequests = new ArrayList<>();
        allUsers = new ArrayList<>();

        ActionListener searchFriendsAction = e -> {
            String username = searchFriends.getText();
            if (username.isEmpty()) {
                addUsersToPanel(friendsList, friends, UserPanel.FRIEND);
                return;
            }
            update();

            List<User> similarUsers = Util.compareUsernames(username, friends);
            if (similarUsers.isEmpty()) return;

            addUsersToPanel(friendsList, similarUsers, UserPanel.FRIEND);
        };

        searchFriends.addActionListener(searchFriendsAction);
        searchFriendsButton.addActionListener(searchFriendsAction);

        ActionListener searchNewFriendsAction = e -> {
            String username = searchNewFriends.getText();
            if (username.isEmpty()) return;
            update();

            List<User> similarUsers = Util.compareUsernames(username, allUsers);

            addUsersToPanel(newFriendsList, similarUsers, UserPanel.NOT_FRIEND);
        };

        searchFriends.addActionListener(searchNewFriendsAction);
        searchNewFriendsButton.addActionListener(searchNewFriendsAction);

        backToMessagesButton.addActionListener(e -> window.setScreen(window.getMessageScreen()));

        friendRequestsPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addUsersToPanel(friendRequestsPane, friendRequests, UserPanel.REQUEST);
            }
        });
    }

    private void addUsersToPanel(JPanel p, List<User> users, int type) {
        p.removeAll();
        for (User u : users)
            if (window.getClient().getUser().getId() != u.getId())
                p.add(new UserPanel(window, p, window.getClient().getUser(), u, type));
        p.revalidate();
    }

    @Override
    public JPanel getPanel() {
        return friendsScreen;
    }

    @Override
    public void update() {
        window.send(new GetFriendsQuery(window.getClient().getUser().getId(), getScreenID()));
        window.send(new GetFriendRequestsQuery(window.getClient().getUser().getId(), getScreenID()));
        window.send(new GetAllUsersQuery(getScreenID()));
    }

    @Override
    public void updateUI() {
        friendsList.removeAll();
        friendRequestsPane.removeAll();
        addUsersToPanel(friendsList, friends, UserPanel.FRIEND);
        addUsersToPanel(friendRequestsPane, friendRequests, UserPanel.REQUEST);
    }

    @Override
    public void handleSendable(Sendable s) {
        if (s instanceof UserResult) {
            UserResult ur = (UserResult) s;
            if (ur.getQuery() instanceof GetFriendsQuery) {
                friends = ur.get();
                addUsersToPanel(friendsList, friends, UserPanel.FRIEND);
            } else if (ur.getQuery() instanceof GetFriendRequestsQuery) {
                friendRequests = ur.get();
                addUsersToPanel(friendRequestsPane, friendRequests, UserPanel.REQUEST);
            } else if (ur.getQuery() instanceof GetAllUsersQuery) {
                allUsers = ur.get();
            }
            updateUI();
        }
    }

    @Override
    public long getScreenID() {
        return 3;
    }

    @Override
    public void changeLanguage() {
        backToMessagesButton.setText(window.getLangString("app.go_back"));
        pane.setTitleAt(0, window.getLangString("app.friends"));
        pane.setTitleAt(1, window.getLangString("app.add_friends"));
        pane.setTitleAt(2, window.getLangString("app.friend_requests"));
        searchFriendsButton.setText(window.getLangString("app.search"));
        searchNewFriendsButton.setText(window.getLangString("app.search"));
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
        friendsScreen = new JPanel();
        friendsScreen.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        friendsScreen.setBackground(new Color(-12829636));
        friendsScreen.setForeground(new Color(-1));
        pane = new JTabbedPane();
        pane.setBackground(new Color(-12829636));
        pane.setForeground(new Color(-1));
        friendsScreen.add(pane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        friendsPane = new JPanel();
        friendsPane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        friendsPane.setBackground(new Color(-12829636));
        friendsPane.setForeground(new Color(-1));
        pane.addTab(this.$$$getMessageFromBundle$$$("lang", "app.friends"), friendsPane);
        searchFriends = new JTextField();
        searchFriends.setBackground(new Color(-12829636));
        searchFriends.setDisabledTextColor(new Color(-1));
        searchFriends.setForeground(new Color(-1));
        friendsPane.add(searchFriends, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchFriendsButton = new JButton();
        searchFriendsButton.setBackground(new Color(-12829636));
        searchFriendsButton.setBorderPainted(false);
        searchFriendsButton.setFocusPainted(false);
        searchFriendsButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(searchFriendsButton, this.$$$getMessageFromBundle$$$("lang", "app.search"));
        friendsPane.add(searchFriendsButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        friendsScrollPane = new JScrollPane();
        friendsScrollPane.setBackground(new Color(-12829636));
        friendsScrollPane.setForeground(new Color(-1));
        friendsScrollPane.setHorizontalScrollBarPolicy(31);
        friendsPane.add(friendsScrollPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        friendsList = new JPanel();
        friendsList.setLayout(new GridBagLayout());
        friendsList.setBackground(new Color(-12829636));
        friendsList.setForeground(new Color(-1));
        friendsScrollPane.setViewportView(friendsList);
        addFriendsPane = new JPanel();
        addFriendsPane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        addFriendsPane.setBackground(new Color(-12829636));
        addFriendsPane.setForeground(new Color(-1));
        pane.addTab(this.$$$getMessageFromBundle$$$("lang", "app.add_friends"), addFriendsPane);
        searchNewFriends = new JTextField();
        searchNewFriends.setBackground(new Color(-12829636));
        searchNewFriends.setForeground(new Color(-1));
        addFriendsPane.add(searchNewFriends, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchNewFriendsButton = new JButton();
        searchNewFriendsButton.setBackground(new Color(-12829636));
        searchNewFriendsButton.setFocusPainted(false);
        searchNewFriendsButton.setForeground(new Color(-1));
        searchNewFriendsButton.setText("Szukaj");
        addFriendsPane.add(searchNewFriendsButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newFriendsScrollPane = new JScrollPane();
        newFriendsScrollPane.setBackground(new Color(-12829636));
        newFriendsScrollPane.setForeground(new Color(-1));
        newFriendsScrollPane.setHorizontalScrollBarPolicy(31);
        addFriendsPane.add(newFriendsScrollPane, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        newFriendsList = new JPanel();
        newFriendsList.setLayout(new GridBagLayout());
        newFriendsList.setBackground(new Color(-12829636));
        newFriendsList.setForeground(new Color(-1));
        newFriendsScrollPane.setViewportView(newFriendsList);
        friendRequestsPane = new JPanel();
        friendRequestsPane.setLayout(new GridBagLayout());
        friendRequestsPane.setBackground(new Color(-12829636));
        friendRequestsPane.setForeground(new Color(-1));
        pane.addTab(this.$$$getMessageFromBundle$$$("lang", "app.friend_requests"), friendRequestsPane);
        backToMessagesButton = new JButton();
        backToMessagesButton.setBackground(new Color(-12829636));
        backToMessagesButton.setBorderPainted(false);
        backToMessagesButton.setFocusPainted(false);
        backToMessagesButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(backToMessagesButton, this.$$$getMessageFromBundle$$$("lang", "app.go_back"));
        friendsScreen.add(backToMessagesButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        return friendsScreen;
    }

}
