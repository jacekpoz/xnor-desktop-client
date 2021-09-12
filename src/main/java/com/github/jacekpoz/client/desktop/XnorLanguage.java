package com.github.jacekpoz.client.desktop;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

public class XnorLanguage {

    public static final XnorLanguage en_US = new XnorLanguage(new Locale("en", "US"));
    public static final XnorLanguage es_ES = new XnorLanguage(new Locale("es", "ES"));
    public static final XnorLanguage pl_PL = new XnorLanguage(new Locale("pl", "PL"));
    public static final XnorLanguage lol_US = new XnorLanguage(new Locale("lol", "US"));

    public static final XnorLanguage[] SUPPORTED_LANGUAGES = {en_US, es_ES, pl_PL, lol_US};

    @Getter
    private final Locale locale;
    @Getter @Setter
    private String localeName;

    public XnorLanguage(Locale locale) {
        this(locale, "null");
    }

    public XnorLanguage(Locale locale, String localeName) {
        this.locale = locale;
        this.localeName = localeName;
    }

    public String getLanguageCode() {
        return locale.toString();
    }

    @Override
    public String toString() {
        return localeName;
    }
}
