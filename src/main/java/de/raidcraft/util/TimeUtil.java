package de.raidcraft.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public final class TimeUtil {

    private TimeUtil() {

    }

    public static double secondsToMinutes(double seconds) {

        return ((int) ((seconds / 60.0) * 100)) / 100.0;
    }

    public static double ticksToMinutes(long ticks) {

        return secondsToMinutes(ticksToSeconds(ticks));
    }

    public static double millisToMinutes(long millis) {

        return secondsToMinutes(millisToSeconds(millis));
    }

    public static long yearsToTicks(long years) {

        return daysToTicks(years * 365L);
    }

    public static long daysToTicks(long days) {

        return hoursToTicks(days * 24L);
    }

    public static long hoursToTicks(long hours) {

        return minutesToTicks(hours * 60L);
    }

    public static long minutesToTicks(long minutes) {

        return secondsToTicks(minutes * 60.0);
    }

    public static double ticksToSeconds(long ticks) {

        return ((int) (((double) ticks / 20.0) * 100.0)) / 100.0;
    }

    public static double millisToSeconds(long millis) {

        return ((int) (((double) millis / 1000.0) * 100.0)) / 100.0;
    }

    public static long secondsToTicks(double seconds) {

        return Math.round(seconds * 20);
    }

    public static long secondsToMillis(double seconds) {

        return (long) (seconds * 1000);
    }

    public static long ticksToMillis(long ticks) {

        return secondsToMillis(ticksToSeconds(ticks));
    }

    public static long millisToTicks(long millis) {

        return secondsToTicks(millisToSeconds(millis));
    }

    public static String getFormattedTime(double seconds) {

        if (seconds > 60.0) {
            return secondsToMinutes(seconds) + "min";
        } else {
            return (((int) (seconds * 100)) / 100.0) + "s";
        }
    }

    public static String getAccurrateFormatedTime(long millis) {

        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(days > 1 ? " Tage " : " Tag ");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append(hours > 1 ? " Stunden " : " Stunde ");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append(minutes > 1 ? " Minuten " : " Minute ");
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append(seconds > 1 ? " Sekunden " : " Sekunde ");
        }

        return (sb.toString());
    }

    public static String getAccurrateShortFormatedTime(long millis) {

        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append("d");
        }
        if (hours > 0) {
            sb.append(hours);
            sb.append("h");
        }
        if (minutes > 0) {
            sb.append(minutes);
            sb.append("min");
        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append("s");
        }

        return (sb.toString());
    }

    private static final Pattern TIME_PATTERN = Pattern.compile("^(([\\d]+)y)?(([\\d]+)d)?(([\\d]+)h)?(([\\d]+)m)?(([\\d]+)s)?([\\d]+)?$");
    // #0 	1y10d2h33m2s1
    // #1	1y
    // #2	1 -> years
    // #3	10d
    // #4	10 -> days
    // #5	2h
    // #6	2 -> hours
    // #7	33m
    // #8	33 -> minutes
    // #9	2s
    // #10	2 -> seconds
    // #11	1 -> ticks

    /**
     * Parses a given input string to ticks.
     * 10min or 10m -> 10 * 60 * 20
     * 5s -> 5 * 20
     * 10 -> 10 ticks
     *
     * @param input to parse
     * @return ticks
     */
    public static long parseTimeAsTicks(String input) {

        if (Objects.isNull(input)) return 0;
        Matcher matcher = TIME_PATTERN.matcher(input);
        if (!matcher.matches()) return 0;
        long ticks = 0;
        if (!Objects.isNull(matcher.group(2))) {
            ticks += yearsToTicks(Long.parseLong(matcher.group(2)));
        }
        if (!Objects.isNull(matcher.group(4))) {
            ticks += daysToTicks(Long.parseLong(matcher.group(4)));
        }
        if (!Objects.isNull(matcher.group(6))) {
            ticks += hoursToTicks(Long.parseLong(matcher.group(6)));
        }
        if (!Objects.isNull(matcher.group(8))) {
            ticks += minutesToTicks(Long.parseLong(matcher.group(8)));
        }
        if (!Objects.isNull(matcher.group(10))) {
            ticks += secondsToTicks(Long.parseLong(matcher.group(10)));
        }
        if (!Objects.isNull(matcher.group(11))) {
            ticks += Long.parseLong(matcher.group(11));
        }
        return ticks;
    }

    public static long parseTimeAsMillis(String input) {
        return ticksToMillis(parseTimeAsTicks(input));
    }

    public static double parseTimeAsSeconds(String input) {
        return ticksToSeconds(parseTimeAsTicks(input));
    }
}
