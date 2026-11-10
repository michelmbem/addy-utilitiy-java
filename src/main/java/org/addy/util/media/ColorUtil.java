package org.addy.util.media;

import java.awt.*;

/**
 * Provides static operations for measuring and blending colors.
 * This class is not intended to be instantiated.
 */
public final class ColorUtil {
    /** Fully transparent black, with all color channels and alpha set to zero. */
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private static final double DARKNESS_THRESHOLD = 0.5;

    /** Prevents instances of this utility class from being created. */
    private ColorUtil() {}

    /**
     * Computes the luminance (that is, brightness) of a color.
     *
     * @param color color for which to get luminance
     * @return a floating-point value in the range [0, 1], where 0 is black and 1 is white
     */
    public static double luminance(Color color) {
        return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
    }

    /**
     * Checks whether the given color can be considered bright.
     *
     * @param color color for which to check brightness
     * @return {@code true} if the color's luminance is greater than or equal to the darkness threshold;
     *         {@code false} otherwise
     */
    public static boolean isLight(Color color) {
        return luminance(color) >= DARKNESS_THRESHOLD;
    }

    /**
     * Checks whether the given color can be considered dark.
     *
     * @param color color for which to check darkness
     * @return {@code true} if the color's luminance is lower than the darkness threshold;
     *         {@code false} otherwise
     */
    public static boolean isDark(Color color) {
        return luminance(color) < DARKNESS_THRESHOLD;
    }

    /**
     * Mixes two colors by blending their components.
     * The level is the weight of {@code other}; values at or above 1 return {@code color},
     * and values at or below 0 return {@code other}.
     *
     * @param color first color to mix
     * @param other second color to mix
     * @param level weight of the second color in the mix
     * @return the blended color
     */
    public static Color mix(Color color, Color other, double level) {
        if (level >= 1) return color;
        if (level <= 0) return other;

        double complement = 1 - level;
        int alpha = (int) (complement * color.getAlpha() + level * other.getAlpha());
        int red = (int) (complement * color.getRed() + level * other.getRed());
        int green = (int) (complement * color.getGreen() + level * other.getGreen());
        int blue = (int) (complement * color.getBlue() + level * other.getBlue());
        return new Color(red, green, blue, alpha);
    }

    /**
     * Mixes a color with white to create a brighter version.
     *
     * @param color color to brighten
     * @param level weight of white in the mix
     * @return a mix of {@code color} and {@link Color#WHITE} at the given level
     */
    public static Color lighter(Color color, double level) {
        return mix(color, Color.WHITE, level);
    }

    /** Creates a brighter version of a color using a level of 0.5. */
    public static Color lighter(Color color) {
        return lighter(color, 0.5);
    }

    /**
     * Mixes a color with black to create a darker version.
     *
     * @param color color to darken
     * @param level weight of black in the mix
     * @return a mix of {@code color} and {@link Color#BLACK} at the given level
     */
    public static Color darker(Color color, double level) {
        return mix(color, Color.BLACK, level);
    }

    /** Creates a darker version of a color using a level of 0.5. */
    public static Color darker(Color color) {
        return darker(color, 0.5);
    }

    /**
     * Mixes a color with transparent black to create a translucent version.
     *
     * @param color color to make translucent
     * @param level weight of transparency in the mix
     * @return a mix of {@code color} and transparent black at the given level
     */
    public static Color translucent(Color color, double level) {
        return mix(color, TRANSPARENT, level);
    }

    /** Creates a translucent version of a color using a level of 0.5. */
    public static Color translucent(Color color) {
        return translucent(color, 0.5);
    }
}
