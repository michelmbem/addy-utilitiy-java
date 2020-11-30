package org.addy.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ImageHelperTest {
	
	@Test
	@Order(1)
	void canDetectAndCorrectJpegOrientation() {
		File[] files = new File[] {
			new File("C:\\Users\\mbem_\\Downloads\\20201106_155510.jpg"),
			new File("C:\\Users\\mbem_\\Downloads\\20201106_155514.jpg")
		};
		
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
			File srcFile = new File("C:\\\\Users\\\\mbem_\\\\Downloads\\\\20201106_155514-corrected.jpg");
			File destFile = new File("C:\\\\Users\\\\mbem_\\\\Downloads\\\\20201106_155514-gs.jpg");
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
			File srcFile = new File("C:\\\\Users\\\\mbem_\\\\Downloads\\\\20201106_155510-corrected.jpg");
			File destFile = new File("C:\\\\Users\\\\mbem_\\\\Downloads\\\\20201106_155510-flipped.jpg");
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
			new File("C:\\Users\\mbem_\\Downloads\\20201031_154019.jpg"),
			new File("C:\\Users\\mbem_\\Downloads\\20201106_155510-flipped.jpg"),
			new File("C:\\Users\\mbem_\\Downloads\\20201106_155514-gs.jpg")/*,
			new File("C:\\Users\\mbem_\\Downloads\\bernard.png")*/
		};
		
		File outFile = new File("C:\\Users\\mbem_\\Downloads\\tiles.jpg");
		
		try {
			Image tiles = ImageHelper.makeTiles(files, 256, 256, Color.LIGHT_GRAY);
			ImageIO.write((RenderedImage) tiles, "jpg", outFile);
			assertTrue(outFile.exists());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
