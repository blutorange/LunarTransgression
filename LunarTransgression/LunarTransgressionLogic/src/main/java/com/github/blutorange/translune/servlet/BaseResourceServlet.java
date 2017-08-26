package com.github.blutorange.translune.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.persistence.Transient;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;

import com.github.blutorange.translune.db.ILunarDatabaseManager;
import com.github.blutorange.translune.db.Resource;
import com.github.blutorange.translune.ic.Classed;
import com.github.blutorange.translune.ic.ComponentFactory;

@SuppressWarnings("serial")
public abstract class BaseResourceServlet extends HttpServlet {
	@Transient
	@Inject @Classed(BaseResourceServlet.class)
	Logger logger;

	@Transient
	@Inject
	ILunarDatabaseManager databaseManager;

	@Override
	public void init() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		final String resourceName = req.getParameter(getKeyResource());
		if (resourceName == null || resourceName.isEmpty()) {
			resp.sendError(400, "No resource name given.");
			return;
		}
		final Resource resource = databaseManager.withEm(false, em -> {
			return em.find(Resource.class, resourceName);
		});
		if (resource == null) {
			resp.sendError(404, "Resource not found.");
			return;
		}
		resp.setCharacterEncoding("US-ASCII");
		resp.setContentType(resource.getMime());
		resp.setContentLength(resource.getData().length);
		resp.setHeader("content-disposition", "attachment; filename=\"" + StringEscapeUtils.escapeJava(resource.getFilename()) +"\"");
		resp.setStatus(200);
		resp.getOutputStream().write(resource.getData());
	}

	protected abstract String getKeyResource();
}