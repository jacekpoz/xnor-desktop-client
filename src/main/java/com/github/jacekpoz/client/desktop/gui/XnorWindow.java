package com.github.jacekpoz.client.desktop.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jacekpoz.client.desktop.XnorDesktopClient;
import com.github.jacekpoz.client.desktop.InputHandler;
import com.github.jacekpoz.client.desktop.gui.screens.*;
import com.github.jacekpoz.client.desktop.logging.LogFormatter;
import com.github.jacekpoz.common.jackson.JsonObjectMapper;
import com.github.jacekpoz.common.sendables.Sendable;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.*;

public class XnorWindow extends JFrame {

    private final static Logger ROOT_LOGGER = LogManager.getLogManager().getLogger("");
    private final static Logger LOGGER = Logger.getLogger(XnorWindow.class.getName());

    @Getter
    private PrintWriter out;
    @Getter
    private BufferedReader in;

    @Getter
    private final InputHandler handler;

    @Getter
    private final Screen[] screens;

    @Getter
    private final MessageScreen messageScreen;
    @Getter
    private final LoginScreen loginScreen;
    @Getter
    private final RegisterScreen registerScreen;
    @Getter
    private final FriendsScreen friendsScreen;
    @Getter
    private final CreateChatsScreen createChatsScreen;
    @Getter
    private final SettingsScreen settingsScreen;

    @Getter
    private final XnorDesktopClient client;

    @Getter
    @Setter
    private ResourceBundle languageBundle;

    @Getter
    private String logDirectory;

    @Getter
    private String currentLogFile;

    @Getter
    private final JsonObjectMapper mapper;

    @Getter
    @Setter
    private Screen lastScreen;
    @Getter
    @Setter
    private Screen currentScreen;

    public XnorWindow(XnorDesktopClient c) {
        client = c;

        languageBundle = ResourceBundle.getBundle("lang");
        logDirectory = System.getenv("APPDATA") + "/xnor/logs/";
        ROOT_LOGGER.setUseParentHandlers(false);
        changeLogDirectory(logDirectory);

        try {
            out = new PrintWriter(client.getSocket().getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mapper = new JsonObjectMapper();

        handler = new InputHandler(this);

        messageScreen = new MessageScreen(this);
        loginScreen = new LoginScreen(this);
        registerScreen = new RegisterScreen(this);
        friendsScreen = new FriendsScreen(this);
        createChatsScreen = new CreateChatsScreen(this);
        settingsScreen = new SettingsScreen(this);

        screens = new Screen[] {messageScreen, loginScreen, registerScreen, friendsScreen, createChatsScreen, settingsScreen};

        Locale lang = new Locale(getClient().readFromSettingsFile("language").split("_")[0],
                getClient().readFromSettingsFile("language").split("_")[1]);
        changeLanguage(lang);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/images/logo/xnor_icon.png")).getImage());
        setMinimumSize(new Dimension(800, 600));

        lastScreen = loginScreen;
        currentScreen = loginScreen;

        handler.start();
    }

    public void start() {
        setScreen(loginScreen);
        setVisible(true);
    }

    public void setScreen(Screen screen) {
        lastScreen = currentScreen;
        setContentPane(screen.getPanel());
        screen.update();
        screen.updateUI();
        currentScreen = screen;
        revalidate();
    }

    public void send(Sendable s) {
        String json;
        try {
            json = mapper.writeValueAsString(s);
            out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Screen getScreen(long id) {
        for (Screen s : screens)
            if (s.getScreenID() == id)
                return s;
        return null;
    }

    public void changeLanguage(Locale lang) {
        languageBundle = ResourceBundle.getBundle("lang", lang);
        setTitle(languageBundle.getString("app.title"));
        for (Screen s : screens) {
            s.changeLanguage();
        }
    }

    public void changeLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
        try {
            for (Handler h : ROOT_LOGGER.getHandlers()) {
                ROOT_LOGGER.removeHandler(h);
                h.flush();
                h.close();
            }

            String logFile = logDirectory + "\\xnor_" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")
                    .format(new Date(System.currentTimeMillis())) + ".log";
            FileHandler fh = new FileHandler(logFile);
            currentLogFile = logFile;
            fh.setFormatter(new LogFormatter());
            ROOT_LOGGER.addHandler(fh);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to create log file", e);
        }
    }

    public String getLangString(String key) {
        return languageBundle.getString(key);
    }

    public void logout() {
        client.setLoggedIn(false);
        setScreen(loginScreen);
        loginScreen.updateUI();
        registerScreen.updateUI();
        settingsScreen.updateUI();
        client.setUser(null);
    }
}
