package com.github.jacekpoz.client.desktop.gui;

import com.github.jacekpoz.common.sendables.Attachment;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.Message;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageSender extends JPanel {
    private final XnorWindow window;

    private long attachmentCounter;
    private final List<Attachment> attachments;

    private JButton addFileButton;
    private JTextPane textPane;
    private JButton sendMessageButton;

    public MessageSender(XnorWindow w) {
        window = w;
        attachmentCounter = 0;
        attachments = new ArrayList<>();

        setupUI();

        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                    textPane.setText(textPane.getText() + "\n");
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
                    Transferable clipboardContent = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

                    if (clipboardContent == null) {
                        e.consume();
                        return;
                    }
                    /*if (clipboardContent.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                        try {
                            BufferedImage img = (BufferedImage) clipboardContent.getTransferData(DataFlavor.imageFlavor);
                            messageArea.insertIcon(new ImageIcon(img));
                            e.consume();
                        } catch (UnsupportedFlavorException | IOException ex) {
                            ex.printStackTrace();
                        }
                    } else */
                    if (clipboardContent.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        try {
                            List<File> files = (List<File>) clipboardContent.getTransferData(DataFlavor.javaFileListFlavor);

                            for (File file : files) {
                                String path = file.getPath();

                                Attachment a = new Attachment(
                                        attachmentCounter,
                                        (long) textPane.getText().length(),
                                        FilenameUtils.getBaseName(path),
                                        FilenameUtils.getExtension(path)
                                );

                                try (FileReader reader = new FileReader(file)) {
                                    int c;
                                    while ((c = reader.read()) != -1) {
                                        a.getFileContents().add((byte) c);
                                    }
                                }

                                attachments.add(a);
                                textPane.insertComponent(new AttachmentPanel(textPane, a, false).$$$getRootComponent$$$());
                            }
                        } catch (UnsupportedFlavorException | IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });

        addFileButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                File[] files = chooser.getSelectedFiles();
                if (files == null || files.length == 0) return;
                for (File f : files) {
                    Attachment a = new Attachment(
                            attachmentCounter,
                            (long) textPane.getText().length(),
                            FilenameUtils.getBaseName(f.getPath()),
                            FilenameUtils.getExtension(f.getPath())
                    );
                    textPane.insertComponent(new AttachmentPanel(textPane, a, false).$$$getRootComponent$$$());
                }
            }
        });
    }

    // run the runnable - textPane (on enter click) and sendMessageButton (on button click obviously)
    public void addListener(Runnable runnable) {
        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    runnable.run();
                }
            }
        });
        sendMessageButton.addActionListener(e -> runnable.run());
    }

    public Message getMessage() {
        if (!textPane.getText().isEmpty() && textPane.getComponents().length > 0) {
            Chat c = window.getClient().getChat();
            Message m = new Message(
                    c.getMessageCounter(),
                    c.getChatID(),
                    window.getClient().getUser().getUserID(),
                    textPane.getText(),
                    LocalDateTime.now()
            );
            m.getAttachments().addAll(attachments);
            m.getAttachments().clear();
            c.getMessages().add(m);
            attachmentCounter = 0;
            textPane.setText("");
            return m;
        }
        return null;
    }

    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        addFileButton = new JButton(new ImageIcon(Objects.requireNonNull(getClass().getResource("/images/add_file.png"))));
        addFileButton.setBackground(new Color(-12829636));
        addFileButton.setForeground(new Color(-1));
        addFileButton.setBorderPainted(false);
        addFileButton.setEnabled(false);
        addFileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addFileButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        textPane = new JTextPane();
        textPane.setBackground(new Color(-12829636));
        textPane.setForeground(new Color(-1));
        textPane.setEnabled(false);
        textPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        textPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPane.setAlignmentY(Component.BOTTOM_ALIGNMENT);
        textPane.setContentType("text/html");
        sendMessageButton = new JButton(window.getLangString("app.send"));
        sendMessageButton.setBackground(new Color(-12829636));
        sendMessageButton.setForeground(new Color(-1));
        sendMessageButton.setBorderPainted(false);
        sendMessageButton.setEnabled(false);
        sendMessageButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        sendMessageButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        add(addFileButton);
        add(textPane);
        add(sendMessageButton);
    }

    @Override
    public void setEnabled(boolean enabled) {
        textPane.setEnabled(enabled);
        addFileButton.setEnabled(enabled);
        sendMessageButton.setEnabled(enabled);
    }

    public void changeLanguage() {
        sendMessageButton.setText(window.getLangString("app.send"));
    }
}
