package org.addy.util.media;

public enum RotationAngle {
    CW(1),
    CCW(-1),
    DCW(2);

    private final int direction;

    RotationAngle(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    public double getRadians() {
        return direction * Math.PI / 2;
    }
}
