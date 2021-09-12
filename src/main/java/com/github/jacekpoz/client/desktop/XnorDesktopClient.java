package com.github.jacekpoz.client.desktop;

import com.github.jacekpoz.client.desktop.gui.XnorWindow;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.User;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XnorDesktopClient {

    private final static Logger LOGGER = Logger.getLogger(XnorDesktopClient.class.getName());

    @Getter
    private final Socket socket;
    @Getter
    private final XnorWindow window;
    @Getter @Setter
    private User user;
    @Getter @Setter
    private Chat chat;
    @Getter @Setter
    private boolean isLoggedIn;
    @Getter @Setter
    private boolean isOnline;
    @Getter @Setter
    private boolean isVLCAvailable;

    private final Properties settings;

    public XnorDesktopClient(Socket s, boolean isOnline, boolean isVLCAvailable) {
        settings = new Properties();
        tryInitializeAppDataDirectory();
        socket = s;
        this.isOnline = isOnline;
        this.isVLCAvailable = isVLCAvailable;
        window = new XnorWindow(this);
        user = new User(-1, "dupa", "dupa dupa", LocalDateTime.MIN);
        chat = new Chat(-1, "dupa", LocalDateTime.MIN, -1);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

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

    public String getSetting(String settingName) {
        return settings.getProperty(settingName, "null");
    }

    public void setSetting(String settingName, String setting) {
        settings.setProperty(settingName, setting);
    }

    private void saveSettingsToFile() {
        try {
            FileWriter settingsWriter = new FileWriter(XnorClientConstants.XNOR_SETTINGS_FILE);
            settings.store(settingsWriter, "auto-save");
            LOGGER.log(Level.INFO, "Auto-saved settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToSettingsFile(String key, Object value) {
        try {
            File file = new File(System.getenv("APPDATA") + "\\xnor\\settings.txt");
            Scanner scanner = new Scanner(file);
            String validdata = "";
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith(key)) {
                    validdata += key + " : " + value.toString() + "\n";
                } else {
                    validdata += line + "\n";
                }
            }

            FileWriter writer = new FileWriter(file);
            writer.write(validdata);
            writer.close();


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String readFromSettingsFile(String key) {
        try {
            File file = new File(System.getenv("APPDATA") + "\\xnor\\settings.txt");
            Scanner scanner = new Scanner(file);

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.startsWith(key)) {
                    return line.split(" : ")[1];
                }
            }

        } catch (FileNotFoundException ex) {
            tryInitializeAppDataDirectory();
            readFromSettingsFile(key);
        }
        return "";
    }

    public void start() {
        window.start();
    }

    public void stop() throws IOException {
        if (socket != null) socket.close();
        window.dispose();
    }

}
