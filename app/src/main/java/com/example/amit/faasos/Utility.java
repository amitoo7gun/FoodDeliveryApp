package com.example.amit.faasos;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by amit on 9/5/2015.
 */
public class Utility {

    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }
}
