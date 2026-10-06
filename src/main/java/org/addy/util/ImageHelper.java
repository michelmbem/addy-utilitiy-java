package org.addy.util;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;
import org.jcodec.scale.AWTUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

public final class ImageHelper {

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

    public enum ImageOrientation {
        UNDETERMINED,
        NORMAL,
        FLIP_HORIZONTAL,
        ROTATE_180,
        FLIP_VERTICAL,
        FLIP_VERTICAL_ROTATE_CW,
        ROTATE_CW,
        FLIP_HORIZONTAL_ROTATE_CW,
        ROTATE_CCW
    }

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

    public static Image resize(Image originalImage,
                               int desiredWidth,
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

            resizedImage = new BufferedImage(
                    effectiveWidth,
                    effectiveHeight,
                    BufferedImage.TYPE_INT_RGB);
            var g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    originalImage, 0, 0, effectiveWidth, effectiveHeight,
                    0, 0, width, height, null);
            g.dispose();
        } else {
            resizedImage = new BufferedImage(
                    desiredWidth,
                    desiredHeight,
                    BufferedImage.TYPE_INT_RGB);
            var g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(
                    originalImage, 0, 0, desiredWidth, desiredHeight,
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
                g.drawImage(
                        originalImage, i, 0, i + 1, height,
                        j, 0, j + 1, height, null);
            }
        }

        if (axis.isVertical()) {
            for (int i = 0, j = height - 1; i < height; ++i, --j) {
                g.drawImage(
                        originalImage, 0, i, width, i + 1,
                        0, j, width, j + 1, null);
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

        Image croppedImage = new BufferedImage(
                croppedWidth,
                croppedHeight,
                BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) croppedImage.getGraphics();
        g.drawImage(
                originalImage, 0, 0, croppedWidth, croppedHeight,
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
    
    public static Image makeTiles(File[] sourceFiles, int width, int height, Paint bg)
            throws IOException {

        var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setPaint(bg);
        g.fillRect(0, 0, width, height);
        
        int row = 0, col = 0;
        for (File sourceFile : sourceFiles) {
        	BufferedImage tile = ImageIO.read(sourceFile);
            paintCover(g, tile, col * width / 2, row * height / 2, width / 2, height / 2);
            if (++col >= 2) {
                col = 0;
                if (++row >= 2)
                    break;
            }
        }
        
        return image;
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

		g.drawImage(
                image, left, top, left + width, top + height,
                x, y, x + w, y + h, null);
	}
    
    public static ImageOrientation getOrientationFromExif(File jpegFile) {
        try {
        	if (!FileUtil.getContentType(jpegFile).equalsIgnoreCase("image/jpeg"))
                return ImageOrientation.UNDETERMINED;
        	
            Iterable<JpegSegmentMetadataReader> readers = List.of(new ExifReader());
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile, readers);
            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            return directory != null
                    ? ImageOrientation.values()[
                            directory.getInt(ExifDirectoryBase.TAG_ORIENTATION)]
                    : ImageOrientation.UNDETERMINED;
        } catch (IOException | MetadataException | JpegProcessingException e) {
            e.printStackTrace();
            return ImageOrientation.UNDETERMINED;
        }
    }
    
    public static void correctOrientation(File sourceFile, File destFile) {
        ImageOrientation orientation = getOrientationFromExif(sourceFile);
        if (orientation.ordinal() <= ImageOrientation.NORMAL.ordinal()) return;

        try {
            Image image = ImageIO.read(sourceFile);
            image = switch (orientation) {
                case FLIP_HORIZONTAL -> flip(image, FlipAxis.Y);
                case FLIP_VERTICAL -> flip(image, FlipAxis.X);
                case ROTATE_CW -> rotate(image, RotationAngle.CW);
                case ROTATE_CCW -> rotate(image, RotationAngle.CCW);
                case ROTATE_180 -> rotate(image, RotationAngle.DCW);
                case FLIP_HORIZONTAL_ROTATE_CW ->
                        rotate(flip(image, FlipAxis.Y), RotationAngle.CW);
                default -> rotate(flip(image, FlipAxis.X), RotationAngle.CW);
            };
            ImageIO.write((RenderedImage) image, "jpg", destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toBase64DataURL(File imageFile) throws IOException {
        byte[] fileBytes = FileUtil.readAllBytes(imageFile);
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        String mimeType = FileUtil.getContentType(imageFile);
        return "data:" + mimeType + ";base64," + base64String;
    }

    public static Image fromBase64DataURL(String base64String) throws IOException {
    	if (!base64String.startsWith("data:image/")) return null;
    	int commaIndex = base64String.indexOf(',');
    	if (commaIndex < 0) return null;
    	byte[] bytes = Base64.getDecoder().decode(base64String.substring(commaIndex + 1));
    	return ImageIO.read(new ByteArrayInputStream(bytes));
    }
    
    public static Image getImageFromPDFPage(File pdfFile, int pageNumber) {
        BufferedImage image = null;
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            var renderer = new PDFRenderer(document);
            image = renderer.renderImage(pageNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return image;
    }
    
    public static BufferedImage extractVideoFrame(File videoFile, float relativePosition) {
        BufferedImage capturedFrame = null;
        int frameNumber;
        
        try {
            SeekableByteChannel channel = NIOUtils.readableFileChannel(videoFile.getPath());
            
            try (MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(channel)) {
                DemuxerTrack videoTrack = demuxer.getVideoTrack();
                
                if (relativePosition >= 1)
                    frameNumber = (int) relativePosition;
                else if (relativePosition >= 0)
                    frameNumber = (int) (videoTrack.getMeta().getTotalFrames() * relativePosition);
                else
                    frameNumber = (int) (videoTrack.getMeta().getTotalFrames() + relativePosition);
            }
            
            Picture picture = FrameGrab.getFrameFromFile(videoFile, frameNumber);
            capturedFrame = AWTUtil.toBufferedImage(picture);
        } catch (IOException | JCodecException e) {
            e.printStackTrace();
        }
        
        return capturedFrame;
    }

}
