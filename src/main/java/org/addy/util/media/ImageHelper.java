package org.addy.util.media;

import org.addy.util.ArrayUtil;
import org.addy.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public final class ImageHelper {
    private ImageHelper() {}
    
    public static BufferedImage buffer(Image originalImage, int imageType) {
        if (originalImage instanceof BufferedImage bi) return bi;
        
        var bufferedImage = new BufferedImage(
        		originalImage.getWidth(null),
                originalImage.getHeight(null),
                imageType);
        var g = (Graphics2D) bufferedImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        
        return bufferedImage;
    }

    public static Image resize(Image originalImage, int desiredWidth,
                               int desiredHeight,
                               boolean preserveAspectRatio) {

        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        BufferedImage resizedImage;

        if (preserveAspectRatio) {
            float aspectRatio = (float) width / height;
            float widthRatio = (float) width / desiredWidth;
            float heightRatio = (float) height / desiredHeight;
            int effectiveWidth, effectiveHeight;

            if (widthRatio > heightRatio) {
                effectiveWidth = desiredWidth;
                effectiveHeight = (int) (effectiveWidth / aspectRatio);
            } else {
                effectiveHeight = desiredHeight;
                effectiveWidth = (int) (effectiveHeight * aspectRatio);
            }

            resizedImage = new BufferedImage(effectiveWidth, effectiveHeight, BufferedImage.TYPE_INT_RGB);
            var g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, effectiveWidth, effectiveHeight,
                    0, 0, width, height, null);
            g.dispose();
        } else {
            resizedImage = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_RGB);
            var g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, desiredWidth, desiredHeight,
                    0, 0, width, height, null);
            g.dispose();
        }

        return resizedImage;
    }

    public static Image rotate(Image originalImage, RotationAngle angle) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        AffineTransform at = AffineTransform.getRotateInstance(
                angle.getRadians(), width / 2.0, height / 2.0);
        
        Image rotatedImage;
        if (RotationAngle.DCW.equals(angle)) {
            rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            double translation = angle.getDirection() * (width - height) / 2.0;
            at.translate(translation, translation);
            rotatedImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        }
        
        var g = (Graphics2D) rotatedImage.getGraphics();
        g.drawImage(originalImage, at, null);
        g.dispose();

        return rotatedImage;
    }

    public static Image flip(Image originalImage, FlipAxis axis) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        var flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) flippedImage.getGraphics();

        if (axis.isHorizontal()) {
            for (int i = 0, j = width - 1; i < width; ++i, --j) {
                g.drawImage(originalImage, i, 0, i + 1, height, j, 0, j + 1, height, null);
            }
        }

        if (axis.isVertical()) {
            for (int i = 0, j = height - 1; i < height; ++i, --j) {
                g.drawImage(originalImage, 0, i, width, i + 1, 0, j, width, j + 1, null);
            }
        }

        g.dispose();
        return flippedImage;
    }

    public static Image crop(Image originalImage, int left, int top, int right, int bottom) {
        if (left < 0 || top < 0 || right < 0 || bottom < 0)
        	return originalImage;

        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        int croppedWidth = width - left - right;
        int croppedHeight = height - top - bottom;

        if (croppedWidth <= 0 || croppedHeight <= 0)
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        Image croppedImage = new BufferedImage(croppedWidth, croppedHeight, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) croppedImage.getGraphics();
        g.drawImage(originalImage, 0, 0, croppedWidth, croppedHeight,
                left, top, width - right, height - bottom, null);
        g.dispose();

        return croppedImage;
    }

    public static Image grayScale(Image originalImage) {
        BufferedImage image = buffer(originalImage, BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                var c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                int gray = red + green + blue;
                var gsColor = new Color(gray, gray, gray);
                image.setRGB(j, i, gsColor.getRGB());
            }
        }

        return image;
    }

    public static Image addBorder(Image originalImage, Paint borderPaint, Insets borderInsets) {
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);
        int width = originalWidth + borderInsets.left + borderInsets.right;
        int height = originalHeight + borderInsets.top + borderInsets.bottom;
        var imageWithBorder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) imageWithBorder.getGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setPaint(borderPaint);
        g.fillRect(0, 0, width, height);
        g.drawImage(originalImage, borderInsets.left, borderInsets.top, originalWidth, originalHeight, null);
        g.dispose();

        return imageWithBorder;
    }
    
    public static Image makeTiles(File[] sourceFiles, int width, int height,
                                  int rows, int columns, Paint background) throws IOException {

        if (ArrayUtil.isEmpty(sourceFiles)) throw new IllegalArgumentException("No source file provided");
        if (width <= 0) throw new IllegalArgumentException("width must be positive");
        if (height <= 0) throw new IllegalArgumentException("height must be positive");
        if (rows <= 0) throw new IllegalArgumentException("rows must be positive");
        if (columns <= 0) throw new IllegalArgumentException("columns must be positive");

        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setPaint(background);
        g.fillRect(0, 0, width, height);

        int tileWidth = (int)((float) width / columns);
        int tileHeight = (int)((float) height / rows);
        int row = 0, column = 0;

        for (File sourceFile : sourceFiles) {
        	BufferedImage tile = ImageIO.read(sourceFile);
            paintCover(g, tile, column * tileWidth, row * tileHeight, tileWidth, tileHeight);
            if (++column >= columns) {
                column = 0;
                if (++row >= rows) break;
            }
        }

        g.dispose();
        return image;
    }

    public static Image makeTiles(File[] sourceFiles, int width, int height, Paint background) throws IOException {
        if (ArrayUtil.isEmpty(sourceFiles)) throw new IllegalArgumentException("No source file provided");
        if (width <= 0) throw new IllegalArgumentException("width must be positive");
        if (height <= 0) throw new IllegalArgumentException("height must be positive");

        int rows = (int) Math.sqrt(sourceFiles.length);
        int columns = (int) Math.ceil((double) sourceFiles.length / rows);
        return makeTiles(sourceFiles, width, height, rows, columns, background);
    }

    public static Image getSprite(Image spriteSheet, int spriteX, int spriteY,
                                  int spriteWidth, int spriteHeight, Insets spriteInsets) {

        int left = spriteX * spriteWidth + spriteInsets.left;
        int top = spriteY * spriteHeight + spriteInsets.top;
        int width = spriteWidth - spriteInsets.left - spriteInsets.right;
        int height = spriteHeight - spriteInsets.top - spriteInsets.bottom;

        var sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) sprite.getGraphics();
        g.drawImage(spriteSheet, 0, 0, width, height,
                left, top, left + width, top + height, null);
        g.dispose();

        return sprite;
    }

    public static Image getSprite(Image spriteSheet, int spriteX, int spriteY,
                                  int[] spriteWidths, int[] spriteHeights,
                                  Insets spriteInsets) {

        int left = spriteInsets.left;
        for (int i = 0; i < spriteX; ++i) left += spriteWidths[i];

        int top = spriteInsets.top;
        for (int i = 0; i < spriteY; ++i) top += spriteHeights[i];

        int width = spriteWidths[spriteX] - spriteInsets.left - spriteInsets.right;
        int height = spriteHeights[spriteY] - spriteInsets.top - spriteInsets.bottom;

        var sprite = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) sprite.getGraphics();
        g.drawImage(spriteSheet, 0, 0, width, height,
                left, top, left + width, top + height, null);
        g.dispose();

        return sprite;
    }

    public static String toBase64DataURL(File imageFile) throws IOException {
        byte[] fileBytes = FileUtil.readAllBytes(imageFile);
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        String mimeType = FileUtil.getContentType(imageFile.getName());
        return "data:" + mimeType + ";base64," + base64String;
    }

    public static Image fromBase64DataURL(String base64String) throws IOException {
        if (!base64String.startsWith("data:image/")) return null;
        int commaIndex = base64String.indexOf(',');
        if (commaIndex < 0) return null;
        byte[] bytes = Base64.getDecoder().decode(base64String.substring(commaIndex + 1));
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

	private static void paintCover(Graphics2D g, BufferedImage image,
                                   int left, int top, int width, int height) {

		double aspectRatio = (double) image.getWidth() / image.getHeight();
		double widthRatio = (double) image.getWidth() / width;
		double heightRatio = (double) image.getHeight() / height;
		int x, y, h, w;
		
		x = y = 0;
		if (widthRatio > heightRatio) {
			h = image.getHeight();
			w = (int) (image.getWidth() * width / (height * aspectRatio));
			x = (image.getWidth() - w) / 2;
		} else {
			w = image.getWidth();
			h = (int) (image.getHeight() * height / (width / aspectRatio));
			y = (image.getHeight() - h) / 2;
		}

		g.drawImage(image, left, top, left + width, top + height,
                x, y, x + w, y + h, null);
	}

}
