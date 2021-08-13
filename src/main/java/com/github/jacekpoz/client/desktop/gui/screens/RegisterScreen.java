package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.ChatWindow;
import com.github.jacekpoz.client.desktop.gui.Screen;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.database.queries.user.RegisterQuery;
import com.github.jacekpoz.common.sendables.database.results.RegisterResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.kosprov.jargon2.api.Jargon2.*;

public class RegisterScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(RegisterScreen.class.getName());

    private transient final ChatWindow window;

    private transient JPanel registerScreen;
    private transient JTextField nicknameField;
    private transient JButton registerButton;
    private transient JButton loginButton;
    private transient JLabel result;
    private transient JPasswordField passwordField;
    private transient JLabel passwordLabel;
    private transient JLabel nicknameLabel;
    private JButton settingsButton;

    public RegisterScreen(ChatWindow w) {
        window = w;

        nicknameField.addActionListener(e -> SwingUtilities.invokeLater(passwordField::requestFocusInWindow));
        ActionListener al = e -> register(nicknameField.getText(), passwordField.getPassword());
        passwordField.addActionListener(al);
        registerButton.addActionListener(al);
        loginButton.addActionListener(e -> window.setScreen(window.getLoginScreen()));

        settingsButton.addActionListener(e -> {
            window.setScreen(window.getSettingsScreen());
            window.setLastScreen(this);
        });
    }

    private void register(String username, char[] password) {

        if (username.isEmpty() || password.length == 0) {
            result.setText(window.getLangString("app.input_name_and_password"));
            return;
        }

        Hasher hasher = jargon2Hasher()
                .type(Type.ARGON2d)
                .memoryCost(65536)
                .timeCost(3)
                .parallelism(4)
                .saltLength(16)
                .hashLength(64);

        String hash = hasher
                .password(new String(password).getBytes(StandardCharsets.UTF_8))
                .encodedHash();

        window.send(new RegisterQuery(username, hash, getScreenID()));
    }

    @Override
    public JPanel getPanel() {
        return registerScreen;
    }

    @Override
    public void update() {

    }

    @Override
    public void updateUI() {
        result.setText("");
        nicknameField.setText("");
        passwordField.setText("");
    }

    @Override
    public void handleSendable(Sendable s) {
        if (s instanceof RegisterResult) {
            RegisterResult rr = (RegisterResult) s;
            switch (rr.getResult()) {
                case ACCOUNT_CREATED:
                    LOGGER.log(Level.INFO, "Account created.", rr.get().get(0));
                    result.setText(window.getLangString("app.account_created"));
                    break;
                case USERNAME_TAKEN:
                    LOGGER.log(Level.INFO, "Username taken.");
                    result.setText(window.getLangString("app.username_taken"));
                    break;
                case SQL_EXCEPTION:
                    LOGGER.log(Level.SEVERE, "An SQLException occured while registering.", rr.getEx());
                    result.setText(window.getLangString("app.sql_exception"));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public long getScreenID() {
        return 1;
    }

    @Override
    public void changeLanguage() {
        nicknameLabel.setText(window.getLangString("app.nickname"));
        passwordLabel.setText(window.getLangString("app.password"));
        registerButton.setText(window.getLangString("app.register"));
        loginButton.setText(window.getLangString("app.go_to_login"));

        loginButton.setMnemonic(loginButton.getText().charAt(0));
        registerButton.setMnemonic(registerButton.getText().charAt(0));
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
        registerScreen = new JPanel();
        registerScreen.setLayout(new GridLayoutManager(8, 1, new Insets(0, 0, 0, 0), -1, -1));
        registerScreen.setBackground(new Color(-12829636));
        registerScreen.setForeground(new Color(-12829636));
        nicknameLabel = new JLabel();
        nicknameLabel.setBackground(new Color(-12829636));
        nicknameLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(nicknameLabel, this.$$$getMessageFromBundle$$$("lang", "app.nickname"));
        registerScreen.add(nicknameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nicknameField = new JTextField();
        nicknameField.setBackground(new Color(-12829636));
        nicknameField.setCaretColor(new Color(-1));
        nicknameField.setForeground(new Color(-1));
        registerScreen.add(nicknameField, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        passwordLabel = new JLabel();
        passwordLabel.setBackground(new Color(-12829636));
        passwordLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(passwordLabel, this.$$$getMessageFromBundle$$$("lang", "app.password"));
        registerScreen.add(passwordLabel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        registerButton = new JButton();
        registerButton.setBackground(new Color(-12829636));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(registerButton, this.$$$getMessageFromBundle$$$("lang", "app.register"));
        registerScreen.add(registerButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loginButton = new JButton();
        loginButton.setBackground(new Color(-12829636));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setForeground(new Color(-1));
        this.$$$loadButtonText$$$(loginButton, this.$$$getMessageFromBundle$$$("lang", "app.go_to_login"));
        registerScreen.add(loginButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        result = new JLabel();
        result.setBackground(new Color(-12829636));
        result.setForeground(new Color(-1));
        result.setText("");
        registerScreen.add(result, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(-12829636));
        passwordField.setCaretColor(new Color(-1));
        passwordField.setForeground(new Color(-1));
        registerScreen.add(passwordField, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        settingsButton = new JButton();
        settingsButton.setBackground(new Color(-12829636));
        settingsButton.setBorderPainted(false);
        settingsButton.setFocusPainted(false);
        settingsButton.setForeground(new Color(-1));
        settingsButton.setHorizontalTextPosition(0);
        settingsButton.setIcon(new ImageIcon(getClass().getResource("/images/settings.png")));
        settingsButton.setText("");
        registerScreen.add(settingsButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(30, 30), 0, false));
        nicknameLabel.setLabelFor(nicknameField);
        passwordLabel.setLabelFor(passwordField);
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
        return registerScreen;
    }

}
