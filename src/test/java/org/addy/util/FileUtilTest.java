package org.addy.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

class FileUtilTest {

	static final File file1 = new File("C:\\Users\\mbem_\\Downloads\\20200902_142831.jpg");
	static final File file2 = new File("C:\\Users\\mbem_\\Downloads\\ManningLogo.png");
	static final File file3 = new File("C:\\Users\\mbem_\\Downloads");
	static final File file4 = new File("C:\\Users\\mbem_\\Downloads\\w_java58.pdf");
	
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
		FileUtil.PatternFilter filter = new FileUtil.PatternFilter(".*ManningLogo.*");
		assertFalse(filter.accept(file1));
		assertTrue(filter.accept(file2));
		assertFalse(filter.accept(file3));
		assertFalse(filter.accept(file4));
	}
	
	@Test
	void contentTypeFilterAcceptsWildcard() throws IOException {
		FileUtil.ContentTypeFilter filter = new FileUtil.ContentTypeFilter("image/*");
		assertTrue(filter.accept(file1));
		assertTrue(filter.accept(file2));
		assertTrue(filter.accept(file3));
		assertFalse(filter.accept(file4));
	}
	
	@Test
	void contentTypeFilterAcceptsPrecise() throws IOException {
		FileUtil.ContentTypeFilter filter = new FileUtil.ContentTypeFilter("image/jpeg,application/pdf");
		assertTrue(filter.accept(file1));
		assertFalse(filter.accept(file2));
		assertTrue(filter.accept(file3));
		assertTrue(filter.accept(file4));
	}

}
