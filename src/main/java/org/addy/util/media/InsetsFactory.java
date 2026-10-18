package org.addy.util.media;

import java.awt.Insets;

public final class InsetsFactory {
    private InsetsFactory() {}

    public static Insets empty() {
        return uniform(0);
    }

    public static Insets uniform(int all) {
        return new Insets(all, all, all, all);
    }

    public static Insets xy(int x, int y) {
        return new Insets(y, x, y, x);
    }
}
