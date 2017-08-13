package com.github.blutorange.translune;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.ext.Provider;

@Provider
public class ResponseFilter implements ContainerResponseFilter {
	@Override
	public void filter(final ContainerRequestContext req, final ContainerResponseContext resp) throws IOException {
		resp.getHeaders().add(Constants.HEADER_ACCESS_CONTROL, Constants.HEADER_ACCESS_CONTROL_ALL);
		final CacheControl cacheControl = new CacheControl();
		cacheControl.setNoCache(false);
		resp.getHeaders().add(Constants.HEADER_CACHE_CONTROL, cacheControl);
	}
}