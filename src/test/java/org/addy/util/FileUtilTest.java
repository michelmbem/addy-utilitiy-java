package org.addy.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest {
	private static final File file1 = new File("files/image-from-pexels.jpg");
	private static final File file2 = new File("files/image-from-internet.png");
	private static final File file3 = new File("files");
	private static final File file4 = new File("files/dummy-document.pdf");

	@Test
	void checkingFilesWorks() {
		assertTrue(FileUtil.exists(file1.getPath()));
		assertTrue(FileUtil.exists(file2.getPath()));
		assertTrue(FileUtil.isDirectory(file3.getPath()));
		assertTrue(FileUtil.isFile(file4.getPath()));
		assertFalse(FileUtil.isHidden(file4.getPath()));
	}

	@Test
	void pathPartExtractionWorks() {
		assertEquals(file3.getPath(), FileUtil.getDirectory(file1.getPath()));
		assertEquals("image-from-pexels.jpg", FileUtil.getFileName(file1.getPath()));
		assertEquals(".jpg", FileUtil.getExtension(file1.getPath()));
		assertEquals("files" + File.separator + "image-from-pexels.png", FileUtil.changeExtension(file1.getPath(), ".png"));
		assertTrue(FileUtil.getExtension(file3.getPath()).isEmpty());
		assertEquals("files.gif", FileUtil.changeExtension(file3.getPath(), ".gif"));
	}

	@Test
	void getContentTypeWorks() throws IOException {
		assertEquals("image/jpeg", FileUtil.getContentType(file1));
		assertEquals("image/png", FileUtil.getContentType(file2));
		assertEquals("application/pdf", FileUtil.getContentType(file4));
		assertNull(FileUtil.getContentType(file3.getPath()));
	}

	@Test
	void combineWorks() {
		assertEquals("files" + File.separator + "toto.docx", FileUtil.combine("files", "toto.docx"));
		assertEquals("files" + File.separator + "scripts" + File.separator + "init.sh",
				FileUtil.combine("files", "scripts", "init.sh"));
	}

	@Test
	void gettingSystemDirsWorks() {
		assertTrue(FileUtil.isDirectory(FileUtil.getUserDir()));
		assertTrue(FileUtil.isDirectory(FileUtil.getCurrentDir()));
		assertTrue(FileUtil.isFile(FileUtil.combine(FileUtil.getCurrentDir(), file1.getPath())));
		assertTrue(FileUtil.isDirectory(FileUtil.getTempDir()));
	}

	@Test
	void movingFilesWorks() {
		String source = file1.getPath();
		String dest = FileUtil.combine(FileUtil.getDirectory(source), "target.jpg");
		assertFalse(FileUtil.exists(dest));

		FileUtil.move(source, dest);
		assertTrue(FileUtil.isFile(dest));
		assertFalse(FileUtil.exists(source));

		FileUtil.move(dest, source);
		assertFalse(FileUtil.isFile(dest));
		assertTrue(FileUtil.exists(source));
	}

	@Test
	void movingDirectoriesWorks() {
		String source = file3.getPath();
		String dest = "test-data";
		assertFalse(FileUtil.exists(dest));

		FileUtil.move(source, dest);
		assertTrue(FileUtil.isDirectory(dest));
		assertTrue(FileUtil.isFile(FileUtil.combine(dest, FileUtil.getFileName(file2.getPath()))));
		assertFalse(FileUtil.exists(source));

		FileUtil.move(dest, source);
		assertFalse(FileUtil.isDirectory(dest));
		assertTrue(FileUtil.exists(source));
		assertTrue(FileUtil.isFile(file2.getPath()));
	}

	@Test
	void copyingAndDeletingFilesWorks() throws IOException {
		String source = file4.getPath();
		String dest = FileUtil.combine(FileUtil.getDirectory(source), "copy.pdf");
		assertFalse(FileUtil.exists(dest));

		FileUtil.copy(source, dest);
		assertTrue(FileUtil.isFile(dest));
		assertTrue(FileUtil.exists(source));

		FileUtil.delete(dest);
		assertFalse(FileUtil.isFile(dest));
		assertTrue(FileUtil.exists(source));
	}

	@Test
	void copyingAndDeletingDirectoriesWorks() throws IOException {
		String source = file3.getPath();
		String dest = "file-copies";
		assertFalse(FileUtil.exists(dest));

		FileUtil.copy(source, dest);
		assertTrue(FileUtil.isDirectory(dest));
		assertTrue(FileUtil.isFile(FileUtil.combine(dest, FileUtil.getFileName(file2.getPath()))));
		assertTrue(FileUtil.exists(source));
		assertTrue(FileUtil.exists(file2.getPath()));

		FileUtil.delete(dest);
		assertFalse(FileUtil.isDirectory(dest));
		assertFalse(FileUtil.isFile(FileUtil.combine(dest, FileUtil.getFileName(file2.getPath()))));
		assertTrue(FileUtil.exists(source));
	}

	@Test
	void walkTreeWorks() {
		List<String> fileList = new LinkedList<>();
		FileUtil.walkTree(file3, file -> true, node -> fileList.add(node.getPath()));
		assertEquals(10, fileList.size());
		assertTrue(fileList.contains(file1.getPath()));
		assertTrue(fileList.contains(file4.getPath()));
	}

	@Test
	void deleteDotGitAndDotVSDirs() {
		List<String> fileList = new LinkedList<>();
		var codeDir = new File("C:\\Users\\mbem_\\source\\local");
		var cleaner = new FileUtil.TreeWalker() {

			@Override
			public boolean beforeEnteringDirectory(File node) {
				if (node.isDirectory() &&
						(node.getName().equals(".vs") || node.getName().equals(".git"))) {
					FileUtil.delete(node);
					fileList.add(node.getPath());
					return false;
				}

				return true;
			}

			@Override
			public void onLeaf(File node) {}
		};

		FileUtil.walkTree(codeDir, file -> true, cleaner);
		assertFalse(fileList.isEmpty());
	}

	@Test
	void extensionFilterAccepts() {
		FileUtil.ExtensionFilter filter = new FileUtil.ExtensionFilter(".gif, .jpeg , .jpg,.bmp");
		assertTrue(filter.accept(file1));
		assertFalse(filter.accept(file2));
		assertTrue(filter.accept(file3));
		assertFalse(filter.accept(file4));
	}

	@Test
	void patternFilterAccepts() {
		FileUtil.PatternFilter filter = new FileUtil.PatternFilter(".*from-internet.*");
		assertFalse(filter.accept(file1));
		assertTrue(filter.accept(file2));
		assertFalse(filter.accept(file3));
		assertFalse(filter.accept(file4));
	}

	@Test
	void contentTypeFilterAcceptsWildcard() {
		FileUtil.ContentTypeFilter filter = new FileUtil.ContentTypeFilter("image/*");
		assertTrue(filter.accept(file1));
		assertTrue(filter.accept(file2));
		assertTrue(filter.accept(file3));
		assertFalse(filter.accept(file4));
	}

	@Test
	void contentTypeFilterAcceptsPrecise() {
		FileUtil.ContentTypeFilter filter = new FileUtil.ContentTypeFilter("image/jpeg,application/pdf");
		assertTrue(filter.accept(file1));
		assertFalse(filter.accept(file2));
		assertTrue(filter.accept(file3));
		assertTrue(filter.accept(file4));
	}
}
