package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.XnorWindow;
import com.github.jacekpoz.client.desktop.gui.Screen;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.database.queries.UserQuery;
import com.github.jacekpoz.common.sendables.database.queries.UserQueryEnum;
import com.github.jacekpoz.common.sendables.database.results.UserResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
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
        languageComboBox.addItem(new Locale("lol", "US"));

        lang = Locale.US;

        languageComboBox.addItemListener(itemEvent -> updateLanguage());

        logFilesTextField.setText(window.getClient().readFromSettingsFile("logDirectory"));

        chooseDirectoryButton.addActionListener(e -> {
            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(window.getClient().readFromSettingsFile("logDirectory")));
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
                window.getClient().writeToSettingsFile("logDirectory", logDirectoryPath);
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
        window.getClient().writeToSettingsFile("language", lang);
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
        settingsScreen.setLayout(new GridLayoutManager(6, 6, new Insets(0, 0, 0, 0), -1, -1));
        settingsScreen.setBackground(new Color(-12829636));
        settingsScreen.setForeground(new Color(-1));
        languageLabel = new JLabel();
        languageLabel.setBackground(new Color(-12829636));
        Font languageLabelFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, languageLabel.getFont());
        if (languageLabelFont != null) languageLabel.setFont(languageLabelFont);
        languageLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(languageLabel, this.$$$getMessageFromBundle$$$("lang", "app.language"));
        settingsScreen.add(languageLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        languageComboBox = new JComboBox();
        languageComboBox.setBackground(new Color(-11513776));
        Font languageComboBoxFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, languageComboBox.getFont());
        if (languageComboBoxFont != null) languageComboBox.setFont(languageComboBoxFont);
        languageComboBox.setForeground(new Color(-1));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        languageComboBox.setModel(defaultComboBoxModel1);
        settingsScreen.add(languageComboBox, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logFilesLabel = new JLabel();
        logFilesLabel.setBackground(new Color(-12829636));
        Font logFilesLabelFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, logFilesLabel.getFont());
        if (logFilesLabelFont != null) logFilesLabel.setFont(logFilesLabelFont);
        logFilesLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(logFilesLabel, this.$$$getMessageFromBundle$$$("lang", "app.log_file_location"));
        settingsScreen.add(logFilesLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        logFilesTextField = new JTextField();
        logFilesTextField.setBackground(new Color(-11513776));
        logFilesTextField.setCaretColor(new Color(-1));
        Font logFilesTextFieldFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, logFilesTextField.getFont());
        if (logFilesTextFieldFont != null) logFilesTextField.setFont(logFilesTextFieldFont);
        logFilesTextField.setForeground(new Color(-1));
        settingsScreen.add(logFilesTextField, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        resultLabel = new JLabel();
        resultLabel.setBackground(new Color(-12829636));
        Font resultLabelFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, resultLabel.getFont());
        if (resultLabelFont != null) resultLabel.setFont(resultLabelFont);
        resultLabel.setForeground(new Color(-1));
        resultLabel.setText("");
        settingsScreen.add(resultLabel, new GridConstraints(5, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteAccountButton = new JButton();
        deleteAccountButton.setBackground(new Color(-65536));
        deleteAccountButton.setBorderPainted(false);
        deleteAccountButton.setEnabled(false);
        Font deleteAccountButtonFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, deleteAccountButton.getFont());
        if (deleteAccountButtonFont != null) deleteAccountButton.setFont(deleteAccountButtonFont);
        deleteAccountButton.setForeground(new Color(-1));
        deleteAccountButton.setLabel("");
        this.$$$loadButtonText$$$(deleteAccountButton, this.$$$getMessageFromBundle$$$("lang", "app.delete_account"));
        deleteAccountButton.setVisible(false);
        settingsScreen.add(deleteAccountButton, new GridConstraints(4, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        settingsScreen.add(spacer1, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        saveLogPathButton = new JButton();
        saveLogPathButton.setBackground(new Color(-11513776));
        saveLogPathButton.setBorderPainted(false);
        saveLogPathButton.setFocusPainted(false);
        Font saveLogPathButtonFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, saveLogPathButton.getFont());
        if (saveLogPathButtonFont != null) saveLogPathButton.setFont(saveLogPathButtonFont);
        saveLogPathButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(saveLogPathButton, this.$$$getMessageFromBundle$$$("lang", "app.save_path"));
        settingsScreen.add(saveLogPathButton, new GridConstraints(2, 4, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(120, -1), null, 0, false));
        chooseDirectoryButton = new JButton();
        chooseDirectoryButton.setBackground(new Color(-11513776));
        chooseDirectoryButton.setBorderPainted(false);
        chooseDirectoryButton.setFocusPainted(false);
        Font chooseDirectoryButtonFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, chooseDirectoryButton.getFont());
        if (chooseDirectoryButtonFont != null) chooseDirectoryButton.setFont(chooseDirectoryButtonFont);
        chooseDirectoryButton.setForeground(new Color(-1));
        chooseDirectoryButton.setHideActionText(true);
        this.$$$loadButtonText$$$(chooseDirectoryButton, this.$$$getMessageFromBundle$$$("lang", "app.choose_directory"));
        settingsScreen.add(chooseDirectoryButton, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(120, -1), null, 0, false));
        goBackButton = new JButton();
        goBackButton.setBackground(new Color(-11513776));
        goBackButton.setBorderPainted(false);
        goBackButton.setFocusPainted(false);
        Font goBackButtonFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, goBackButton.getFont());
        if (goBackButtonFont != null) goBackButton.setFont(goBackButtonFont);
        goBackButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(goBackButton, this.$$$getMessageFromBundle$$$("lang", "app.go_back"));
        settingsScreen.add(goBackButton, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        settingsScreen.add(spacer2, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        languageLabel.setLabelFor(languageComboBox);
        logFilesLabel.setLabelFor(logFilesTextField);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
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
