package com.github.jacekpoz.client.desktop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class XnorSettings extends Properties {

    public final static HashMap<String, String> DEFAULTS = new HashMap<>();
    static {
        DEFAULTS.put("language", XnorLanguage.en_US.getLanguageCode());
        DEFAULTS.put("logsDir", XnorClientConstants.XNOR_LOGS_DIRECTORY);
        DEFAULTS.put("autoSavePeriod", "5");
    }

    public XnorLanguage getLanguage() {
        for (XnorLanguage xl : XnorLanguage.SUPPORTED_LANGUAGES) {
            if (xl.getLanguageCode().equals(super.getProperty("language", "null"))) {
                return xl;
            }
        }
        return XnorLanguage.en_US;
    }

    public void setLanguage(XnorLanguage lang) {
        super.setProperty("language", lang.getLanguageCode());
    }

    public String getLogsDirectory() {
        return super.getProperty("logsDir", DEFAULTS.get("logsDir"));
    }

    public void setLogsDirectory(String logsDirectory) {
        super.setProperty("logsDir", logsDirectory);
    }

    public int getAutoSavePeriod() {
        return Integer.parseInt(super.getProperty("autoSavePeriod", DEFAULTS.get("autoSavePeriod")));
    }

    public void setAutoSavePeriod(int period) {
        super.setProperty("autoSavePeriod", String.valueOf(period));
    }

    public void saveToFile(FileWriter writer, String comments) throws IOException {
        store(writer, comments);
    }
}
