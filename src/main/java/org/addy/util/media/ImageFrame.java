package org.addy.util.media;

import java.awt.*;
import java.awt.image.BufferedImage;

public record ImageFrame(BufferedImage image, Rectangle bounds, int delay, String disposal) {}
