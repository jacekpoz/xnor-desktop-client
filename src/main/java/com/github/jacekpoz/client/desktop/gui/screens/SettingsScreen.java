package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.XnorWindow;
import com.github.jacekpoz.client.desktop.gui.Screen;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.database.queries.UserQuery;
import com.github.jacekpoz.common.sendables.database.queries.UserQueryEnum;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(SettingsScreen.class.getName());

    private final XnorWindow window;

    private JPanel settingsScreen;
    private JComboBox<Locale> languageComboBox;
    private JLabel languageLabel;
    private JButton goBackButton;
    private JTextField logFilesTextField;
    private JButton chooseDirectoryButton;
    private JLabel logFilesLabel;
    private JLabel resultLabel;
    private JButton saveLogPathButton;
    private JButton deleteAccountButton;

    private JFileChooser chooser;

    private Locale lang;

    public SettingsScreen(XnorWindow w) {
        window = w;
        languageComboBox.addItem(Locale.US);
        languageComboBox.addItem(new Locale("pl", "PL"));
        languageComboBox.addItem(new Locale("es", "ES"));

        lang = Locale.US;

        languageComboBox.addItemListener(itemEvent -> updateLanguage());

        chooseDirectoryButton.addActionListener(e -> {
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setDialogTitle(window.getLanguageBundle().getString("app.log_location_chooser_title"));
            if (chooser.showOpenDialog(getPanel()) == JFileChooser.APPROVE_OPTION) {
                logFilesTextField.setText(chooser.getCurrentDirectory().toPath().toString());
            }
        });

        saveLogPathButton.addActionListener(e -> {
            Path logDirectoryPath = new File(logFilesTextField.getText()).toPath();
            if (Files.isDirectory(logDirectoryPath)) {
                LOGGER.log(Level.INFO, "Changed log directory", logDirectoryPath);
                window.changeLogDirectory(logDirectoryPath.toString());
            } else {
                resultLabel.setText(window.getLanguageBundle().getString("app.invalid_path"));
                window.pack();
            }
        });

        goBackButton.addActionListener(e -> window.setScreen(window.getLastScreen()));

        deleteAccountButton.addActionListener(e -> {
            Object[] options = {window.getLangString("app.yes"), window.getLangString("app.no")};
            int result = JOptionPane.showOptionDialog(
                    window,
                    window.getLangString("app.are_you_sure"),
                    "",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    null
            );
            if (result == JOptionPane.YES_OPTION) {
                UserQuery delete = new UserQuery(
                        false,
                        getScreenID(),
                        UserQueryEnum.DELETE_USER
                );
                delete.putValue("userID", window.getClient().getUser().getUserID());
                window.send(delete);
                window.logout();
            }
        });
    }

    private void updateLanguage() {
        Locale newLang = languageComboBox.getItemAt(languageComboBox.getSelectedIndex());
        if (lang.equals(newLang)) return;
        lang = newLang;
        window.changeLanguage(lang);
        LOGGER.log(Level.INFO, "Changed app language", lang);
    }

    @Override
    public JPanel getPanel() {
        return settingsScreen;
    }

    @Override
    public void update() {
        updateLanguage();
    }

    @Override
    public void updateUI() {
        if (window.getClient().isLoggedIn()) {
            deleteAccountButton.setVisible(true);
            deleteAccountButton.setEnabled(true);
        } else if (!window.getClient().isLoggedIn()) {
            deleteAccountButton.setVisible(false);
            deleteAccountButton.setEnabled(false);
        }
        window.pack();
    }

    @Override
    public void handleSendable(Sendable s) {
        if (s instanceof UserResult) {
            UserResult ur = (UserResult) s;
            if (ur.getQuery().getQueryType() == UserQueryEnum.DELETE_USER) {
                LOGGER.log(Level.INFO, "Account deleted", ur.get().get(0));
            }
        }
    }

    @Override
    public long getScreenID() {
        return 5;
    }

    @Override
    public void changeLanguage() {
        languageLabel.setText(window.getLangString("app.language"));
        goBackButton.setText(window.getLangString("app.go_back"));
        chooseDirectoryButton.setText(window.getLangString("app.choose_directory"));
        logFilesLabel.setText(window.getLangString("app.log_file_location"));
        saveLogPathButton.setText(window.getLangString("app.save_path"));
        deleteAccountButton.setText(window.getLangString("app.delete_account"));
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
        settingsScreen = new JPanel();
        settingsScreen.setLayout(new GridLayoutManager(5, 4, new Insets(0, 0, 0, 0), -1, -1));
        settingsScreen.setBackground(new Color(-12829636));
        settingsScreen.setForeground(new Color(-1));
        languageLabel = new JLabel();
        languageLabel.setBackground(new Color(-12829636));
        languageLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(languageLabel, this.$$$getMessageFromBundle$$$("lang", "app.language"));
        settingsScreen.add(languageLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        languageComboBox = new JComboBox();
        languageComboBox.setBackground(new Color(-12829636));
        languageComboBox.setForeground(new Color(-1));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        languageComboBox.setModel(defaultComboBoxModel1);
        settingsScreen.add(languageComboBox, new GridConstraints(1, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        goBackButton = new JButton();
        goBackButton.setBackground(new Color(-12829636));
        goBackButton.setBorderPainted(false);
        goBackButton.setFocusPainted(false);
        goBackButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(goBackButton, this.$$$getMessageFromBundle$$$("lang", "app.go_back"));
        settingsScreen.add(goBackButton, new GridConstraints(0, 0, 1, 4, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logFilesLabel = new JLabel();
        logFilesLabel.setBackground(new Color(-12829636));
        logFilesLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(logFilesLabel, this.$$$getMessageFromBundle$$$("lang", "app.log_file_location"));
        settingsScreen.add(logFilesLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logFilesTextField = new JTextField();
        logFilesTextField.setBackground(new Color(-12829636));
        logFilesTextField.setCaretColor(new Color(-1));
        logFilesTextField.setForeground(new Color(-1));
        settingsScreen.add(logFilesTextField, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        chooseDirectoryButton = new JButton();
        chooseDirectoryButton.setBackground(new Color(-12829636));
        chooseDirectoryButton.setBorderPainted(false);
        chooseDirectoryButton.setFocusPainted(false);
        chooseDirectoryButton.setForeground(new Color(-1));
        chooseDirectoryButton.setHideActionText(true);
        this.$$$loadButtonText$$$(chooseDirectoryButton, this.$$$getMessageFromBundle$$$("lang", "app.choose_directory"));
        settingsScreen.add(chooseDirectoryButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resultLabel = new JLabel();
        resultLabel.setBackground(new Color(-12829636));
        resultLabel.setForeground(new Color(-1));
        resultLabel.setText("");
        settingsScreen.add(resultLabel, new GridConstraints(4, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveLogPathButton = new JButton();
        saveLogPathButton.setBackground(new Color(-12829636));
        saveLogPathButton.setBorderPainted(false);
        saveLogPathButton.setFocusPainted(false);
        saveLogPathButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(saveLogPathButton, this.$$$getMessageFromBundle$$$("lang", "app.save_path"));
        settingsScreen.add(saveLogPathButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteAccountButton = new JButton();
        deleteAccountButton.setBackground(new Color(-65536));
        deleteAccountButton.setBorderPainted(false);
        deleteAccountButton.setEnabled(false);
        deleteAccountButton.setForeground(new Color(-1));
        deleteAccountButton.setLabel("Usuń konto");
        this.$$$loadButtonText$$$(deleteAccountButton, this.$$$getMessageFromBundle$$$("lang", "app.delete_account"));
        deleteAccountButton.setVisible(false);
        settingsScreen.add(deleteAccountButton, new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        languageLabel.setLabelFor(languageComboBox);
        logFilesLabel.setLabelFor(logFilesTextField);
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
        return settingsScreen;
    }
}
