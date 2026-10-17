package org.addy.util.document;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfHelperTest {

    @BeforeEach
    void setUp() {
        String[] generated = {"dummy-document.png", "jordan.pdf", "report.pdf", "combined.pdf"};
        File file;

        for (String name : generated) {
            file = new File("files/" + name);
            if (file.exists()) file.delete();
        }
    }

    @Test
    void getPageAsImageWorks() {
        try {
            File srcFile = new File("files/dummy-document.pdf");
            File destFile = new File("files/dummy-document.png");
            Image pageImage = PdfHelper.getPageAsImage(srcFile, 0);
            ImageIO.write((RenderedImage) pageImage, "png", destFile);
            assertTrue(destFile.isFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void convertImageToPdfWorks() {
        File imageFile = new File("files/jordan.jpg");
        File outFile = new File("files/jordan.pdf");

        try {
            PdfHelper.convertImageToPdf(ImageIO.read(imageFile), outFile);
            assertTrue(outFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void convertHtmlToPdfWorks() {
        File outFile = new File("files/report.pdf");

        try {
            PdfHelper.convertHtmlToPdf(HTML, outFile, null);
            assertTrue(outFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void mergeWorks() {
        File[] files = new File[] {
                new File("files/dummy-document.pdf"),
                new File("files/image-from-pexels.jpg"),
                new File("files/image-from-internet.png")};

        File outFile = new File("files/combined.pdf");

        try {
            PdfHelper.merge(outFile, files);
            assertTrue(outFile.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final String HTML = """
        <!DOCTYPE html>
        <html lang="fr">
        <head>
            <meta charset="UTF-8"/>
            <style>
                body {
                    font-family: sans-serif;
                    margin: 30px;
                }
    
                h1 {
                    color: #2455a4;
                }
    
                table {
                    width: 100%;
                    border-collapse: collapse;
                }
    
                th, td {
                    border: 1px solid #ccc;
                    padding: 8px;
                    text-align: left;
                }
            </style>
        </head>
        <body>
            <h1>Facture n° 2026-001</h1>
            <p>Client : Jean Dupont</p>

            <table>
                <tr>
                    <th>Description</th>
                    <th>Montant</th>
                </tr>
                <tr>
                    <td>Prestation informatique</td>
                    <td>500,00 $</td>
                </tr>
            </table>
        </body>
        </html>
        """;
}