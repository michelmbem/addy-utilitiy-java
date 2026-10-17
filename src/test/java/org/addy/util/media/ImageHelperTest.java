package org.addy.util.media;

import org.addy.util.FileUtil;
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
		String[] generated = {"tiles", "sprite"};
		File file;

		for (String name : names) {
			for (String modifier : modifiers) {
				file = new File(String.format("files/%s-/%s.jpg", name, modifier));
				if (file.exists()) file.delete();
			}
		}

		for (String basename : generated) {
			file = new File("files/" + basename + ".jpg");
			if (file.exists()) file.delete();
		}
	}

	@Test
	@Order(1)
	void canDetectAndCorrectJpegOrientation() {
		File[] files = new File[] {
				new File("files/jordan.jpg"),
				new File("files/daniel.jpg")};

		for (File srcFile : files) {
			String path = srcFile.getPath();
			File destFile = new File(FileUtil.changeExtension(
					path, "-corrected" + FileUtil.getExtension(path)));
			JpegImageHelper.correctOrientation(srcFile, destFile);
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
			ImageIO.write((RenderedImage) destImage, JpegImageHelper.FORMAT, destFile);
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
			Image destImage = ImageHelper.flip(srcImage, FlipAxis.Y);
			ImageIO.write((RenderedImage) destImage, JpegImageHelper.FORMAT, destFile);
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
				new File("files/daniel-corrected.jpg"),
				new File("files/image-from-internet.png")};
		
		File outFile = new File("files/tiles.jpg");
		
		try {
			Image tiles = ImageHelper.makeTiles(files, 256, 256, Color.LIGHT_GRAY);
			ImageIO.write((RenderedImage) tiles, JpegImageHelper.FORMAT, outFile);
			assertTrue(outFile.exists());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@Order(5)
	void canGetSprite() {
		File sheetFile = new File("files/tiles.jpg");
		File spriteFile = new File("files/sprite.jpg");

		try {
			Image sheet = ImageIO.read(sheetFile);
			Image sprite = ImageHelper.getSprite(sheet, 1, 0, 128, 128,
					new Insets(0, 0, 0, 0));
			ImageIO.write((RenderedImage) sprite, JpegImageHelper.FORMAT, spriteFile);
			assertTrue(spriteFile.exists());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
