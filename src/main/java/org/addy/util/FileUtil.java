package org.addy.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public final class FileUtil {
	
	private FileUtil() {
	}

	public static boolean exists(String path) {
		return (new File(path)).exists();
	}

	public static boolean isDirectory(String path) {
		return (new File(path)).isDirectory();
	}

	public static boolean isFile(String path) {
		return (new File(path)).isFile();
	}

	public static boolean isHidden(String path) {
		return (new File(path)).isHidden();
	}

	public static String getDirectory(String path) {
		return (new File(path)).getParent();
	}

	public static String getFileName(String path) {
		return (new File(path)).getName();
	}

	public static String getExtension(String fileName) {
		if (fileName != null) {
			int lastDot = fileName.lastIndexOf(".");
			if (lastDot > fileName.lastIndexOf(File.separator)) {
				return fileName.substring(lastDot);
			}
		}
		return "";
	}

	public static String changeExtension(String fileName, String newExtension) {
		if (fileName != null) {
			int lastDot = fileName.lastIndexOf(".");
			if (lastDot > fileName.lastIndexOf(File.separator)) {
				return String.valueOf(fileName.substring(0, lastDot)) + newExtension;
			}
		}
		return fileName;
	}

	public static String combine(String path1, String path2) {
		return (new File(new File(path1), path2)).getPath();
	}

	public static String getUserDir() {
		return System.getProperty("user.home");
	}

	public static String getCurrentDir() {
		return System.getProperty("user.dir");
	}

	public static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	public static boolean move(String source, String dest) {
		return (new File(source)).renameTo(new File(dest));
	}

	public static int copyStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[1024];
		int total = 0;
		int amount;
		
		while ((amount = input.read(buffer)) > 0) {
			output.write(buffer, 0, amount);
			total += amount;
		}

		return total;
	}

	public static boolean copy(File sourceFile, File destFile) throws IOException {
		if (!sourceFile.exists()) return false;

		if (destFile.isDirectory())
			destFile = new File(destFile, sourceFile.getName());

		if (sourceFile.isDirectory()) {
			if (!destFile.isDirectory() && !destFile.mkdirs())
				return false;

			for (File childFile : sourceFile.listFiles()) {
				if (!copy(childFile, new File(destFile, childFile.getName())))
					return false;
			}

			return true;
		}

		File destDir = destFile.getParentFile();
		if (!destDir.isDirectory() && !destDir.mkdirs()) {
			return false;
		}
		
		try (InputStream input = new FileInputStream(sourceFile)) {
			try (OutputStream output = new FileOutputStream(destFile)) {
				copyStream(input, output);
			}
		}

		return true;
	}

	public static boolean copy(String source, String dest) throws IOException {
		return copy(new File(source), new File(dest));
	}

	public static boolean delete(File file) {
		if (file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				if (!delete(childFile))
					return false;
			}

		}
		return file.delete();
	}

	public static boolean delete(String path) {
		return delete(new File(path));
	}

	public static InputStream open(String path) throws FileNotFoundException {
		return new FileInputStream(path);
	}

	public static OutputStream create(String path) throws FileNotFoundException {
		return new FileOutputStream(path, false);
	}

	public static OutputStream append(String path) throws FileNotFoundException {
		return new FileOutputStream(path, true);
	}

	public static BufferedReader openText(String path) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
	}

	public static PrintWriter createText(String path) throws FileNotFoundException {
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(path, false)), true);
	}

	public static PrintWriter appendText(String path) throws FileNotFoundException {
		return new PrintWriter(new OutputStreamWriter(new FileOutputStream(path, true)), true);
	}
}
