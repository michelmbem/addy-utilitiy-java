package org.addy.util.media;

import java.awt.image.BufferedImage;

public record ImageFrame(BufferedImage image, int delay, String disposal, int width, int height) {
    public ImageFrame(BufferedImage image) {
        this(image, -1, null, -1, -1);
    }
}
