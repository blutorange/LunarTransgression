package com.github.blutorange.translune.media;

import java.io.IOException;
import java.util.List;

import com.github.blutorange.translune.db.Resource;

public interface IImageProcessing {
	public IAtlasImage packResources(String imageName, List<Resource> resourceList) throws IOException;
	public IAtlasImage packResources(String imageName, String[] resourceList) throws IOException;
}