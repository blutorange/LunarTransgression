package com.github.blutorange.translune.media;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageReader;

import com.github.blutorange.translune.db.Resource;

public interface IImageProcessing {
	default IAtlasImage packResources(final String imageName, final List<Resource> resourceList) throws IOException {
		return packResources(imageName, resourceList, 512, 512);
	}
	default IAtlasImage packResources(final String imageName, final String[] resourceList) throws IOException {
		return packResources(imageName, resourceList, 512, 512);
	}
	IAtlasImage packResources(String imageName, List<Resource> resourceList, int width, int height) throws IOException;
	IAtlasImage packResources(String imageName, String[] resourceList, int width, int height) throws IOException;
	BufferedImage generateRandomAvatar(String seed);
	BufferedImage generateDisabledAvatar();
	ImageReader getImageReader(String mime) throws IOException;
	BufferedImage openFromByteArray(String mime, byte[] data) throws IOException;
	BufferedImage openFromResource(String mime, String resourcePath) throws IOException;
	BufferedImage openFromStream(String mime, InputStream stream) throws IOException;
	BufferedImage openFromFile(String mime, String filePath) throws IOException;
	BufferedImage generateRandomAvatar();
	void writeToStream(RenderedImage image, String type, OutputStream stream) throws IOException;
	byte[] writeToByteArray(RenderedImage image, String type) throws IOException;
	void writeToFile(RenderedImage image, String type, String filePath) throws IOException;
	void writeToFile(RenderedImage image, String type, File file) throws IOException;
}