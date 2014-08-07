package com.cyrilqc.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.cyrilqc.core.exception.CyrilQCRuntimeException;

public class CyrilQCEngine {

	private static CyrilQCEngine instance;

	private final Configuration configuration;
	private CyrilQCProject project;

	private CyrilQCEngine() throws UnsupportedEncodingException, IOException {
		configuration = new DefaultConfiguration();
	}

	public static synchronized CyrilQCEngine getInstance() throws UnsupportedEncodingException, IOException {
		if (instance == null) {
			instance = new CyrilQCEngine();
		}
		return instance;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public File getProjectFile() {
		final File projectFile = new File(configuration.getProjectFile());
		if (!projectFile.exists()) {
			throw new CyrilQCRuntimeException("Project file " + projectFile.getAbsolutePath() + " not found");
		}

		return projectFile;
	}

	public CyrilQCProject getProject() throws Exception {
		if (project == null) {
			synchronized (this) {
				if (project == null) {
					final File projectFile = getProjectFile();
					project = new CyrilQCProject(this, projectFile);
					project.init();
				}
			}
		}
		return project;
	}

}
