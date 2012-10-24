package com.cyrilqc.core;

import java.net.URL;

public interface Configuration {

	URL getProjectURL();

	String getTestTargetPrefix();

	String getMultiTestTargetPrefix();

	String getProjectNamePrefix();

	int getLoggingLevelDefault();

	int getLoggingLevelTest();

	int getLoggingLevelInfrastructure();

	int getLoggingLevelLogo();

	char getLogoCharacter();

	int getLogoLength();

}
