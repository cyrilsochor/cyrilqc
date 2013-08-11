package com.cyrilqc.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

public class StreamUtils {

	private StreamUtils() {
	}

	public static Properties parseProperties(final InputStream inputStream) throws UnsupportedEncodingException, IOException {
		return parseProperties(inputStream, null);
	}

	public static Properties parseProperties(final InputStream inputStream, final Properties defaults)
			throws UnsupportedEncodingException, IOException {
		final Properties ret = new Properties(defaults);
		return loadProperties(ret, inputStream);
	}

	public static Properties loadProperties(final Properties properies, final InputStream inputStream)
			throws UnsupportedEncodingException,
			IOException {
		final Reader propertiesReader = new InputStreamReader(inputStream, "UTF-8");
		try {
			properies.load(propertiesReader);
			return properies;
		} finally {
			propertiesReader.close();
		}
	};

	/**
	 * Copy all properties to new Properties instance.
	 * 
	 * @param sourceProperies
	 *            properties to be copied
	 */
	public static void copyProperties(Properties sourceProperies, Properties targetProperties) {
		final Enumeration<?> propertyNames = sourceProperies.propertyNames();
		while (propertyNames.hasMoreElements()) {
			final String name = (String) propertyNames.nextElement();
			final String value = sourceProperies.getProperty(name);
			targetProperties.setProperty(name, value);
		}
	}

}
