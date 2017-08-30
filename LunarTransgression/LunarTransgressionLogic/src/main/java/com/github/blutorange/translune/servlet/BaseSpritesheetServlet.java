package com.github.blutorange.translune.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.blutorange.translune.ic.ComponentFactory;

@SuppressWarnings("serial")
public class BaseSpritesheetServlet extends HttpServlet {
	@Override
	public void init() {
		ComponentFactory.getLunarComponent().inject(this);
	}

	public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
	}
}