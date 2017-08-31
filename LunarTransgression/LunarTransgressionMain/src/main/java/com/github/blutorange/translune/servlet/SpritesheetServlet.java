package com.github.blutorange.translune.servlet;

import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(asyncSupported = true, urlPatterns = "/spritesheet/*", displayName = "Spritesheet provide servlet", name = "SpritesheetServlet", description = "Provides dynamically generated spritesheets")
public class SpritesheetServlet extends BaseSpritesheetServlet {
	// Logic implemented by base servlet.
}