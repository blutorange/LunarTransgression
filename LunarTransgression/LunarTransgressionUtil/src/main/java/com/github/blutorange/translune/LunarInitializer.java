package com.github.blutorange.translune;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsoniter.JsonIterator;
import com.jsoniter.spi.DecodingMode;

@SuppressWarnings("nls")
public class LunarInitializer implements ServletContainerInitializer {
	private final static Logger LOG = LoggerFactory.getLogger(LunarInitializer.class);
//	public static EntityManagerFactory ENTITY_MANAGER_FACTORY;
//	public static Properties CUSTOM_PROPERTIES = new Properties();
	@Override
	public void onStartup(@Nullable final Set<Class<?>> classes, final @Nullable ServletContext servletContext) throws ServletException {
		try {
			LOG.info("initializing application server");

			LOG.info("setting json iterator mode to static");
			JsonIterator.setMode(DecodingMode.STATIC_MODE);

//			LOG.info("reading custom properties");
//			try (InputStream is = LunarInitializer.class.getClassLoader().getResourceAsStream("custom.properties")) {
//				CUSTOM_PROPERTIES.load(is);
//			}

//			LOG.info("creating entity manager factory");
//			ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("hibernate");
//			LOG.info("entity manager factory is " + ENTITY_MANAGER_FACTORY.getClass().getCanonicalName());
		}
		catch (final Exception e) {
			LOG.error("failed to process lunar initializer", e);
			throw new ServletException("failed to process lunar initializer", e);
		}
	}
}