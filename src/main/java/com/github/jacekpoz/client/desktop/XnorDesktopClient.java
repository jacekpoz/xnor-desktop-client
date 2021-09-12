package com.github.jacekpoz.client.desktop;

import com.github.jacekpoz.client.desktop.gui.XnorWindow;
import com.github.jacekpoz.common.sendables.Chat;
import com.github.jacekpoz.common.sendables.User;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Scanner;

public class XnorDesktopClient {

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

    public XnorDesktopClient(Socket s, boolean isOnline, boolean isVLCAvailable) {
        tryInitializeAppDataDirectory();
        socket = s;
        this.isOnline = isOnline;
        this.isVLCAvailable = isVLCAvailable;
        window = new XnorWindow(this);
        user = new User(-1, "dupa", "dupa dupa", LocalDateTime.MIN);
        chat = new Chat(-1, "dupa", LocalDateTime.MIN, -1);
    }

    public void tryInitializeAppDataDirectory() {
        try {
            File file = new File(System.getenv("APPDATA") + "\\xnor\\");
            if (!file.exists()) { if (file.mkdir()) System.out.println("created xnor directory"); }
            else { System.out.println("xnor directory already exists"); }

            file = new File(System.getenv("APPDATA") + "\\xnor\\logs\\");
            if (!file.exists()) { if (file.mkdir()) System.out.println("created xnor log directory"); }
            else { System.out.println("xnor log directory already exists"); }

            file = new File(System.getenv("APPDATA") + "\\xnor\\settings.txt"); // txt because poop ass pee
            if (file.createNewFile()) { System.out.println("created settings file"); }
            else { System.out.println("settings file already exists"); }

            Scanner scanner = new Scanner(file);
            String wholeFile = "";

            while (scanner.hasNextLine()) {
                wholeFile += scanner.nextLine() + "\n";
            }

            if(wholeFile.trim().equals("")) {
                FileWriter writer = new FileWriter(file);
                String logDir = System.getenv("APPDATA") + "\\xnor\\logs\\";
                writer.write("AutoLogin : false\nUsername :  \nPassword :  \nlogDirectory : " + logDir + "\nlanguage : en_US");
                writer.close();
                System.out.println("wrote basic things");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
