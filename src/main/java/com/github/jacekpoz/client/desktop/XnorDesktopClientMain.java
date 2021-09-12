package com.github.jacekpoz.client.desktop;

import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

import javax.swing.*;
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


        boolean isVLCAvaliable = new NativeDiscovery().discover();

        try {
            new XnorDesktopClient(new Socket(host, port), true, isVLCAvaliable).start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to server", e);
            new XnorDesktopClient(null, false, isVLCAvaliable).start();
        }

    }

}