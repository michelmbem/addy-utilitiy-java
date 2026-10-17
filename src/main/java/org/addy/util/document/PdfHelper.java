package org.addy.util.document;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.addy.util.ArrayUtil;
import org.addy.util.FileUtil;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public final class PdfHelper {
    public static final String CONTENT_TYPE = "application/pdf";
    private static final float SCREEN_DPI = 96f;
    private static final float PDF_DPI = 72f;
    private static final float DPI_RATIO = PDF_DPI / SCREEN_DPI;
    private static final MemoryUsageSetting MEMORY_USAGE = MemoryUsageSetting.setupMixed(50L * 1024 * 1024);
    
    private PdfHelper() {}

    public static Image getPageAsImage(File pdfFile, int pageNumber) {
        BufferedImage image = null;

        try (PDDocument document = PDDocument.load(pdfFile)) {
            var renderer = new PDFRenderer(document);
            image = renderer.renderImage(pageNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static Image getPageAsImage(String fileName, int pageNumber) {
        return getPageAsImage(new File(fileName), pageNumber);
    }

    public static void convertImageToPdf(BufferedImage image, OutputStream output) throws IOException {
        Objects.requireNonNull(image, "image cannot be null");
        Objects.requireNonNull(output, "outputFile cannot be null");

        if (image.getWidth() <= 0 || image.getHeight() <= 0)
            throw new IllegalArgumentException("Invalid image size");

        float width = image.getWidth() * DPI_RATIO;
        float height = image.getHeight() * DPI_RATIO;
        var pageSize = new PDRectangle(width, height);

        try (var document = new PDDocument()) {
            var page = new PDPage(pageSize);
            document.addPage(page);

            try (var content = new PDPageContentStream(document, page)) {
                var pdImage = LosslessFactory.createFromImage(document, image);
                content.drawImage(pdImage, 0, 0, width, height);
            }

            document.save(output);
            output.flush();
        }
    }

    public static void convertImageToPdf(BufferedImage image, File outputFile) throws IOException {
        try (var output = new FileOutputStream(outputFile)) {
            convertImageToPdf(image, output);
        }
    }

    public static void convertImageToPdf(BufferedImage image, String outputPath) throws IOException {
        try (var output = new FileOutputStream(outputPath)) {
            convertImageToPdf(image, output);
        }
    }

    public static void convertHtmlToPdf(String html, OutputStream output, String baseUri) throws IOException {
        Objects.requireNonNull(output, "output cannot be null");
        new PdfRendererBuilder().withHtmlContent(html, baseUri).toStream(output).run();
        output.flush();
    }

    public static void convertHtmlToPdf(String html, File outputFile, String baseUri) throws IOException {
        try (var output = new FileOutputStream(outputFile)) {
            convertHtmlToPdf(html, output, baseUri);
        }
    }

    public static void convertHtmlToPdf(String html, String outputPath, String baseUri) throws IOException {
        try (var output = new FileOutputStream(outputPath)) {
            convertHtmlToPdf(html, output, baseUri);
        }
    }

    public static void merge(File outputFile, File... inputFiles) throws IOException {
        Objects.requireNonNull(outputFile, "outputFile cannot be null");

        if (ArrayUtil.isEmpty(inputFiles))
            throw new IllegalArgumentException("No input file provided");

        var merger = new PDFMergerUtility();
        List<File> generatedFiles = new LinkedList<>();
        Stream.of(inputFiles).forEach(file -> mergeFile(file, merger, generatedFiles));
        merger.setDestinationFileName(outputFile.getPath());
        merger.mergeDocuments(MEMORY_USAGE);
        generatedFiles.forEach(File::delete);
    }

    public static void merge(String outputPath, String... inputPaths) throws IOException {
        merge(new File(outputPath), Stream.of(inputPaths).map(File::new).toArray(File[]::new));
    }

    private static void mergeFile(File file, PDFMergerUtility merger, List<File> generatedFiles) {
        try {
            String contentType = FileUtil.getContentType(file.getName());

            if (CONTENT_TYPE.equals(contentType)) {
                merger.addSource(file);
            } else if (contentType != null) {
                File pdfFile = new File(FileUtil.changeExtension(file.getPath(), ".pdf"));

                if (contentType.startsWith("image/"))
                    convertImageToPdf(ImageIO.read(file), pdfFile);
                else if (contentType.startsWith("text/"))
                    convertHtmlToPdf(FileUtil.readAllText(file), pdfFile, null);
                else
                    throw new IllegalArgumentException("Could not convert file to PDF : " + file);

                merger.addSource(pdfFile);
                generatedFiles.add(pdfFile);
            } else {
                throw new IllegalArgumentException("Could not convert file to PDF : " + file);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
