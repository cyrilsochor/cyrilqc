package com.cyrilqc.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tools.ant.Project;

import com.cyrilqc.core.exception.CyrilQCRuntimeException;

public class DefaultConfiguration implements Configuration {
	private static final String PROJECT_FILENAME_DEFAULT = "cyrilqc.xml";
	private static final String PROJECT_FILE_SYSTEM_VARIABLE = "cyrilqc.project.file";
	private static final String TEST_TARGET_PREFIX = "test";
	private static final String MULTI_TEST_TARGET_PREFIX = "multitest";
	private static final String PROJECT_NAME_PREFIX = "CyrilQC";

	public URL getProjectURL() {
		String projectPath = System.getProperty(PROJECT_FILE_SYSTEM_VARIABLE);
		if (projectPath == null) {
			projectPath = PROJECT_FILENAME_DEFAULT;
		}

		final File projectFile = new File(projectPath);
		if (!projectFile.exists()) {
			throw new CyrilQCRuntimeException("Project file " + projectFile.getAbsolutePath() + " not found");
		}

		try {
			return projectFile.toURI().toURL();
		} catch (final MalformedURLException e) {
			throw new CyrilQCRuntimeException("Invalid project file " + projectFile.getAbsolutePath(), e);
		}
	}

	public String getTestTargetPrefix() {
		return TEST_TARGET_PREFIX;
	}

	public String getMultiTestTargetPrefix() {
		return MULTI_TEST_TARGET_PREFIX;
	}

	public String getProjectNamePrefix() {
		return PROJECT_NAME_PREFIX;
	}

	public int getLoggingLevelDefault() {
		return Project.MSG_INFO;
	}

	public int getLoggingLevelTest() {
		return Project.MSG_INFO;
	}

	public int getLoggingLevelInfrastructure() {
		return Project.MSG_WARN;
	}

	public int getLoggingLevelLogo() {
		return Project.MSG_INFO;
	}

	public char getLogoCharacter() {
		return ':';
	}

	public int getLogoLength() {
		return 80;
	}

}
