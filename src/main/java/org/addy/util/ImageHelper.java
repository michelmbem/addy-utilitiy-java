package org.addy.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;

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

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifReader;

public final class ImageHelper {

    public static final int CW = 1;
    public static final int CCW = -1;
    public static final int DCW = 2;

    public static final int AXIS_X = 1;
    public static final int AXIS_Y = 2;
    public static final int AXIS_XY = 3;
    
    public static final int ORIENTATION_UNDETERMINED = 0;
    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
    public static final int ORIENTATION_ROTATE_180 = 3;
    public static final int ORIENTATION_FLIP_VERTICAL = 4;
    public static final int ORIENTATION_FLIP_VERTICAL_ROTATE_CW = 5;
    public static final int ORIENTATION_ROTATE_CW = 6;
    public static final int ORIENTATION_FLIP_HORIZONTAL_ROTATE_CW = 7;
    public static final int ORIENTATION_ROTATE_CCW = 8;

    private ImageHelper() {
    }
    
    public static BufferedImage buffer(Image originalImage, int imageType) {
        if (originalImage instanceof BufferedImage)
        	return (BufferedImage) originalImage;
        
        BufferedImage bufferedImage = new BufferedImage(
        		originalImage.getWidth(null), originalImage.getHeight(null), imageType);
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();
        
        return bufferedImage;
    }

    public static Image resize(Image originalImage, int desiredWidth, int desiredHeight, boolean preserveAspectRatio) {
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
            Graphics2D g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, effectiveWidth, effectiveHeight, 0, 0, width, height, null);
            g.dispose();
        } else {
            resizedImage = new BufferedImage(desiredWidth, desiredHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = (Graphics2D) resizedImage.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, desiredWidth, desiredHeight, 0, 0, width, height, null);
            g.dispose();
        }

        return resizedImage;
    }

    public static Image rotate(Image originalImage, int direction) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        double angle = direction * Math.PI / 2;
        AffineTransform at = AffineTransform.getRotateInstance(angle, width / 2.0, height / 2.0);
        
        Image rotatedImage;
        if (direction == DCW) {
            rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        } else {
            at.translate(direction * (width - height) / 2.0, direction * (width - height) / 2.0);
            rotatedImage = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        }
        
        Graphics2D g = (Graphics2D) rotatedImage.getGraphics();
        g.drawImage(originalImage, at, null);
        g.dispose();

        return rotatedImage;
    }

    public static Image flip(Image originalImage, int axis) {
        int width = originalImage.getWidth(null);
        int height = originalImage.getHeight(null);
        Image flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) flippedImage.getGraphics();

        if ((axis & AXIS_X) != 0) {
            for (int i = 0, j = height - 1; i < height; ++i, --j) {
                g.drawImage(originalImage, 0, i, width, i + 1, 0, j, width, j + 1, null);
            }
        }

        if ((axis & AXIS_Y) != 0) {
            for (int i = 0, j = width - 1; i < width; ++i, --j) {
                g.drawImage(originalImage, i, 0, i + 1, height, j, 0, j + 1, height, null);
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
        int newWidth = width - left - right;
        int newHeight = height - top - bottom;

        if (newWidth <= 0 || newHeight <= 0)
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        Image croppedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) croppedImage.getGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, left, top, width - right, height - bottom, null);
        g.dispose();

        return croppedImage;
    }

    public static Image grayScale(Image originalImage) {
        BufferedImage image = buffer(originalImage, BufferedImage.TYPE_INT_RGB);
        int width = image.getWidth();
        int height = image.getHeight();
        
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                image.setRGB(j, i, newColor.getRGB());
            }
        }

        return image;
    }
    
    public static Image makeTiles(File[] sourceFiles, int width, int height, Paint bg) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
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

	private static void paintCover(Graphics2D g, BufferedImage image, int left, int top, int width, int height) {
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

		g.drawImage(image, left, top, left + width, top + height, x, y, x + w, y + h, null);
	}
    
    public static int getOrientationFromExif(File jpegFile) {
        try {
        	if (!FileUtil.getContentType(jpegFile).equalsIgnoreCase("image/jpeg"))
                return ORIENTATION_UNDETERMINED;
        	
            Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader());
            Metadata metadata = JpegMetadataReader.readMetadata(jpegFile, readers);
            Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (directory == null) return ORIENTATION_UNDETERMINED;
            return directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (IOException | MetadataException | JpegProcessingException ex) {
            ex.printStackTrace();
            return ORIENTATION_UNDETERMINED;
        }
    }
    
    public static void correctOrientation(File sourceFile, File destFile) {
        int orientation = getOrientationFromExif(sourceFile);
        if (orientation <= ORIENTATION_NORMAL) return;

        try {
            Image image = ImageIO.read(sourceFile);

            switch (orientation) {
                case ORIENTATION_FLIP_HORIZONTAL:
                    image = flip(image, AXIS_Y);
                    break;
                case ORIENTATION_FLIP_VERTICAL:
                    image = flip(image, AXIS_X);
                    break;
                case ORIENTATION_ROTATE_CW:
                    image = rotate(image, CW);
                    break;
                case ORIENTATION_ROTATE_CCW:
                    image = rotate(image, CCW);
                    break;
                case ORIENTATION_ROTATE_180:
                    image = rotate(image, DCW);
                    break;
                case ORIENTATION_FLIP_HORIZONTAL_ROTATE_CW:
                    image = rotate(flip(image, AXIS_Y), CW);
                    break;
                default:
                    image = rotate(flip(image, AXIS_X), CW);
                    break;
            }

            ImageIO.write((RenderedImage) image, "jpg", destFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String encodeBase64(File imageFile) throws IOException {
        byte[] fileBytes = FileUtil.readAllBytes(imageFile);
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        String mimeType = FileUtil.getContentType(imageFile);
        return "data:" + mimeType + ";base64," + base64String;
    }

    public static Image decodeBase64(String base64String) throws IOException {
    	if (!base64String.startsWith("data:image/")) return null;
    	int commaIndex = base64String.indexOf(',');
    	if (commaIndex < 0) return null;
    	byte[] bytes = Base64.getDecoder().decode(base64String.substring(commaIndex + 1));
    	return ImageIO.read(new ByteArrayInputStream(bytes));
    }
    
    public static Image getImageFromPDFPage(File pdfFile, int pageNumber) {
        BufferedImage image = null;
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            image = renderer.renderImage(pageNumber);
        } catch (IOException ex) {
            ex.printStackTrace();
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
        } catch (IOException | JCodecException ex) {
            ex.printStackTrace();
        }
        
        return capturedFrame;
    }

}
