package com.github.blutorange.translune.media;

import java.io.IOException;
import java.util.List;

import com.github.blutorange.translune.db.Resource;

public interface IImageProcessing {
	default IAtlasImage packResources(final String imageName, final List<Resource> resourceList) throws IOException {
		return packResources(imageName, resourceList, 512, 512);
	}
	default IAtlasImage packResources(final String imageName, final String[] resourceList) throws IOException {
		return packResources(imageName, resourceList, 512, 512);
	}
	public IAtlasImage packResources(String imageName, List<Resource> resourceList, int width, int height) throws IOException;
	public IAtlasImage packResources(String imageName, String[] resourceList, int width, int height) throws IOException;
}