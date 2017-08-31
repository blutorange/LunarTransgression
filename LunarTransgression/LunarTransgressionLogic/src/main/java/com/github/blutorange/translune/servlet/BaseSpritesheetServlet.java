package com.github.blutorange.translune.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.github.blutorange.translune.ic.ComponentFactory;
import com.github.blutorange.translune.media.IAtlasImage;
import com.github.blutorange.translune.media.IImageProcessing;
import com.github.blutorange.translune.util.Constants;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * translune/spritesheet.png?gzip&resources=1.png,2.png
 * translune/spritesheet.json?gzip&resources=1.png,2.png
 *
 * @author madgaksha
 *
 */
@SuppressWarnings("serial")
public class BaseSpritesheetServlet extends HttpServlet {

	@Inject
	IImageProcessing imageProcessing;

	private Cache<String, IAtlasImage> cache;

	@Override
	public void init() {
		ComponentFactory.getLunarComponent().inject(this);
		cache = CacheBuilder.newBuilder().maximumSize(1000).expireAfterAccess(10, TimeUnit.MINUTES)
				.<String, IAtlasImage> build();
	}

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		// Read request parameters and decode if gzipped+base64.
		final String path = req.getPathInfo();
		final String extension = FilenameUtils.getExtension(path);
		final boolean jsonRequest = extension.equals("json");
		final boolean gzip = req.getParameter(Constants.SERVLET_KEY_GZIP) != null;
		String resources = req.getParameter(Constants.SERVLET_KEY_RESOURCES);
		if (gzip) {
			final byte[] bytes = Base64.getDecoder().decode(resources);
			try (InputStream is = new ByteArrayInputStream(bytes)) {
				resources = IOUtils.toString(new GZIPInputStream(is), StandardCharsets.UTF_8);
			}
		}
		final String[] resourceList = resources.split(",");
		// Get unique ID for spritesheet, sort images by name.
		Arrays.sort(resourceList);
		final String spritesheetId = String.join(",", resourceList);
		final String imageName;
		if (gzip) {
			final byte[] bytes = spritesheetId.getBytes(StandardCharsets.UTF_8);
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
					GZIPOutputStream gos = new GZIPOutputStream(baos)) {
				gos.write(bytes);
				imageName = new String(Base64.getEncoder().encode(baos.toByteArray()), StandardCharsets.UTF_8);
			}
		}
		else {
			imageName = "spritesheet.png?resources=" + URLEncoder.encode(spritesheetId, "UTF-8");
		}
		// Generate spritesheet, checking the cache first.
		final IAtlasImage image;
		try {
			image = cache.get(spritesheetId, () -> imageProcessing.packResources(imageName, resourceList));
		}
		catch (final ExecutionException e) {
			throw new IOException("failed to get spritesheet: " + spritesheetId, e);
		}
		// Return output as the response.
		try {
			resp.setHeader(Constants.HEADER_ACCESS_CONTROL, Constants.HEADER_ACCESS_CONTROL_ALL);
			resp.setStatus(200);
			if (jsonRequest) {
				final byte[] bytes = image.getAtlasJson().getBytes(StandardCharsets.UTF_8);
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("application/json");
				resp.setContentLength(bytes.length);
				resp.setHeader("content-disposition", "attachment; filename=\"spritesheet.json\"");
				resp.getOutputStream().write(bytes);
			}
			else {
				resp.setCharacterEncoding("US-ASCII");
				resp.setContentType(image.getImageMime());
				resp.setContentLength(image.getImageBytes().length);
				resp.setHeader("content-disposition", "attachment; filename=\"spritesheet.png\"");
				resp.getOutputStream().write(image.getImageBytes());
			}
		}
		catch (final Exception e) {
				throw new ServletException("failed to write output", e);
		}
	}
}