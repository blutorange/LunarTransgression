package com.github.blutorange.translune.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.blutorange.translune.LunarServletContextListener;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.media.IAtlasImage;
import com.github.blutorange.translune.media.IImageProcessing;

public class PackSprite {
	public static void main(final String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Usage: java PackSprite outputFile input-files...");
			System.exit(1);
		}
		final LunarServletContextListener scl = new LunarServletContextListener();
		scl.initialize();
		final MimetypesFileTypeMap mimeMap = ComponentFactory.getLunarComponent().mimetypesFileTypeMap();
		try {
			final IImageProcessing imageProcessing = ComponentFactory.getLunarComponent().imageProcessing();
			final List<Resource> resourceList = new ArrayList<>(args.length-1);
			for (int i = 1; i< args.length; ++i) {
				final Resource resource = new Resource();
				final String filename = FilenameUtils.getName(args[i]);
				resource.setData(FileUtils.readFileToByteArray(new File(args[i])));
				resource.setFilename(filename);
				resource.setMime(mimeMap.getContentType(args[i]));
				resource.setName(filename);
				resourceList.add(resource);
			}
			final String basename = FilenameUtils.getBaseName(args[0]);
			final String pathBase = FilenameUtils.removeExtension(args[0]);
			final IAtlasImage img = imageProcessing.packResources(basename + ".png", resourceList, 512, 512);
			FileUtils.write(new File(pathBase + ".json"), img.getAtlasJson());
			FileUtils.writeByteArrayToFile(new File(pathBase + ".png"), img.getImageBytes());
		}
		finally {
			scl.destroy();
		}
		System.exit(0);
	}
}
