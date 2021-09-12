package com.github.jacekpoz.client.desktop;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.text.ParseException;

public class XnorNumberFormatter extends NumberFormatter {

    public XnorNumberFormatter(NumberFormat format) {
        super(format);
        setValueClass(Long.class);
        setMinimum(-1);
        setMaximum(Long.MAX_VALUE);
        setAllowsInvalid(false);
        setCommitsOnValidEdit(true);
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        return text.isEmpty() ? null : super.stringToValue(text);
    }
}
