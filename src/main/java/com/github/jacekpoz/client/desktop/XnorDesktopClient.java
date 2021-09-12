package com.github.jacekpoz.client.desktop;

import com.github.jacekpoz.client.desktop.gui.XnorWindow;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.User;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XnorDesktopClient {

    private final static Logger LOGGER = Logger.getLogger(XnorDesktopClient.class.getName());

    @Getter
    private final Socket socket;
    @Getter
    private final XnorWindow window;
    @Getter
    @Setter
    private User user;
    @Getter
    @Setter
    private Chat chat;
    @Getter
    @Setter
    private boolean isLoggedIn;
    @Getter
    @Setter
    private boolean isOnline;
    @Getter
    @Setter
    private boolean isVLCAvailable;

    @Getter
    private final XnorSettings settings;

    private ScheduledExecutorService executor;

    public XnorDesktopClient(Socket s, boolean isOnline, boolean isVLCAvailable) {
        settings = new XnorSettings();
        tryInitializeAppDataDirectory();
        socket = s;
        this.isOnline = isOnline;
        this.isVLCAvailable = isVLCAvailable;
        window = new XnorWindow(this);
        user = new User(-1, "dupa", "dupa dupa", LocalDateTime.MIN);
        chat = new Chat(-1, "dupa", LocalDateTime.MIN, -1);
    }

    private void tryInitializeAppDataDirectory() {
        try {
            File file = new File(XnorClientConstants.XNOR_DIRECTORY);
            if (!file.exists())
                if (file.mkdir())
                    LOGGER.log(Level.INFO, "Created xnor directory", file);

            file = new File(XnorClientConstants.XNOR_LOGS_DIRECTORY);
            if (!file.exists())
                if (file.mkdir())
                    LOGGER.log(Level.INFO, "Created log directory", file);

            file = new File(XnorClientConstants.XNOR_SETTINGS_FILE);
            if (file.createNewFile())
                LOGGER.log(Level.INFO, "Created settings file" + file);

            FileReader reader = new FileReader(file);
            settings.load(reader);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void startAutoSave(int period) {
        if (!executor.isShutdown()) {
            executor.shutdown();
            executor = null;
        }
        if (executor == null) executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleAtFixedRate(() -> saveSettings("auto-save"), 1, period, TimeUnit.MINUTES);
    }

    public void saveSettings(String comments) {
        try {
            settings.saveToFile(new FileWriter(XnorClientConstants.XNOR_SETTINGS_FILE), comments);
            LOGGER.log(Level.INFO, "Auto-saved settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        window.start();
    }

    public void stop() throws IOException {
        if (socket != null) socket.close();
        window.dispose();
    }

}
