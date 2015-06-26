package de.raidcraft.util;

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

    public static String getFormattedTime(double seconds) {

        if (seconds > 60.0) {
            return secondsToMinutes(seconds) + "min";
        } else {
            return (((int) (seconds * 100)) / 100.0) + "s";
        }
    }

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

        if (input.endsWith("m") || input.endsWith("min")) {
            return minutesToTicks(Long.parseLong(input.replace("m", "").replace("min", "min")));
        }
        if (input.endsWith("s")) {
            return secondsToTicks(Long.parseLong(input.replace("s", "")));
        }
        return Long.parseLong(input);
    }
}
