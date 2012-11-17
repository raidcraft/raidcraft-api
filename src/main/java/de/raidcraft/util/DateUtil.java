package de.raidcraft.util;

import de.raidcraft.RaidCraft;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: Philip
 * Date: 17.09.12 - 21:48
 * Description:
 */
public final class DateUtil {

    private DateUtil() {

    }

    public static final SimpleDateFormat DATE = new SimpleDateFormat("dd-MM-yyy HH:mm:ss");

    public static String getCurrentDateString() {

        return getDateString(new Date());
    }

    public static String getDateString(long timestamp) {

        return getDateString(new Date(timestamp));
    }

    public static long currentTimestamp() {

        return System.currentTimeMillis() / 1000;
    }

    public static String getDateString(Date date) {

        return formatDate(date, DATE);
    }

    public static String formatDate(Date date, SimpleDateFormat format) {

        return format.format(date);
    }

    public static long getTimeStamp(String date) {

        try {
            return DATE.parse(date).getTime();
        } catch (ParseException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
