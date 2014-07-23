package de.raidcraft.util;

import java.util.Random;

/**
 * @author Silthus
 */
public final class MathUtil {

    public static final Random RANDOM = new Random();

    public static double toPercent(double percent) {

        return ((int) ((percent * 100.0) * 100)) / 100.0;
    }

    public static double trim(double number) {

        return ((int) (100.0 * number)) / 100.0;
    }
}