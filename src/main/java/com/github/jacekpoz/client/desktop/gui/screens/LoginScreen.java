package com.github.jacekpoz.client.desktop.gui.screens;

import com.github.jacekpoz.client.desktop.gui.XnorWindow;
import com.github.jacekpoz.client.desktop.gui.Screen;
import com.github.jacekpoz.common.sendables.Sendable;
import com.github.jacekpoz.common.sendables.User;
import com.github.jacekpoz.common.sendables.database.queries.UserQuery;
import com.github.jacekpoz.common.sendables.database.queries.UserQueryEnum;
import com.github.jacekpoz.common.sendables.database.results.LoginResult;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginScreen implements Screen {

    private final static Logger LOGGER = Logger.getLogger(LoginScreen.class.getName());

    private transient final XnorWindow window;

    private transient JPanel loginScreen;
    private transient JTextField nicknameField;
    private transient JPasswordField passwordField;
    private transient JButton loginButton;
    private transient JButton registerButton;
    private transient JLabel result;
    private transient JLabel nicknameLabel;
    private transient JLabel passwordLabel;
    private JButton settingsButton;
    private JCheckBox rememberMeCheckBox;

    public LoginScreen(XnorWindow w) {
        window = w;
        nicknameField.addActionListener(e -> SwingUtilities.invokeLater(passwordField::requestFocusInWindow));
        ActionListener al = e -> login(nicknameField.getText(), passwordField.getPassword(), false);
        passwordField.addActionListener(al);
        loginButton.addActionListener(al);
        registerButton.addActionListener(e -> window.setScreen(window.getRegisterScreen()));

        settingsButton.addActionListener(e -> {
            window.setScreen(window.getSettingsScreen());
            window.setLastScreen(this);
        });

        try {
            File f = new File(System.getenv("APPDATA") + "/xnor/settings.txt");
            Scanner reader = new Scanner(f);

            boolean autoLogin = false;
            String usernameFromFile = "";
            char[] passwordFromFile = new char[0];

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line.startsWith("AutoLogin")) {
                    autoLogin = Boolean.parseBoolean(line.split(" : ")[1]);
                }
                if (line.startsWith("Username")) {
                    usernameFromFile = line.split(" : ")[1];
                }
                if (line.startsWith("Password")) {
                    String pw = line.split(" : ")[1];
                    passwordFromFile = pw.toCharArray();
                }
            }

            if (autoLogin) {
                login(usernameFromFile, passwordFromFile, true);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (FileNotFoundException ex) {
            window.getClient().initializeAppDataDirectory();
            new LoginScreen(window);
        }
    }

    private void login(String username, char[] password, boolean autologin) {

        if (username.isEmpty() || password.length == 0) {
            result.setText(window.getLangString("app.input_name_and_password"));
            return;
        }

        UserQuery login = new UserQuery(
                false,
                getScreenID(),
                UserQueryEnum.LOGIN
        );
        login.putValue("username", username);
        login.putValue("password", new String(password).getBytes(StandardCharsets.UTF_8));

        if (!autologin) {
            window.getClient().writeToSettingsFile("AutoLogin", rememberMeCheckBox.isSelected());
            window.getClient().writeToSettingsFile("Username", username);
            window.getClient().writeToSettingsFile("Password", password);
        }

        window.send(login);
    }

    @Override
    public JPanel getPanel() {
        return loginScreen;
    }

    @Override
    public void update() {
        if (window.getClient().isLoggedIn())
            for (Screen s : window.getScreens())
                if (!(s instanceof LoginScreen)) {
                    s.update();
                    s.updateUI();
                }
        updateUI();
    }

    @Override
    public void updateUI() {
        result.setText("");
        nicknameField.setText("");
        passwordField.setText("");
    }

    @Override
    public void handleSendable(Sendable s) {
        if (s instanceof LoginResult) {
            LoginResult lr = (LoginResult) s;

            switch (lr.getResult()) {
                case LOGGED_IN: {
                    User u = lr.get().get(0);
                    window.getClient().setUser(u);
                    window.getClient().setLoggedIn(true);
                    window.setScreen(window.getMessageScreen());
                    update();
                    result.setText(window.getLangString("app.logged_in"));
                    LOGGER.log(Level.INFO, "Logged in", u);
                    break;
                }
                case ACCOUNT_DOESNT_EXIST:
                    LOGGER.log(Level.INFO, "Account doesn't exist");
                    result.setText(window.getLangString("app.account_doesnt_exist"));
                    break;
                case WRONG_PASSWORD:
                    LOGGER.log(Level.INFO, "Wrong password");
                    result.setText(window.getLangString("app.wrong_password"));
                    break;
                case SQL_EXCEPTION:
                    LOGGER.log(Level.SEVERE, "An SQLException occured while logging in ", lr.getEx());
                    result.setText(window.getLangString("app.sql_exception"));
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    @Override
    public long getScreenID() {
        return 0;
    }

    @Override
    public void changeLanguage() {
        nicknameLabel.setText(window.getLangString("app.nickname"));
        passwordLabel.setText(window.getLangString("app.password"));
        loginButton.setText(window.getLangString("app.login"));
        registerButton.setText(window.getLangString("app.go_to_register"));

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
        loginScreen = new JPanel();
        loginScreen.setLayout(new GridLayoutManager(12, 1, new Insets(0, 0, 0, 0), -1, -1));
        loginScreen.setBackground(new Color(-12829636));
        loginScreen.setForeground(new Color(-1));
        loginScreen.setVisible(true);
        nicknameLabel = new JLabel();
        nicknameLabel.setBackground(new Color(-12829636));
        nicknameLabel.setEnabled(true);
        Font nicknameLabelFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, nicknameLabel.getFont());
        if (nicknameLabelFont != null) nicknameLabel.setFont(nicknameLabelFont);
        nicknameLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(nicknameLabel, this.$$$getMessageFromBundle$$$("lang", "app.nickname"));
        loginScreen.add(nicknameLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordLabel = new JLabel();
        passwordLabel.setBackground(new Color(-12829636));
        passwordLabel.setEnabled(true);
        Font passwordLabelFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, passwordLabel.getFont());
        if (passwordLabelFont != null) passwordLabel.setFont(passwordLabelFont);
        passwordLabel.setForeground(new Color(-1));
        this.$$$loadLabelText$$$(passwordLabel, this.$$$getMessageFromBundle$$$("lang", "app.password"));
        loginScreen.add(passwordLabel, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        passwordField = new JPasswordField();
        passwordField.setBackground(new Color(-12829636));
        passwordField.setCaretColor(new Color(-1));
        Font passwordFieldFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, passwordField.getFont());
        if (passwordFieldFont != null) passwordField.setFont(passwordFieldFont);
        passwordField.setForeground(new Color(-1));
        loginScreen.add(passwordField, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        loginButton = new JButton();
        loginButton.setBackground(new Color(-11513776));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        Font loginButtonFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, loginButton.getFont());
        if (loginButtonFont != null) loginButton.setFont(loginButtonFont);
        loginButton.setForeground(new Color(-1));
        loginButton.setInheritsPopupMenu(false);
        loginButton.setLabel("");
        this.$$$loadButtonText$$$(loginButton, this.$$$getMessageFromBundle$$$("lang", "app.login"));
        loginScreen.add(loginButton, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, -1), new Dimension(120, -1), null, 0, false));
        registerButton = new JButton();
        registerButton.setBackground(new Color(-11513776));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        Font registerButtonFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, registerButton.getFont());
        if (registerButtonFont != null) registerButton.setFont(registerButtonFont);
        registerButton.setForeground(new Color(-1));
        registerButton.setLabel("");
        this.$$$loadButtonText$$$(registerButton, this.$$$getMessageFromBundle$$$("lang", "app.go_to_register"));
        loginScreen.add(registerButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, -1), new Dimension(120, -1), null, 0, false));
        nicknameField = new JTextField();
        nicknameField.setBackground(new Color(-12829636));
        nicknameField.setCaretColor(new Color(-1));
        Font nicknameFieldFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, nicknameField.getFont());
        if (nicknameFieldFont != null) nicknameField.setFont(nicknameFieldFont);
        nicknameField.setForeground(new Color(-1));
        loginScreen.add(nicknameField, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(200, -1), null, 0, false));
        result = new JLabel();
        result.setBackground(new Color(-12829636));
        Font resultFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, result.getFont());
        if (resultFont != null) result.setFont(resultFont);
        result.setForeground(new Color(-1));
        result.setText("");
        loginScreen.add(result, new GridConstraints(10, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 26), null, 0, false));
        settingsButton = new JButton();
        settingsButton.setBackground(new Color(-11513776));
        settingsButton.setBorderPainted(false);
        settingsButton.setContentAreaFilled(true);
        settingsButton.setDoubleBuffered(false);
        settingsButton.setEnabled(true);
        settingsButton.setFocusPainted(false);
        settingsButton.setForeground(new Color(-1));
        settingsButton.setHideActionText(false);
        settingsButton.setHorizontalAlignment(0);
        settingsButton.setHorizontalTextPosition(0);
        settingsButton.setIcon(new ImageIcon(getClass().getResource("/images/settings.png")));
        settingsButton.setOpaque(true);
        settingsButton.setText("");
        loginScreen.add(settingsButton, new GridConstraints(11, 0, 1, 1, GridConstraints.ANCHOR_SOUTHWEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, new Dimension(30, 30), 0, false));
        final JLabel label1 = new JLabel();
        label1.setDoubleBuffered(true);
        Font label1Font = this.$$$getFont$$$("Comic Sans MS", -1, 28, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setForeground(new Color(-16777216));
        label1.setHorizontalAlignment(10);
        label1.setHorizontalTextPosition(11);
        label1.setIcon(new ImageIcon(getClass().getResource("/images/logo/xnor_small.png")));
        label1.setText("XNOR");
        label1.setVerticalAlignment(0);
        label1.setVerticalTextPosition(3);
        loginScreen.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        loginScreen.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(-1, 106), null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        loginScreen.add(spacer2, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        rememberMeCheckBox = new JCheckBox();
        rememberMeCheckBox.setActionCommand("");
        rememberMeCheckBox.setBackground(new Color(-11513776));
        rememberMeCheckBox.setContentAreaFilled(false);
        rememberMeCheckBox.setFocusPainted(false);
        Font rememberMeCheckBoxFont = this.$$$getFont$$$("Comic Sans MS", -1, -1, rememberMeCheckBox.getFont());
        if (rememberMeCheckBoxFont != null) rememberMeCheckBox.setFont(rememberMeCheckBoxFont);
        rememberMeCheckBox.setForeground(new Color(-1));
        rememberMeCheckBox.setHorizontalAlignment(10);
        rememberMeCheckBox.setLabel("");
        rememberMeCheckBox.setName("");
        rememberMeCheckBox.setSelected(false);
        this.$$$loadButtonText$$$(rememberMeCheckBox, this.$$$getMessageFromBundle$$$("lang", "app.remember_me"));
        loginScreen.add(rememberMeCheckBox, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nicknameLabel.setLabelFor(nicknameField);
        passwordLabel.setLabelFor(passwordField);
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
        return loginScreen;
    }

}
