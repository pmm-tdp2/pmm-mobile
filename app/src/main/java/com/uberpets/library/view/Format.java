package com.uberpets.library.view;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Format {
    public static String iso(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(date);
    }

    public static String human(Date date) {
        return new SimpleDateFormat().format(date);
    }
}
