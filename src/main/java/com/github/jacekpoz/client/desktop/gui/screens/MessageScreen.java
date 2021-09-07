package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.*;
import com.github.jacekpoz.common.Util;
import com.github.jacekpoz.common.sendables.*;
import com.github.jacekpoz.common.sendables.database.queries.chat.GetUsersChatsQuery;
import com.github.jacekpoz.common.sendables.database.queries.message.InsertMessageQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetMessageAuthorQuery;
import com.github.jacekpoz.common.sendables.database.queries.user.GetUsersInChatQuery;
import com.github.jacekpoz.common.sendables.database.results.ChatResult;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.commons.io.FilenameUtils;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
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
    private transient JTextPane messageArea;
    private transient JButton sendMessageButton;
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

    private long attachmentCounter = 0;
    private List<Attachment> attachments;

    private EmbeddedMediaPlayerComponent empc;

    public MessageScreen(ChatWindow w) {
        window = w;
        usersChats = new ArrayList<>();
        usersInChats = new HashMap<>();
        messageAuthors = new HashMap<>();

        attachments = new ArrayList<>();

        $$$setupUI$$$();
        messageArea.setBorder(BorderFactory.createLineBorder(Color.WHITE));

        Runnable sendMessageRunnable = () -> {
            if (!messageArea.getText().isEmpty() && messageArea.getComponents().length > 0) {
                Chat c = window.getClient().getChat();
                Message m = new Message(
                        c.getMessages().size(),
                        c.getChatID(),
                        window.getClient().getUser().getUserID(),
                        messageArea.getText(),
                        LocalDateTime.now()
                );
                m.getAttachments().addAll(attachments);
                m.getAttachments().clear();
                c.getMessages().add(m);
                sendMessage(m);
                addMessage(window.getClient().getUser(), m);
                attachmentCounter = 0;
                LOGGER.log(Level.INFO, "Sent message", m);
                messageArea.setText("");
                JScrollBar bar = messagesScrollPane.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            }
        };

        ActionListener sendMessageAction = e -> sendMessageRunnable.run();

        messageArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                    messageArea.setText(messageArea.getText() + "\n");
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessageRunnable.run();
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
                    Transferable clipboardContent = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

                    if (clipboardContent == null) {
                        e.consume();
                        return;
                    }
                    if (clipboardContent.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        try {
                            BufferedImage img = (BufferedImage) clipboardContent.getTransferData(DataFlavor.imageFlavor);
                            messageArea.insertIcon(new ImageIcon(img));
                            e.consume();
                        } catch (UnsupportedFlavorException | IOException ex) {
                            ex.printStackTrace();
                        }
                    } else if (clipboardContent.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        try {
                            List<File> files = (List<File>) clipboardContent.getTransferData(DataFlavor.javaFileListFlavor);

                            for (File file : files) {
                                System.out.println(FilenameUtils.getExtension(file.getPath()));
                                if (FilenameUtils.getExtension(file.getPath()).equals("mp4")) {
                                    empc = new EmbeddedMediaPlayerComponent();
                                    messageArea.insertComponent(empc);
                                    empc.mediaPlayer().media().play(file.getPath());
                                    System.out.println(empc);
                                }

                                Attachment a = new Attachment(
                                        attachmentCounter,
                                        messageArea.getText().length(),
                                        FilenameUtils.getExtension(file.getPath())
                                );

                                try (FileInputStream fis = new FileInputStream(file)) {
                                    int c;
                                    while ((c = fis.read()) != -1) {
                                        System.out.println((byte) c);
                                        a.getFileContents().add((byte) c);
                                    }
                                }

                                attachments.add(a);
                            }
                        } catch (UnsupportedFlavorException | IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
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
        chats.revalidate();
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
        c.getMessages().forEach(message -> addMessage(messageAuthors.get(message), message));
        messageArea.setEnabled(true);
        sendMessageButton.setEnabled(true);
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
            window.send(new GetUsersInChatQuery(c.getChatID(), getScreenID()));
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
            window.send(new GetUsersChatsQuery(window.getClient().getUser().getUserID(), getScreenID()));
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
                    if (c.getChatID() == gmaq.getChatID())
                        for (Message m : c.getMessages())
                            if (m.getMessageID() == gmaq.getMessageID())
                                messageAuthors.put(m, ur.get(0));
            } else if (ur.getQuery() instanceof GetUsersInChatQuery) {
                GetUsersInChatQuery guicq = (GetUsersInChatQuery) ur.getQuery();
                for (Chat c : usersChats) {
                    if (c.getChatID() == guicq.getChatID()) {
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
        sendMessageButton.setText(window.getLangString("app.send"));
    }


    private void createUIComponents() {
        messages = new MessageContainer(window);
        chats = new ChatsContainer();
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
        messagesScrollPane.setForeground(new Color(-1));
        messagesScrollPane.setHorizontalScrollBarPolicy(31);
        messagesScrollPane.setVerticalScrollBarPolicy(22);
        messageScreen.add(messagesScrollPane, new GridConstraints(0, 4, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        messageScreen.add(messageAreaScrollPane, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        messageArea = new JTextPane();
        messageArea.setBackground(new Color(-12829636));
        messageArea.setCaretColor(new Color(-1));
        messageArea.setContentType("text/plain");
        messageArea.setEditable(true);
        messageArea.setEnabled(false);
        messageArea.setForeground(new Color(-1));
        messageArea.setMargin(new Insets(0, 0, 0, 0));
        messageArea.setOpaque(true);
        messageAreaScrollPane.setViewportView(messageArea);
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

}
