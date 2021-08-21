package com.github.jacekpoz.client.desktop;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XnorDesktopClientMain {


    private final static Logger LOGGER = Logger.getLogger(XnorDesktopClientMain.class.getName());

    public static void main(String[] args) {

        if (args.length != 2)
            System.err.println("You need to input the host and the port");

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            LOGGER.log(Level.SEVERE, "RuntimeException in thread " + t, e);
            e.printStackTrace();
        });

        try {
            XnorDesktopClient c = new XnorDesktopClient(new Socket(host, port));
            c.start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to server", e);
            System.exit(1);
        }

    }

}