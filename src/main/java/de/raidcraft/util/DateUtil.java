package de.raidcraft.util;

import de.raidcraft.api.RaidCraft;

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

    public static Date getCurrentTime() {
        return new Date();
    }

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

    public static String formatSeconds(int secondsTotal) {

        int hours = (int) Math.floor(secondsTotal / 3600);
        int minutes = (int) Math.floor((secondsTotal - (hours * 3600)) / 60);
        int seconds = secondsTotal - (hours * 3600) - (minutes * 60);

        String hPre, mPre, sPre;
        hPre = (hours < 10) ? "0" : "";
        mPre = (minutes < 10) ? "0" : "";
        sPre = (seconds < 10) ? "0" : "";
        return hPre + hours + ":" + mPre + minutes + ":" + sPre + seconds;
    }
}
