package org.addy.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.json.export.JsonExporter;
import net.sf.jasperreports.json.export.SimpleJsonExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.poi.export.JRXlsExporter;
import net.sf.jasperreports.swing.JRViewer;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.ResourceBundle;

public final class JRHelper {
	private JRHelper() {}
	
	public static void viewReport(String path, Map<String, Object> parameters, Connection connection) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, connection);
		printPreview(jPrint);
	}

	public static void viewReport(String path, Map<String, Object> parameters, JRDataSource dataSource) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, dataSource);
		printPreview(jPrint);
	}

	public static void printReport(String path, Map<String, Object> parameters, Connection connection) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, connection);
		JasperPrintManager.printReport(jPrint, true);
	}

	public static void printReport(String path, Map<String, Object> parameters, JRDataSource dataSource) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, dataSource);
		JasperPrintManager.printReport(jPrint, true);
	}

	public static void exportReport(String path, Map<String, Object> parameters, Connection connection, String destPath) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, connection);
		exportToFile(jPrint, destPath);
	}

	public static void exportReport(String path, Map<String, Object> parameters, JRDataSource dataSource, String destPath) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, dataSource);
		exportToFile(jPrint, destPath);
	}

	public static void exportReport(String path, Map<String, Object> parameters, Connection connection, OutputStream out, String format) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, connection);
		exportToStream(jPrint, out, format);
	}

	public static void exportReport(String path, Map<String, Object> parameters, JRDataSource dataSource, OutputStream out, String format) throws Exception {
		JasperPrint jPrint = JasperFillManager.fillReport(path, parameters, dataSource);
		exportToStream(jPrint, out, format);
	}

	private static void printPreview(JasperPrint jPrint) {
		JFrame frame = new JFrame();
		ImageIcon icon = new ImageIcon(JRHelper.class.getClassLoader().getResource("viewer.png"));
		frame.setIconImage(icon.getImage());
		ResourceBundle resources = ResourceBundle.getBundle("JRHelper");
		frame.setTitle(resources.getString("frame.title"));
		Dimension screenSize = frame.getToolkit().getScreenSize();
		frame.setSize(2 * screenSize.width / 3, 2 * screenSize.height  / 3);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		JRViewer viewer = new JRViewer(jPrint);
		frame.getContentPane().add(viewer);
		frame.setVisible(true);
		viewer.setFitPageZoomRatio();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void exportToFile(JasperPrint jPrint, String destPath) throws JRException {
		Exporter exporter = getExporter(FileUtil.getExtension(destPath).toLowerCase());
		if (exporter != null) {
			exporter.setExporterInput(new SimpleExporterInput(jPrint));
			exporter.setExporterOutput(getExporterOutput(destPath));
			exporter.exportReport();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void exportToStream(JasperPrint jPrint, OutputStream out, String format) throws JRException {
		Exporter exporter = getExporter(format);
		if (exporter != null) {
			exporter.setExporterInput(new SimpleExporterInput(jPrint));
			exporter.setExporterOutput(getExporterOutput(out, format));
			exporter.exportReport();
		}
	}

	@SuppressWarnings("rawtypes")
	private static Exporter getExporter(String format) throws JRException {
        return switch (format) {
            case ".pdf" -> new JRPdfExporter();
            case ".jpg", ".jpeg", ".png" -> new JRGraphics2DExporter();
            case ".rtf" -> new JRRtfExporter();
            case ".docx" -> new JRDocxExporter();
            case ".xls" -> new JRXlsExporter();
            case ".xlsx" -> new JRXlsxExporter();
            case ".pptx" -> new JRPptxExporter();
            case ".odt" -> new JROdtExporter();
            case ".ods" -> new JROdsExporter();
            case ".txt" -> new JRTextExporter();
            case ".csv" -> new JRCsvExporter();
            case ".json" -> new JsonExporter();
            case ".xml", ".jrpxml" -> new JRXmlExporter();
            case ".htm", ".html" -> new HtmlExporter();
            default -> null;
        };
	}

	private static ExporterOutput getExporterOutput(String path) {
        return switch (FileUtil.getExtension(path).toLowerCase()) {
            case ".jpg", ".jpeg", ".png" -> new SimpleGraphics2DExporterOutput();
            case ".txt", ".csv" -> new SimpleWriterExporterOutput(path);
            case ".json" -> new SimpleJsonExporterOutput(path);
            case ".xml", ".jrpxml" -> new SimpleXmlExporterOutput(path);
            case ".htm", ".html" -> new SimpleHtmlExporterOutput(path);
            default -> new SimpleOutputStreamExporterOutput(path);
        };
	}

	private static ExporterOutput getExporterOutput(OutputStream out, String format) {
        return switch (format) {
            case ".jpg", ".jpeg", ".png" -> new SimpleGraphics2DExporterOutput();
            case ".txt", ".csv" -> new SimpleWriterExporterOutput(out);
            case ".json" -> new SimpleJsonExporterOutput(out);
            case ".xml", ".jrpxml" -> new SimpleXmlExporterOutput(out);
            case ".htm", ".html" -> new SimpleHtmlExporterOutput(out);
            default -> new SimpleOutputStreamExporterOutput(out);
        };
	}
}
