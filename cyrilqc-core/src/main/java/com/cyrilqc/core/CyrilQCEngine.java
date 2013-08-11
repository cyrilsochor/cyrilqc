package com.cyrilqc.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

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

	public URL getProjectURL() {
		final File projectFile = new File(configuration.getProjectFile());
		if (!projectFile.exists()) {
			throw new CyrilQCRuntimeException("Project file " + projectFile.getAbsolutePath() + " not found");
		}

		try {
			return projectFile.toURI().toURL();
		} catch (final MalformedURLException e) {
			throw new CyrilQCRuntimeException("Invalid project file " + projectFile.getAbsolutePath(), e);
		}
	}

	public CyrilQCProject getProject() throws Exception {
		if (project == null) {
			synchronized (this) {
				if (project == null) {
					final URL projectURL = getProjectURL();
					project = new CyrilQCProject(this, projectURL);
					project.init();
				}
			}
		}
		return project;
	}

}
