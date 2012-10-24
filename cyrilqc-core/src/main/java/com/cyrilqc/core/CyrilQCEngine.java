package com.cyrilqc.core;

import java.net.URL;

public class CyrilQCEngine {

	private static CyrilQCEngine instance;

	private final Configuration configuration;
	private CyrilQCProject project;

	private CyrilQCEngine() {
		configuration = new DefaultConfiguration();
	}

	public static synchronized CyrilQCEngine getInstance() {
		if (instance == null) {
			instance = new CyrilQCEngine();
		}
		return instance;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public CyrilQCProject getProject() throws Exception {
		if (project == null) {
			synchronized (this) {
				if (project == null) {
					final URL projectURL = getConfiguration().getProjectURL();
					project = new CyrilQCProject(this, projectURL);
				}
			}
		}
		return project;
	}

}
