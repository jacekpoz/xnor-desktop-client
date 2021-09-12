package com.github.jacekpoz.client.desktop.logging;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class XnorLogFormatter extends SimpleFormatter {
    @Override
    public synchronized String format(LogRecord lr) {
        return String.format("[%1$s] [%2$-7s] %3$s at %4$s.%5$s%n%6$s%n%7$s %8$s%n",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX")
                        .format(new Date(lr.getMillis())),
                lr.getLevel().getLocalizedName(),
                lr.getMessage(),
                lr.getSourceClassName(),
                lr.getSourceMethodName(),
                lr.getParameters() == null ? "" :
                        lr.getParameters().length == 0 ? "" :
                                lr.getParameters().length == 1 ? lr.getParameters()[0] :
                                        Arrays.toString(lr.getParameters()),
                lr.getThrown() == null ? "" : lr.getThrown().toString(),
                lr.getThrown() == null ? "" : Arrays.toString(lr.getThrown().getStackTrace())
        );
    }
}
