package org.addy.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
	
	public static String getContentType(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            switch (getExtension(file.getPath())) {
                case ".json":
                    return "application/json";
                case ".jsonp":
                    return "application/javascript";
                default:
                    return "application/octet-stream";
            }
        }
        return contentType;
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
	
	public static void walkTree(File rootNode, FileFilter filter, TreeWalker treeWalker) {
		if (rootNode.isDirectory()) {
			if (treeWalker.beforeEnteringDirectory(rootNode)) {
				File[] childNodes = rootNode.listFiles(filter);
				for (File childNode : childNodes) {
					walkTree(childNode, filter, treeWalker);
				}
				treeWalker.afterExitingDirectory(rootNode);
			}
		} else {
			treeWalker.onLeaf(rootNode);
		}
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
	
	public static byte[] readAllBytes(InputStream input) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
        	copyStream(input, output);
        	return output.toByteArray();
        }
    }
	
	public static byte[] readAllBytes(File file) throws IOException {
		try (FileInputStream input = new FileInputStream(file)) {
			return readAllBytes(input);
		}
    }
	
	public static byte[] readAllBytes(String path) throws IOException {
		try (FileInputStream input = new FileInputStream(path)) {
			return readAllBytes(input);
		}
    }
	
	public static String readAllText(InputStream input) throws IOException {
		StringBuilder sb = new StringBuilder();
		String newLine = String.format("%n");
		String line;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
        	while ((line = reader.readLine()) != null) {
        		if (sb.length() != 0) sb.append(newLine);
        		sb.append(line);
        	}
        }
		
		return sb.toString();
    }
	
	public static String readAllText(File file) throws IOException {
		try (FileInputStream input = new FileInputStream(file)) {
			return readAllText(input);
		}
    }
	
	public static String readAllText(String path) throws IOException {
		try (FileInputStream input = new FileInputStream(path)) {
			return readAllText(input);
		}
    }
	
	public static String[] readAllLines(InputStream input) throws IOException {
		List<String> lines = new ArrayList<>();
		String line;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
        	while ((line = reader.readLine()) != null)
        		lines.add(line);
        }
		
		return lines.toArray(new String[] {});
    }
	
	public static String[] readAllLines(File file) throws IOException {
		try (FileInputStream input = new FileInputStream(file)) {
			return readAllLines(input);
		}
    }
	
	public static String[] readAllLines(String path) throws IOException {
		try (FileInputStream input = new FileInputStream(path)) {
			return readAllLines(input);
		}
    }
	
	public static void eachLine(InputStream input, LineConsumer consumer) throws IOException {
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
        	while ((line = reader.readLine()) != null)
        		consumer.consume(line);
        }
    }
	
	public static void eachLine(File file, LineConsumer consumer) throws IOException {
		try (FileInputStream input = new FileInputStream(file)) {
			eachLine(input, consumer);
		}
    }
	
	public static void eachLine(String path, LineConsumer consumer) throws IOException {
		try (FileInputStream input = new FileInputStream(path)) {
			eachLine(input, consumer);
		}
    }
	
	public static void writeAllBytes(File file, byte[] bytes, boolean append) throws IOException {
		try (FileOutputStream output = new FileOutputStream(file, append)) {
			output.write(bytes);
		}
    }
	
	public static void writeAllBytes(String path, byte[] bytes, boolean append) throws IOException {
		try (FileOutputStream output = new FileOutputStream(path, append)) {
			output.write(bytes);
		}
    }
	
	public static void writeAllText(OutputStream output, String text) {
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(output), true)) {
        	writer.write(text);
        }
    }
	
	public static void writeAllText(File file, String text, boolean append) throws IOException {
		try (FileOutputStream output = new FileOutputStream(file, append)) {
			writeAllText(output, text);
		}
    }
	
	public static void writeAllText(String path, String text, boolean append) throws IOException {
		try (FileOutputStream output = new FileOutputStream(path, append)) {
			writeAllText(output, text);
		}
    }
	
	public static void writeAllLines(OutputStream output, String[] lines) {
		String newLine = String.format("%n");
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(output), true)) {
        	for (int i = 0; i < lines.length; ++i) {
        		if (i > 0) writer.write(newLine);
        		writer.write(lines[i]);
        	}
        }
    }
	
	public static void writeAllLines(File file, String[] lines, boolean append) throws IOException {
		try (FileOutputStream output = new FileOutputStream(file, append)) {
			writeAllLines(output, lines);
		}
    }
	
	public static void writeAllLines(String path, String[] lines, boolean append) throws IOException {
		try (FileOutputStream output = new FileOutputStream(path, append)) {
			writeAllLines(output, lines);
		}
    }
	
	///////////////// INNER CLASSES AND INTERFACES ///////////////////
	
	public static interface LineConsumer {
		void consume(String line);
	}
	
	public static interface TreeWalker {
		boolean beforeEnteringDirectory(File node);
		void onLeaf(File node);
		void afterExitingDirectory(File node);
	}
	
	public abstract static class AbstractTreeWalker implements TreeWalker {
		public boolean beforeEnteringDirectory(File node) {
			return true;
		}

		public void afterExitingDirectory(File node) {
		}
	}
	
	public static class PatternFilter implements FileFilter {
		
		private final Pattern pattern;
		
		public PatternFilter(Pattern pattern) {
			this.pattern = pattern;
		}
		
		public PatternFilter(String pattern) {
			this.pattern = Pattern.compile(pattern);
		}

		@Override
		public boolean accept(File file) {
			return pattern.matcher(file.getPath()).matches();
		}

		public Pattern getPattern() {
			return pattern;
		}
		
	}
	
	public static class ExtensionFilter implements FileFilter {
		
		private final String[] extensions;
		
		public ExtensionFilter(String[] extensions) {
			this.extensions = extensions;
		}
		
		public ExtensionFilter(String extensions) {
			this.extensions = extensions.split("\\s*,\\s*");
		}

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) return true;
			
			String extension = getExtension(file.getPath());
			for (String ext : extensions) {
				if (ext.equalsIgnoreCase(extension))
					return true;
			}
			
			return false;
		}

		public String[] getExtensions() {
			return extensions;
		}
		
	}
	
	public static class ContentTypeFilter implements FileFilter {
		
		private final String[] contentTypes;
		private Pattern[] patterns;
		
		public ContentTypeFilter(String[] contentTypes) {
			this.contentTypes = contentTypes;
			initPatterns();
		}
		
		public ContentTypeFilter(String contentTypes) {
			this.contentTypes = contentTypes.split("\\s*,\\s*");
			initPatterns();
		}

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) return true;
			
			try {
				String contentType = getContentType(file);
				for (Pattern pattern : patterns) {
					if (pattern.matcher(contentType).matches())
						return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return false;
		}

		public String[] getContentTypes() {
			return contentTypes;
		}
		
		protected void initPatterns() {
			patterns = new Pattern[contentTypes.length];
			for (int i = 0; i < patterns.length; ++i) {
				String regex = contentTypes[i].replace("*", ".*");
				patterns[i] = Pattern.compile(regex);
			}
		}
		
	}
}
