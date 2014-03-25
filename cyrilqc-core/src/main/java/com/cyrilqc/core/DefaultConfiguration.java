package com.cyrilqc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildLogger;

import com.cyrilqc.core.util.ConvertUtils;
import com.cyrilqc.core.util.StreamUtils;
import com.cyrilqc.core.util.StringUtils;

public class DefaultConfiguration implements Configuration {
	private static final String SYSTEM_PROPERTY_PREFIX = "cyrilqc.";
	private static final String SYSTEM_PROPERTY_COFIG = SYSTEM_PROPERTY_PREFIX + "config.file";

	private static final String DEFAULT_CONFIG_RESOURCE = "cyrilqc-config-default.properties";

	private final Properties defaultProperties;
	private Properties userProperties;

	public DefaultConfiguration() throws UnsupportedEncodingException, IOException {
		defaultProperties = StreamUtils.parseProperties(getClass().getResourceAsStream(DEFAULT_CONFIG_RESOURCE));

		final String userConfigLocation = System.getProperty(SYSTEM_PROPERTY_COFIG);
		if (userConfigLocation != null) {
			final String userConfigLocationTrimmed = userConfigLocation.trim();
			if (!"".equals(userConfigLocationTrimmed)) {
				final File userConfigFile = new File(userConfigLocationTrimmed);
				if (!userConfigFile.isFile()) {
					throw new IllegalArgumentException("Configuration file " + userConfigFile.getAbsolutePath() + " not found");
				}
				userProperties = StreamUtils.parseProperties(new FileInputStream(userConfigFile));
			}
		}
	}

	public String getProjectFile() {
		return getProperty("project.file");
	}

	public String getTestTargetPrefix() {
		return getProperty("test.target.prefix");
	}

	public String getMultiTestTargetPrefix() {
		return getProperty("multitest.target.prefix");
	}

	public String getBeforeModuleTargetPrefix() {
		return getProperty("before.module.target.prefix");
	}

	public String getAfterModuleTargetPrefix() {
		return getProperty("after.module.target.prefix");
	}

	public String getBeforeTestTargetPrefix() {
		return getProperty("before.test.target.prefix");
	}

	public String getAfterTestTargetPrefix() {
		return getProperty("after.test.target.prefix");
	}

	public String getProjectNamePrefix() {
		return getProperty("project.name.prefix");
	}

	public BuildLogger getLoggingLogger() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return (BuildLogger) getObjectPropery("logging.logger");
	}

	public int getLoggingLevelDefault() {
		return getIntegerPropery("logging.level.default");
	}

	public int getLoggingLevelTest() {
		return getIntegerPropery("logging.level.test");
	}

	public int getLoggingLevelInfrastructure() {
		return getIntegerPropery("logging.level.infrastructure");
	}

	public int getLoggingLevelBanner() {
		return getIntegerPropery("logging.level.banner");
	}

	public char getBannerCharacter() {
		return getCharProperty("banner.character");
	}

	public int getBannerLength() {
		return getIntegerPropery("banner.length");
	}

	public PrintStream getOutputPrintStream() {
		return getPrintStream("output.stream");
	}

	public PrintStream getErrorPrintStream() {
		return getPrintStream("error.stream");
	}

	public List<String> getTests() {
		return ConvertUtils.parseList(getProperty("tests"));
	}

	public String getTestsInclude() {
		return getTrimmedProperty("tests.include");
	}

	public String getTestsExclude() {
		return getTrimmedProperty("tests.exclude");
	}

	protected String getProperty(String key) {
		String ret = null;

		if (ret == null) {
			ret = System.getProperty(SYSTEM_PROPERTY_PREFIX + key);
		}

		if (ret == null && userProperties != null) {
			ret = userProperties.getProperty(key);
		}

		if (ret == null && defaultProperties != null) {
			ret = defaultProperties.getProperty(key);
		}

		if (ret == null) {
			throw new NullPointerException("Configuration property '" + key + "' not defined");
		}

		return ret;
	}

	protected char getCharProperty(String key) {
		return ConvertUtils.parseCharacter(getProperty(key));
	}

	protected Boolean getBooleanProperty(String key) {
		return ConvertUtils.parseBoolean(getProperty(key));
	}

	protected Integer getIntegerPropery(String key) {
		return ConvertUtils.parseInteger(getProperty(key));
	}

	protected Long getLongProperty(String key) {
		return ConvertUtils.parseLong(getProperty(key));
	}

	protected Double getDoubleProperty(String key) {
		return ConvertUtils.parseDouble(getProperty(key));
	}

	protected Float getFloatProperty(String key) {
		return ConvertUtils.parseFloat(getProperty(key));
	}

	protected BigDecimal getBigDecimalProperty(String key) {
		return ConvertUtils.parseBigDecimal(getProperty(key));
	}

	protected BigInteger getBigIntegerProperty(String key) {
		return ConvertUtils.parseBigInteger(getProperty(key));
	}

	protected PrintStream getPrintStream(String key) {
		return ConvertUtils.parsePrintStream(getProperty(key));
	}

	protected Object getObjectPropery(String key) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		final String className = getProperty(key);
		if (StringUtils.isEmpty(className)) {
			return null;
		} else {
			final Class<?> clazz = Class.forName(className);
			return clazz.newInstance();
		}
	}

	protected String getTrimmedProperty(String key) {
		final String value = getProperty(key);
		if (value == null) {
			return null;
		}

		final String trimmed = value.trim();
		if (trimmed.length() == 0) {
			return null;
		} else {
			return trimmed;
		}
	}

}
