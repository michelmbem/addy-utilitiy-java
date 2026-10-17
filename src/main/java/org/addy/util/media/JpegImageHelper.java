package org.addy.util.media;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifReader;
import org.addy.util.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.addy.util.media.ImageHelper.flip;
import static org.addy.util.media.ImageHelper.rotate;

public final class JpegImageHelper {
    public static final String CONTENT_TYPE = "image/jpeg";
    public static final String FORMAT = "jpeg";

    private JpegImageHelper() {}

    public static boolean isJpegFile(String fileName) {
        return CONTENT_TYPE.equals(FileUtil.getContentType(fileName));
    }

    public static ImageOrientation getOrientationFromExif(File file) {
        if (!isJpegFile(file.getName())) return ImageOrientation.UNDETERMINED;

        try {
            Iterable<JpegSegmentMetadataReader> readers = List.of(new ExifReader());
            Metadata metadata = JpegMetadataReader.readMetadata(file, readers);
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
            ImageIO.write((RenderedImage) image, FORMAT, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
