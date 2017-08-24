package com.github.blutorange.translune.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.ic.ComponentFactory;

@SuppressWarnings("serial")
@WebServlet(asyncSupported = true, urlPatterns = "/resource", displayName = "Resource provide servlet", name = "ResourceServlet", description = "Provides dynamic resources")
public class ResourceServlet extends HttpServlet {
	Logger LOG = LoggerFactory.getLogger(ResourceServlet.class);
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final String resourceName = req.getParameter(Constants.SERVLET_PARAM_RESOURCE_NAME);
		if (resourceName == null || resourceName.isEmpty()) {
			resp.sendError(400, "No resource name given.");
			return;
		}
		final ILunarDatabaseManager ldm = ComponentFactory.getDatabaseComponent().iLunarDatabaseManager();
		final Resource resource = ldm.withEm(false, em -> {
			return em.find(Resource.class, resourceName);
		});
		if (resource == null) {
			resp.sendError(404, "Resource not found.");
			return;
		}
		String name = resource.getName();
		try {
			final String extension = MimeTypes.getDefaultMimeTypes().forName(resource.getMime()).getExtension();
			name += extension;
		}
		catch (final MimeTypeException e) {
			LOG.error("failed to determine extension from mime type", e);
		}
		resp.setCharacterEncoding("US-ASCII");
		resp.setContentType(resource.getMime());
		resp.setContentLength(resource.getData().length);
		resp.setHeader("content-disposition", "attachment; filename=\"" + StringEscapeUtils.escapeJava(name) +"\"");
		resp.setStatus(200);
		resp.getOutputStream().write(resource.getData());
	}
}
