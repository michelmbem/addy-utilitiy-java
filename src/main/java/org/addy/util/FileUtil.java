package org.addy.util;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public final class FileUtil {
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    private static final String CONTENT_TYPES_RESOURCE = "content-types.properties";
    private static final Map<String, String> EXTENSION_TO_CONTENT_TYPE;
    private static final Map<String, String> CONTENT_TYPE_TO_EXTENSION;

    static {
        var properties = new Properties();

        try (InputStream input = FileUtil.class.getClassLoader()
                .getResourceAsStream(CONTENT_TYPES_RESOURCE)) {

            properties.load(input);
        } catch (NullPointerException | IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        Map<String, String> extensionToContentType = new HashMap<>();
        Map<String, String> contentTypeToExtension = new HashMap<>();

        for (Map.Entry<?, ?> entry : properties.entrySet()) {
            String extension = entry.getKey().toString();
            String contentType = entry.getValue().toString();
            extensionToContentType.put(extension, contentType);
            contentTypeToExtension.putIfAbsent(contentType, extension);
        }

        EXTENSION_TO_CONTENT_TYPE = Collections.unmodifiableMap(extensionToContentType);
        CONTENT_TYPE_TO_EXTENSION = Collections.unmodifiableMap(contentTypeToExtension);
    }

    private FileUtil() {}

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
                return fileName.substring(0, lastDot) + newExtension;
            }
            return fileName + newExtension;
        }
        return null;
    }

    public static String getExtensionFromContentType(String contentType) {
        if (StringUtil.isBlank(contentType)) return null;

        String extension = CONTENT_TYPE_TO_EXTENSION.get(normalizeContentType(contentType));
        return extension != null ? '.' + extension : null;
    }

    public static String getContentType(String fileName) {
        String extension = getExtension(fileName);
        if (StringUtil.isBlank(extension)) return null;

        return EXTENSION_TO_CONTENT_TYPE.getOrDefault(
                normalizeExtension(extension),
                DEFAULT_CONTENT_TYPE);
    }

    public static String combine(String root, String firstChild, String... moreChildren) {
        File combined = new File(root, firstChild);
        for (String child : moreChildren) {
            combined = new File(combined, child);
        }
        return combined.getPath();
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

    public static int copyStream(InputStream input, OutputStream output, int bufferSize)
            throws IOException {

        if (input == null) throw new IllegalArgumentException("The input stream cannot be null");
        if (output == null) throw new IllegalArgumentException("The output stream cannot be null");
        if (bufferSize <= 0) throw new IllegalArgumentException("The buffer size should be positive");

        var buffer = new byte[bufferSize];
        int amount, total = 0;

        while ((amount = input.read(buffer)) > 0) {
            output.write(buffer, 0, amount);
            total += amount;
        }

        return total;
    }

    public static int copyStream(InputStream input, OutputStream output) throws IOException {
        return copyStream(input, output, 8192);
    }

    public static boolean copy(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) return false;

        if (destFile.isDirectory())
            destFile = new File(destFile, sourceFile.getName());

        if (sourceFile.isDirectory()) {
            if (!(destFile.isDirectory() || destFile.mkdirs()))
                return false;

            for (File childFile : sourceFile.listFiles()) {
                if (!copy(childFile, new File(destFile, childFile.getName())))
                    return false;
            }

            return true;
        }

        File destDir = destFile.getParentFile();
        if (!(destDir.isDirectory() || destDir.mkdirs()))
            return false;

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
                for (File childNode : rootNode.listFiles(filter)) {
                    walkTree(childNode, filter, treeWalker);
                }
                treeWalker.afterExitingDirectory(rootNode);
            }
        } else {
            treeWalker.onLeaf(rootNode);
        }
    }

    public static void walkTree(String rootPath, FileFilter filter, TreeWalker treeWalker) {
        walkTree(new File(rootPath), filter, treeWalker);
    }

    public static FileInputStream open(String path) throws FileNotFoundException {
        return new FileInputStream(path);
    }

    public static FileOutputStream create(String path) throws FileNotFoundException {
        return new FileOutputStream(path, false);
    }

    public static FileOutputStream append(String path) throws FileNotFoundException {
        return new FileOutputStream(path, true);
    }

    public static BufferedReader openText(String path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path));
    }

    public static Scanner openScanner(String path) throws FileNotFoundException {
        return new Scanner(openText(path));
    }

    public static PrintWriter createText(String path) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(path, false)), true);
    }

    public static PrintWriter appendText(String path) throws IOException {
        return new PrintWriter(new BufferedWriter(new FileWriter(path, true)), true);
    }

    public static byte[] readAllBytes(InputStream input) throws IOException {
        try (var output = new ByteArrayOutputStream()) {
            copyStream(input, output);
            return output.toByteArray();
        }
    }

    public static byte[] readAllBytes(File file) throws IOException {
        try (var input = new FileInputStream(file)) {
            return readAllBytes(input);
        }
    }

    public static byte[] readAllBytes(String path) throws IOException {
        try (var input = new FileInputStream(path)) {
            return readAllBytes(input);
        }
    }

    public static String readAllText(InputStream input) throws IOException {
        var sb = new StringBuilder();
        String newLine = String.format("%n");
        String line;

        try (var reader = new BufferedReader(new InputStreamReader(input))) {
            while ((line = reader.readLine()) != null) {
                if (!sb.isEmpty()) sb.append(newLine);
                sb.append(line);
            }
        }

        return sb.toString();
    }

    public static String readAllText(File file) throws IOException {
        try (var input = new FileInputStream(file)) {
            return readAllText(input);
        }
    }

    public static String readAllText(String path) throws IOException {
        try (var input = new FileInputStream(path)) {
            return readAllText(input);
        }
    }

    public static String[] readAllLines(InputStream input) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;

        try (var reader = new BufferedReader(new InputStreamReader(input))) {
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines.toArray(String[]::new);
    }

    public static String[] readAllLines(File file) throws IOException {
        try (var input = new FileInputStream(file)) {
            return readAllLines(input);
        }
    }

    public static String[] readAllLines(String path) throws IOException {
        try (var input = new FileInputStream(path)) {
            return readAllLines(input);
        }
    }

    public static void forEachLine(InputStream input, Consumer<String> consumer)
            throws IOException {

        String line;
        try (var reader = new BufferedReader(new InputStreamReader(input))) {
            while ((line = reader.readLine()) != null) {
                consumer.accept(line);
            }
        }
    }

    public static void forEachLine(File file, Consumer<String> consumer) throws IOException {
        try (var input = new FileInputStream(file)) {
            forEachLine(input, consumer);
        }
    }

    public static void forEachLine(String path, Consumer<String> consumer) throws IOException {
        try (var input = new FileInputStream(path)) {
            forEachLine(input, consumer);
        }
    }

    public static void writeAllBytes(File file, byte[] bytes, boolean append) throws IOException {
        try (var output = new FileOutputStream(file, append)) {
            output.write(bytes);
        }
    }

    public static void writeAllBytes(String path, byte[] bytes, boolean append) throws IOException {
        try (var output = new FileOutputStream(path, append)) {
            output.write(bytes);
            output.flush();
        }
    }

    public static void writeAllText(OutputStream output, String text) throws IOException {
        try (var writer = new BufferedWriter(new OutputStreamWriter(output))) {
            writer.write(text);
            writer.flush();
        }
    }

    public static void writeAllText(File file, String text, boolean append) throws IOException {
        try (var output = new FileOutputStream(file, append)) {
            writeAllText(output, text);
        }
    }

    public static void writeAllText(String path, String text, boolean append) throws IOException {
        try (var output = new FileOutputStream(path, append)) {
            writeAllText(output, text);
        }
    }

    public static void writeAllLines(OutputStream output, String[] lines) throws IOException {
        try (var writer = new BufferedWriter(new OutputStreamWriter(output))) {
            for (int i = 0; i < lines.length; ++i) {
                if (i > 0) writer.newLine();
                writer.write(lines[i]);
            }
            writer.flush();
        }
    }

    public static void writeAllLines(File file, String[] lines, boolean append) throws IOException {
        try (var output = new FileOutputStream(file, append)) {
            writeAllLines(output, lines);
        }
    }

    public static void writeAllLines(String path, String[] lines, boolean append) throws IOException {
        try (var output = new FileOutputStream(path, append)) {
            writeAllLines(output, lines);
        }
    }

    private static String normalizeExtension(String extension) {
        String normalized = extension.trim().toLowerCase(Locale.ROOT);

        while (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }

        return normalized;
    }

    private static String normalizeContentType(String contentType) {
        return contentType.trim().toLowerCase(Locale.ROOT);
    }

    //==================== INNER CLASSES AND INTERFACES ====================

    @FunctionalInterface
    public interface TreeWalker {
        void onLeaf(File node);

        default boolean beforeEnteringDirectory(File node) {
            return true;
        }

        default void afterExitingDirectory(File node) {}
    }

    public static class PatternFilter implements FileFilter {
        private final Pattern pattern;

        public PatternFilter(Pattern pattern) {
            this.pattern = pattern;
        }

        public PatternFilter(String pattern) {
            this(Pattern.compile(pattern));
        }

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || pattern.matcher(file.getPath()).matches();
        }

        public final Pattern getPattern() {
            return pattern;
        }
    }

    public static class ExtensionFilter implements FileFilter {
        private final String[] extensions;

        public ExtensionFilter(String[] extensions) {
            this.extensions = extensions;
        }

        public ExtensionFilter(String extensions) {
            this(extensions.split("\\s*,\\s*"));
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

        public final String[] getExtensions() {
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
            this(contentTypes.split("\\s*,\\s*"));
        }

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) return true;

            String contentType = getContentType(file.getName());
            for (Pattern pattern : patterns) {
                if (pattern.matcher(contentType).matches())
                    return true;
            }

            return false;
        }

        public final String[] getContentTypes() {
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
