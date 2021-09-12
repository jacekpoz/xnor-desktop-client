package com.github.jacekpoz.client.desktop;

public final class XnorClientConstants {

    private XnorClientConstants() {
        throw new AssertionError("you dummy");
    }

    public static final String XNOR_DIRECTORY = System.getenv("APPDATA") + "\\xnor\\";
    public static final String XNOR_LOGS_DIRECTORY = XNOR_DIRECTORY + "logs\\";
    public static final String XNOR_SETTINGS_FILE = XNOR_DIRECTORY + "settings.txt";
}
