package org.addy.util.media;

public enum FlipAxis {
    X(false, true),
    Y(true, false),
    BOTH(true, true);

    private final boolean horizontal;
    private final boolean vertical;

    FlipAxis(boolean horizontal, boolean vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public boolean isVertical() {
        return vertical;
    }
}
