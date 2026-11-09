package org.addy.util.media;

import org.addy.util.ArrayUtil;
import org.addy.util.FileUtil;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Provides static operations for converting, transforming, composing, and encoding images.
 * This class is not intended to be instantiated.
 */
public final class ImageHelper {
    /** Maximum allowed difference in each RGB channel when matching a border color. */
    private static final int COLOR_TOLERANCE = 10;

    /** Prevents instances of this utility class from being created. */
    private ImageHelper() {}

    /**
     * Returns the supplied image as a {@link BufferedImage}, rendering it into a new buffer when needed.
     * If the image is already buffered, it is returned unchanged and {@code imageType} is ignored.
     *
     * @param originalImage image to convert
     * @param imageType image type to use when a new buffer is created
     * @return a buffered representation of the image
     */
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

    /**
     * Creates an independent pixel copy of a buffered image, preserving its color model.
     *
     * @param source image to copy
     * @return a new buffered image containing the same pixels
     */
    public static BufferedImage copy(BufferedImage source) {
        ColorModel model = source.getColorModel();
        WritableRaster raster = source.copyData(null);
        return new BufferedImage(model, raster, model.isAlphaPremultiplied(), null);
    }

    /**
     * Resizes an image to fit the requested dimensions.
     * When aspect ratio preservation is enabled, the result fits within the requested bounds.
     *
     * @param originalImage image to resize
     * @param desiredWidth requested output width in pixels
     * @param desiredHeight requested output height in pixels
     * @param preserveAspectRatio whether to preserve the source aspect ratio
     * @return the resized image
     */
    public static Image resize(Image originalImage, int desiredWidth,
                               int desiredHeight, boolean preserveAspectRatio) {

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
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(originalImage, 0, 0, effectiveWidth, effectiveHeight,
                    0, 0, width, height, null);
            g.dispose();
        } else {
            resizedImage = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_RGB);
            var g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(originalImage, 0, 0, desiredWidth, desiredHeight,
                    0, 0, width, height, null);
            g.dispose();
        }

        return resizedImage;
    }

    /**
     * Resizes an image to fit within the requested dimensions while preserving its aspect ratio.
     *
     * @param originalImage image to resize
     * @param desiredWidth maximum output width in pixels
     * @param desiredHeight maximum output height in pixels
     * @return the resized image
     */
    public static Image resize(Image originalImage, int desiredWidth, int desiredHeight) {
        return resize(originalImage, desiredWidth, desiredHeight, true);
    }

    /**
     * Rotates an image by the specified angle, expanding the output dimensions
     * to contain the entire rotated image and centering it in the output.
     * The uncovered area is filled with the specified background paint.
     *
     * @param originalImage image to rotate
     * @param radians rotation angle in radians
     * @param background paint used to fill the uncovered area
     * @return the centered, rotated image
     */
    public static Image rotate(Image originalImage, double radians, Paint background) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int rotatedWidth = (int) (width * cos + height * sin);
        int rotatedHeight = (int) (width * sin + height * cos);

        AffineTransform at = new AffineTransform();
        at.translate(rotatedWidth / 2.0, rotatedHeight / 2.0);
        at.rotate(radians);
        at.translate(-width / 2.0, -height / 2.0);

        int imageType = background.getTransparency() == Transparency.OPAQUE
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        Image rotatedImage = new BufferedImage(rotatedWidth, rotatedHeight, imageType);

        var g = (Graphics2D) rotatedImage.getGraphics();
        g.setPaint(background);
        g.fillRect(0, 0, rotatedWidth, rotatedHeight);
        g.drawImage(originalImage, at, null);
        g.dispose();

        return rotatedImage;
    }

    /**
     * Rotates an image by the angle represented by {@code angle}.
     *
     * @param originalImage image to rotate
     * @param angle rotation to apply
     * @return the rotated image
     */
    public static Image rotate(Image originalImage, RotationAngle angle) {
        return rotate(originalImage, angle.getRadians(), Color.BLACK);
    }

    /**
     * Flips an image across the axis or axes represented by {@code axis}.
     *
     * @param originalImage image to flip
     * @param axis horizontal and/or vertical flip axis
     * @return the flipped image
     */
    public static Image flip(Image originalImage, FlipAxis axis) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);

        AffineTransform at = new AffineTransform();
        at.translate(axis.isHorizontal() ? width : 0, axis.isVertical() ? height : 0);
        at.scale(axis.isHorizontal() ? -1 : 1, axis.isVertical() ? -1 : 1);

        var flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) flippedImage.getGraphics();
        g.drawImage(originalImage, at, null);
        g.dispose();

        return flippedImage;
    }

    /**
     * Crops an image to the rectangular frame specified in source-image coordinates.<br/>
     * A crop that removes all available content returns a 1-by-1 placeholder image.
     *
     * @param originalImage image to crop
     * @param frame rectangle of content to retain
     * @return the cropped image
     * @throws IllegalArgumentException when the specified frame exceeds the source-image dimensions
     */
    public static Image crop(Image originalImage, Rectangle frame) {
        if (frame.x < 0 || frame.y < 0
                || frame.getMaxX() > originalImage.getWidth(null)
                || frame.getMaxY() > originalImage.getHeight(null))
            throw new IllegalArgumentException("Crop bounds exceed the original image size");

        if (frame.width <= 0 || frame.height <= 0)
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        Image croppedImage = new BufferedImage(frame.width, frame.height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) croppedImage.getGraphics();
        g.drawImage(originalImage, 0, 0, frame.width, frame.height,
                frame.x, frame.y, (int) frame.getMaxX(), (int) frame.getMaxY(), null);
        g.dispose();

        return croppedImage;
    }

    /**
     * Removes the specified number of pixels from each edge of an image.<br/>
     * A crop that removes all available content returns a 1-by-1 placeholder image.
     *
     * @param originalImage image to crop
     * @param left pixels to remove from the left edge
     * @param top pixels to remove from the top edge
     * @param right pixels to remove from the right edge
     * @param bottom pixels to remove from the bottom edge
     * @return the cropped image
     * @throws IllegalArgumentException if any of the crop amounts is negative
     */
    public static Image crop(Image originalImage, int left, int top, int right, int bottom) {
        int width = originalImage.getWidth(null) - left - right;
        int height = originalImage.getHeight(null) - top - bottom;
        return crop(originalImage, new Rectangle(left, top, width, height));
    }

    /**
     * Crops an image by the amounts specified in the insets.<br/>
     * A crop that removes all available content returns a 1-by-1 placeholder image.
     *
     * @param originalImage image to crop
     * @param insets crop amounts for the top, left, bottom, and right edges
     * @return the cropped image
     * @throws IllegalArgumentException if any of the crop insets is negative
     */
    public static Image crop(Image originalImage, Insets insets) {
        return crop(originalImage, insets.left, insets.top, insets.right, insets.bottom);
    }

    /**
     * Converts an image to grayscale using a weighted luminance approximation.
     *
     * @param originalImage image to convert
     * @return a grayscale buffered image
     */
    public static Image grayScale(Image originalImage) {
        BufferedImage image = buffer(originalImage, BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                var color = new Color(image.getRGB(x, y));
                int red = (int) (color.getRed() * 0.299);
                int green = (int) (color.getGreen() * 0.587);
                int blue = (int) (color.getBlue() * 0.114);
                int gray = red + green + blue;
                var gsColor = new Color(gray, gray, gray);
                image.setRGB(x, y, gsColor.getRGB());
            }
        }

        return image;
    }

    /**
     * Applies a sepia tone to an image using a standard RGB color transform.
     *
     * @param originalImage image to transform
     * @return a sepia-toned buffered image
     */
    public static Image sepia(Image originalImage) {
        BufferedImage image = buffer(originalImage, BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                int sepiaRed = Math.min(255, (int) (0.393 * red + 0.769 * green + 0.189 * blue));
                int sepiaGreen = Math.min(255, (int) (0.349 * red + 0.686 * green + 0.168 * blue));
                int sepiaBlue = Math.min(255, (int) (0.272 * red + 0.534 * green + 0.131 * blue));
                var sepiaColor = new Color(sepiaRed, sepiaGreen, sepiaBlue);

                image.setRGB(x, y, sepiaColor.getRGB());
            }
        }

        return image;
    }

    /**
     * Adds a border around an image, using the supplied paint and per-edge border sizes.
     *
     * @param originalImage image to surround
     * @param borderPaint paint used to fill the border
     * @param borderInsets border thickness on each edge
     * @return a new image containing the border and the original image
     */
    public static Image addBorder(Image originalImage, Paint borderPaint, Insets borderInsets) {
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);
        int width = originalWidth + borderInsets.left + borderInsets.right;
        int height = originalHeight + borderInsets.top + borderInsets.bottom;
        var imageWithBorder = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        var g = (Graphics2D) imageWithBorder.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setPaint(borderPaint);
        g.fillRect(0, 0, width, height);
        g.drawImage(originalImage, borderInsets.left, borderInsets.top, originalWidth, originalHeight, null);
        g.dispose();

        return imageWithBorder;
    }

    /**
     * Detects contiguous edge bands that exactly match the given color and removes them.
     * The thickness is measured independently from the original image on all four sides.
     *
     * @param originalImage image to inspect and crop
     * @param borderColor exact color to detect at the image edges
     * @return the cropped image, or the original image if no matching bands are found
     */
    public static Image stripBorder(Image originalImage, Color borderColor) {
        BufferedImage image = buffer(originalImage, BufferedImage.TYPE_INT_ARGB);
        int width = image.getWidth();
        int height = image.getHeight();
        int rgb = borderColor.getRGB();

        int top = 0;
        while (top < height && rowHasColor(image, rgb, top, width)) top++;

        int bottom = 0;
        while (bottom < height - top && rowHasColor(image, rgb, height - 1 - bottom, width)) bottom++;

        int left = 0;
        while (left < width && columnHasColor(image, rgb, left, height)) left++;

        int right = 0;
        while (right < width - left && columnHasColor(image, rgb, width - 1 - right, height)) right++;

        return crop(originalImage, left, top, right, bottom);
    }
    
    /**
     * Creates a tiled image from source files using a fixed number of rows and columns.
     * Each source image is scaled to cover its tile, with excess content cropped.
     *
     * @param sourceFiles image files to place in the tiles
     * @param width output image width in pixels
     * @param height output image height in pixels
     * @param rows number of tile rows
     * @param columns number of tile columns
     * @param background paint used behind the tiles
     * @return the composed tiled image
     * @throws IOException if a source image cannot be read
     * @throws IllegalArgumentException if the files or dimensions are invalid
     */
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
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
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

    /**
     * Creates a tiled image and chooses a near-square grid based on the number of source files.
     *
     * @param sourceFiles image files to place in the tiles
     * @param width output image width in pixels
     * @param height output image height in pixels
     * @param background paint used behind the tiles
     * @return the composed tiled image
     * @throws IOException if a source image cannot be read
     * @throws IllegalArgumentException if the files or dimensions are invalid
     */
    public static Image makeTiles(File[] sourceFiles, int width, int height, Paint background) throws IOException {
        if (ArrayUtil.isEmpty(sourceFiles)) throw new IllegalArgumentException("No source file provided");
        if (width <= 0) throw new IllegalArgumentException("width must be positive");
        if (height <= 0) throw new IllegalArgumentException("height must be positive");

        int rows = (int) Math.sqrt(sourceFiles.length);
        int columns = (int) Math.ceil((double) sourceFiles.length / rows);
        return makeTiles(sourceFiles, width, height, rows, columns, background);
    }

    /**
     * Extracts a sprite from a uniform grid of equally sized cells.
     * Insets are removed from the selected cell before it is returned.
     *
     * @param spriteSheet source sprite sheet
     * @param spriteX zero-based column of the sprite
     * @param spriteY zero-based row of the sprite
     * @param spriteWidth width of each grid cell
     * @param spriteHeight height of each grid cell
     * @param spriteInsets margins to remove from the selected cell
     * @return the extracted sprite
     */
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

    /**
     * Extracts a sprite from a grid whose columns and rows can have different sizes.
     * Insets are removed from the selected cell before it is returned.
     *
     * @param spriteSheet source sprite sheet
     * @param spriteX zero-based column of the sprite
     * @param spriteY zero-based row of the sprite
     * @param spriteWidths width of each column
     * @param spriteHeights height of each row
     * @param spriteInsets margins to remove from the selected cell
     * @return the extracted sprite
     */
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

    /**
     * Reads all frames from a GIF stream and composites each frame onto the logical canvas,
     * applying the GIF frame offsets and disposal instructions.
     *
     * @param stream input stream containing GIF data; the stream is closed when this method returns
     * @return frames in playback order, including each frame's delay
     * @throws IOException if the stream cannot be read or no GIF reader is available
     */
    public static List<ImageFrame> extractFrames(InputStream stream) throws IOException {
        try (ImageInputStream input = ImageIO.createImageInputStream(stream)) {
            if (input == null) throw new IOException("Unable to read image stream");

            var readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) throw new IOException("No GIF image reader available");

            ImageReader reader = readers.next();
            try {
                reader.setInput(input, false, false);

                int width = -1;
                int height = -1;
                Color background = null;

                IIOMetadata streamMetadata = reader.getStreamMetadata();
                if (streamMetadata != null) {
                    var root = (IIOMetadataNode) streamMetadata
                            .getAsTree(streamMetadata.getNativeMetadataFormatName());

                    NodeList descriptors = root.getElementsByTagName("LogicalScreenDescriptor");
                    if (descriptors.getLength() > 0) {
                        var descriptor = (IIOMetadataNode) descriptors.item(0);
                        width = Integer.parseInt(descriptor.getAttribute("logicalScreenWidth"));
                        height = Integer.parseInt(descriptor.getAttribute("logicalScreenHeight"));
                    }

                    NodeList tables = root.getElementsByTagName("GlobalColorTable");
                    if (tables.getLength() > 0) {
                        var table = (IIOMetadataNode) tables.item(0);
                        String backgroundIndex = table.getAttribute("backgroundColorIndex");

                        for (Node node = table.getFirstChild(); node != null; node = node.getNextSibling()) {
                            if (node instanceof IIOMetadataNode entry
                                    && entry.getAttribute("index").equals(backgroundIndex)) {
                                background = new Color(
                                        Integer.parseInt(entry.getAttribute("red")),
                                        Integer.parseInt(entry.getAttribute("green")),
                                        Integer.parseInt(entry.getAttribute("blue")));
                                break;
                            }
                        }
                    }
                }

                int frameCount = reader.getNumImages(true);
                if (frameCount == 0) return List.of();

                List<ImageFrame> frames = new ArrayList<>(frameCount);
                BufferedImage canvas = null;
                BufferedImage restoreCanvas = null;
                Rectangle bounds = null;
                String disposalMethod = null;

                for (int i = 0; i < frameCount; ++i) {
                    BufferedImage frame = reader.read(i);

                    if (canvas == null) {
                        if (width < 0 || height < 0) {
                            width = frame.getWidth();
                            height = frame.getHeight();
                        }
                        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                        if (background != null) {
                            Graphics2D g = canvas.createGraphics();
                            g.setPaint(background);
                            g.fillRect(0, 0, width, height);
                            g.dispose();
                        }
                    }

                    if (disposalMethod != null) {
                        switch (disposalMethod) {
                            case "restoreToPrevious" -> canvas = restoreCanvas;
                            case "restoreToBackgroundColor" -> {
                                Graphics2D g = canvas.createGraphics();
                                g.setComposite(AlphaComposite.Clear);
                                g.fill(bounds);
                                g.dispose();
                            }
                        }
                    }

                    IIOMetadata metadata = reader.getImageMetadata(i);
                    var root = (IIOMetadataNode) metadata.getAsTree("javax_imageio_gif_image_1.0");
                    var gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
                    disposalMethod = gce.getAttribute("disposalMethod");
                    int delay = Integer.parseInt(gce.getAttribute("delayTime"));
                    NodeList descriptors = root.getElementsByTagName("ImageDescriptor");
                    NamedNodeMap attrs = descriptors.item(0).getAttributes();
                    int x = Integer.parseInt(attrs.getNamedItem("imageLeftPosition").getNodeValue());
                    int y = Integer.parseInt(attrs.getNamedItem("imageTopPosition").getNodeValue());
                    bounds = new Rectangle(x, y, frame.getWidth(), frame.getHeight());
                    restoreCanvas = "restoreToPrevious".equals(disposalMethod) ? copy(canvas) : null;

                    Graphics2D g = canvas.createGraphics();
                    g.drawImage(frame, x, y, null);
                    g.dispose();

                    frames.add(new ImageFrame(copy(canvas), delay));
                }

                return frames;
            } finally {
                reader.dispose();
            }
        }
    }

    /**
     * Encodes an image in the requested ImageIO format.
     * PNG and GIF use an alpha-capable buffer; other formats use an RGB buffer.
     *
     * @param image image to encode
     * @param format ImageIO format name such as {@code png} or {@code jpg}
     * @return encoded bytes, or an empty array if encoding fails
     */
    public static byte[] toByteArray(Image image, String format) {
        int imageType = "png".equals(format) || "gif".equals(format)
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;

        try (var output = new ByteArrayOutputStream()) {
            ImageIO.write(buffer(image, imageType), format, output);
            return output.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Encodes an image as PNG.
     *
     * @param image image to encode
     * @return PNG bytes, or an empty array if encoding fails
     */
    public static byte[] toByteArray(Image image) {
        return toByteArray(image, "png");
    }

    /**
     * Decodes an image from encoded bytes using the installed ImageIO readers.
     *
     * @param bytes encoded image data
     * @return the decoded image, or {@code null} if decoding fails
     */
    public static Image fromByteArray(byte[] bytes) {
        try (var input = new ByteArrayInputStream(bytes)) {
            return ImageIO.read(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Reads an image file and encodes it as a Base64 data URL using its detected MIME type.
     *
     * @param imageFile image file to encode
     * @return a {@code data:} URL containing the file bytes
     * @throws IOException if the file cannot be read
     */
    public static String toBase64DataURL(File imageFile) throws IOException {
        byte[] fileBytes = FileUtil.readAllBytes(imageFile);
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        String mimeType = FileUtil.getContentType(imageFile.getName());
        return "data:" + mimeType + ";base64," + base64String;
    }

    /**
     * Decodes an image from a Base64 image data URL.
     *
     * @param base64String image data URL to decode
     * @return the decoded image, or {@code null} if the value is not a supported data URL
     * @throws IOException if image decoding fails
     * @throws IllegalArgumentException if the Base64 payload is malformed
     */
    public static Image fromBase64DataURL(String base64String) throws IOException {
        if (!base64String.startsWith("data:image/")) return null;
        int commaIndex = base64String.indexOf(',');
        if (commaIndex < 0) return null;
        byte[] bytes = Base64.getDecoder().decode(base64String.substring(commaIndex + 1));
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    /** Draws a source image scaled to cover a destination rectangle, cropping overflow centrally. */
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

    /** Returns whether every pixel in the specified row is within the color tolerance. */
    private static boolean rowHasColor(BufferedImage image, int rgb, int y, int width) {
        for (int x = 0; x < width; ++x) {
            if (!colorsMatch(image.getRGB(x, y), rgb)) return false;
        }
        return true;
    }

    /** Returns whether every pixel in the specified column is within the color tolerance. */
    private static boolean columnHasColor(BufferedImage image, int rgb, int x, int height) {
        for (int y = 0; y < height; ++y) {
            if (!colorsMatch(image.getRGB(x, y), rgb)) return false;
        }
        return true;
    }

    /** Matches two colors when the absolute difference in each RGB channel is at most the tolerance. */
    private static boolean colorsMatch(int first, int second) {
        return Math.abs(((first >> 16) & 0xff) - ((second >> 16) & 0xff)) <= COLOR_TOLERANCE
                && Math.abs(((first >> 8) & 0xff) - ((second >> 8) & 0xff)) <= COLOR_TOLERANCE
                && Math.abs((first & 0xff) - (second & 0xff)) <= COLOR_TOLERANCE;
    }

}
