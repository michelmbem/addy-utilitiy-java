package org.addy.util;

import java.awt.Component;
import java.awt.Dimension;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRGraphics2DExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.ExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleGraphics2DExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleJsonExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;
import net.sf.jasperreports.swing.JRViewer;

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
		frame.getContentPane().add((Component) viewer);
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
		switch (format) {
			case ".pdf":
				return new JRPdfExporter();
			case ".jpg":
			case ".jpeg":
			case ".png":
				return new JRGraphics2DExporter();
			case ".rtf":
				return new JRRtfExporter();
			case ".docx":
				return new JRDocxExporter();
			case ".xls":
				return new JRXlsExporter();
			case ".xlsx":
				return new JRXlsxExporter();
			case ".pptx":
				return new JRPptxExporter();
			case ".odt":
				return new JROdtExporter();
			case ".ods":
				return new JROdsExporter();
			case ".txt":
				return new JRTextExporter();
			case ".csv":
				return new JRCsvExporter();
			case ".json":
				return new JsonExporter();
			case ".xml":
			case ".jrpxml":
				return new JRXmlExporter();
			case ".htm":
			case ".html":
				return new HtmlExporter();
			default:
				return null;
		} 
	}

	private static ExporterOutput getExporterOutput(String path) {
		switch (FileUtil.getExtension(path).toLowerCase()) {
			case ".jpg":
			case ".jpeg":
			case ".png":
				return new SimpleGraphics2DExporterOutput();
			case ".txt":
			case ".csv":
				return new SimpleWriterExporterOutput(path);
			case ".json":
				return new SimpleJsonExporterOutput(path);
			case ".xml":
			case ".jrpxml":
				return new  SimpleXmlExporterOutput(path);
			case ".htm":
			case ".html":
				return new SimpleHtmlExporterOutput(path);
			default:
				return new SimpleOutputStreamExporterOutput(path);
		}
	}

	private static ExporterOutput getExporterOutput(OutputStream out, String format) {
		switch (format) {
			case ".jpg":
			case ".jpeg":
			case ".png":
				return new SimpleGraphics2DExporterOutput();
			case ".txt":
			case ".csv":
				return new SimpleWriterExporterOutput(out);
			case ".json":
				return new SimpleJsonExporterOutput(out);
			case ".xml":
			case ".jrpxml":
				return new  SimpleXmlExporterOutput(out);
			case ".htm":
			case ".html":
				return new SimpleHtmlExporterOutput(out);
			default:
				return new SimpleOutputStreamExporterOutput(out);
		}
	}
}
