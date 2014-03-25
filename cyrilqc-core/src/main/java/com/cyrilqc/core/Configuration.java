package com.cyrilqc.core;

import java.io.PrintStream;

import org.apache.tools.ant.BuildLogger;

public interface Configuration {

	String getProjectFile();

	String getTestTargetPrefix();

	String getMultiTestTargetPrefix();

	String getProjectNamePrefix();

	BuildLogger getLoggingLogger() throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	int getLoggingLevelDefault();

	int getLoggingLevelTest();

	int getLoggingLevelInfrastructure();

	int getLoggingLevelBanner();

	char getBannerCharacter();

	int getBannerLength();

	String getBeforeModuleTargetPrefix();

	String getAfterModuleTargetPrefix();

	String getBeforeTestTargetPrefix();

	String getAfterTestTargetPrefix();

	PrintStream getOutputPrintStream();

	PrintStream getErrorPrintStream();

}
