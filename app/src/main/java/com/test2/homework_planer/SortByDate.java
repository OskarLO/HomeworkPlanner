package com.test2.homework_planer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;

public class SortByDate implements Comparator<ListItem> {

    @Override
    public int compare(ListItem listItem1, ListItem listItem2) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = sdf.parse(listItem1.getDeadline());
            date2 = sdf.parse(listItem2.getDeadline());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date1.compareTo(date2);
    }
}
