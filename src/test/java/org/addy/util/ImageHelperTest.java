package org.addy.util;

import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageHelperTest {
	@BeforeAll
	static void setup() {
		String[] names = {"jordan", "daniel"};
		String[] modifiers = {"corrected", "gs", "flipped"};
		File file;

		for (String name : names) {
			for (String modifier : modifiers) {
				file = new File(String.format("files/%s-/%s.jpg", name, modifier));
				if (file.exists()) file.delete();
			}
		}

		file = new File("files/tiles.jpg");
		if (file.exists()) file.delete();
	}

	@Test
	@Order(1)
	void canDetectAndCorrectJpegOrientation() {
		File[] files = new File[] {
			new File("files/jordan.jpg"),
			new File("files/daniel.jpg")};
		
		for (File srcFile : files) {
			String path = srcFile.getPath();
			File destFile = new File(FileUtil.changeExtension(path, "-corrected" + FileUtil.getExtension(path)));
			ImageHelper.correctOrientation(srcFile, destFile);
			assertTrue(destFile.isFile());
		}
	}
	
	@Test
	@Order(2)
	void canGrayScale() {
		try {
			File srcFile = new File("files/jordan-corrected.jpg");
			File destFile = new File("files/jordan-gs.jpg");
			Image srcImage = ImageIO.read(srcFile);
			Image destImage = ImageHelper.grayScale(srcImage);
			ImageIO.write((RenderedImage) destImage, "jpg", destFile);
			assertTrue(destFile.isFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Order(3)
	void canFlip() {
		try {
			File srcFile = new File("files/daniel-corrected.jpg");
			File destFile = new File("files/daniel-flipped.jpg");
			Image srcImage = ImageIO.read(srcFile);
			Image destImage = ImageHelper.flip(srcImage, ImageHelper.AXIS_Y);
			ImageIO.write((RenderedImage) destImage, "jpg", destFile);
			assertTrue(destFile.isFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Order(4)
	void canCreateImageTiles() {
		File[] files = new File[] {
			new File("files/image-from-pexels.jpg"),
			new File("files/jordan-corrected.jpg"),
			new File("files/daniel-corrected.jpg")};
		
		File outFile = new File("files/tiles.jpg");
		
		try {
			Image tiles = ImageHelper.makeTiles(files, 256, 256, Color.LIGHT_GRAY);
			ImageIO.write((RenderedImage) tiles, "jpg", outFile);
			assertTrue(outFile.exists());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
