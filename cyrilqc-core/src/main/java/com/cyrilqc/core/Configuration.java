package com.cyrilqc.core;

import java.io.PrintStream;

public interface Configuration {

	String getProjectFile();

	String getTestTargetPrefix();

	String getMultiTestTargetPrefix();

	String getProjectNamePrefix();

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
