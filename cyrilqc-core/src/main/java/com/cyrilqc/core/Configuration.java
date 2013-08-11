package com.cyrilqc.core;

public interface Configuration {

	String getProjectFile();

	String getTestTargetPrefix();

	String getMultiTestTargetPrefix();

	String getProjectNamePrefix();

	int getLoggingLevelDefault();

	int getLoggingLevelTest();

	int getLoggingLevelInfrastructure();

	int getLoggingLevelLogo();

	char getLogoCharacter();

	int getLogoLength();

	String getBeforeModuleTargetPrefix();

	String getAfterModuleTargetPrefix();

	String getBeforeTestTargetPrefix();

	String getAfterTestTargetPrefix();

}
