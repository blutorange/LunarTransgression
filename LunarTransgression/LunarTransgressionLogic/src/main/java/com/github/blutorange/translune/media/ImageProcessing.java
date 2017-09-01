package com.github.blutorange.translune.media;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.hibernate.Session;
import org.slf4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.ic.Classed;

@Singleton
public class ImageProcessing implements IImageProcessing {
	@Inject
	ILunarDatabaseManager databaseManager;

	@Inject
	@Classed(ImageProcessing.class)
	Logger logger;

	@Inject
	public ImageProcessing() {
	}

	@Override
	public IAtlasImage packResources(final String imageName, final List<Resource> resourceList) throws IOException {
		final int width = 512;
		final int height = 512;
		final BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = output.getGraphics();
		final JSONObject frames = new JSONObject();
		int curX = 0;
		int curY = 0;
		int columnHeight = 0;
		for (final Resource resource : resourceList) {
			final Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType(resource.getMime());
			if (!it.hasNext()) {
				logger.error("could not find suitable image reader for " + resource.getName());
				throw new IOException("could not find suitable image reader for " + resource.getName());
			}
			@SuppressWarnings("null")
			final ImageReader ir = it.next();
			try (ByteArrayInputStream is = new ByteArrayInputStream(resource.getData());
					ImageInputStream iis = new MemoryCacheImageInputStream(is)) {
				ir.setInput(iis);
				final BufferedImage input = ir.read(0);
				final int newX = curX + input.getWidth();
				if (input.getWidth() > output.getWidth()) {
					logger.error("image is too large to fit on spritesheet for " + resource.getName());
					throw new IOException("image is too large to fit on spritesheet" + resource.getName());
				}
				if (newX >= output.getWidth()) {
					curX = 0;
					curY += columnHeight;
					columnHeight = 0;
				}
				if (input.getHeight() > columnHeight) {
					columnHeight = input.getHeight();
					if (curY + columnHeight >= output.getHeight()) {
						logger.error("output spritesheet full, cannot write more more");
						throw new IOException("output spritesheet full, cannot write more more");
					}
				}
				graphics.drawImage(input, curX, curY, null);
				frames.put(resource.getName(), createAtlasEntry(curX, curY, input.getWidth(), input.getHeight()));
				curX += input.getWidth();
			}
		}
		graphics.dispose();
		final int usedHeight = curY + columnHeight;
		final BufferedImage result = output.getSubimage(0, 0, output.getWidth(), usedHeight);

		final JSONObject atlas = new JSONObject();
		atlas.put("frames", frames);
		atlas.put("meta", createMeta(result, imageName));
		return new SpritesheetImpl(result, atlas, imageName);
	}

	private JSONObject createMeta(final BufferedImage result, final String imageName) {
		final JSONObject meta = new JSONObject();
		final JSONObject size = new JSONObject();
		meta.put("app", "Java/translune");
		meta.put("version", "1.0");
		meta.put("image", imageName);
		meta.put("size", size);
		meta.put("scale", Integer.valueOf(1));
		size.put("w", Integer.valueOf(result.getWidth()));
		size.put("h", Integer.valueOf(result.getHeight()));
		return meta;
	}

	@SuppressWarnings("boxing")
	private JSONObject createAtlasEntry(final int curX, final int curY, final int width, final int height) {
		final JSONObject entry = new JSONObject();
		final JSONObject frame = new JSONObject();
		final JSONObject spriteSourceSize = new JSONObject();
		final JSONObject sourceSize = new JSONObject();
		frame.put("x", curX);
		frame.put("y", curY);
		frame.put("w", width);
		frame.put("h", height);
		spriteSourceSize.put("x", curX);
		spriteSourceSize.put("y", curY);
		spriteSourceSize.put("w", width);
		spriteSourceSize.put("h", height);
		sourceSize.put("w", width);
		sourceSize.put("h", height);
		entry.put("frame", frame);
		entry.put("rotated", Boolean.FALSE);
		entry.put("trimmed", Boolean.FALSE);
		entry.put("spriteSourceSize", spriteSourceSize);
		entry.put("sourceSize", sourceSize);
		return entry;
	}

	@Override
	public IAtlasImage packResources(final String imageName, final String[] resources) throws IOException {
		final List<Resource> resourceList = databaseManager.withEm(false, em -> {
			final List<Resource> list = em.unwrap(Session.class).byMultipleIds(Resource.class).multiLoad(resources);
			if (list == null || list.size() != resources.length || list.contains(null)) {
				logger.error("failed to load resources");
				return null;
			}
			return list;
		});
		if (resourceList == null)
			throw new IOException("failed to load resources");
		return this.packResources(imageName, resourceList);
	}

	private class SpritesheetImpl implements IAtlasImage {
		private final String atlasJson;
		private final byte[] imageBytes;
		private final String imageName;

		public SpritesheetImpl(final BufferedImage result, final JSONObject atlas, final String imageName)
				throws IOException {
			try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(result, "png", baos);
				this.atlasJson = atlas.toJSONString();
				this.imageBytes = baos.toByteArray();
			}
			this.imageName = imageName;
		}

		@Override
		public byte[] getImageBytes() {
			return imageBytes;
		}

		@Override
		public String getImageMime() {
			return "image/png";
		}

		@Override
		public String getImageName() {
			return imageName;
		}

		@Override
		public String getAtlasJson() {
			return atlasJson;
		}
	}
}