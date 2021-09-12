package com.github.jacekpoz.client.desktop;

import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

public class XnorLocale {

    public static final XnorLocale en_US = new XnorLocale(new Locale("en", "US"));
    public static final XnorLocale es_ES = new XnorLocale(new Locale("es", "ES"));
    public static final XnorLocale pl_PL = new XnorLocale(new Locale("pl", "PL"));
    public static final XnorLocale lol_US = new XnorLocale(new Locale("lol", "US"));

    @Getter
    private final Locale locale;
    @Getter @Setter
    private String localeName;

    public XnorLocale(Locale locale) {
        this(locale, "null");
    }

    public XnorLocale(Locale locale, String localeName) {
        this.locale = locale;
        this.localeName = localeName;
    }

    @Override
    public String toString() {
        return localeName;
    }
}
