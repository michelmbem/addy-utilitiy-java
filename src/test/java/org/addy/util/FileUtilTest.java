package org.addy.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileUtilTest {
	static final File file1 = new File("files/image-from-pexels.jpg");
	static final File file2 = new File("files/image-from-internet.png");
	static final File file3 = new File("files");
	static final File file4 = new File("files/dummy-document.pdf");

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
