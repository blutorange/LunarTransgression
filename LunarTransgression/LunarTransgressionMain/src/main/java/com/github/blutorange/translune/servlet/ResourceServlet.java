package com.github.blutorange.translune.servlet;

import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(asyncSupported = true, urlPatterns = "/resource/*", displayName = "Resource provide servlet", name = "ResourceServlet", description = "Provides dynamic resources")
public class ResourceServlet extends BaseResourceServlet {
}