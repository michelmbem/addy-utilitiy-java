package org.addy.util.document;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class PdfHelper {
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
}
