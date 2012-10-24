package com.cyrilqc.core;

public class CyrilQCTest {

	private final CyrilQCProject project;
	private final String targetName;
	private final String name;
	private final boolean multi;

	public CyrilQCTest(CyrilQCProject project, String targetName, boolean multi, String name) {
		super();
		this.project = project;
		this.name = name;
		this.targetName = targetName;
		this.multi = multi;
	}

	public CyrilQCProject getProject() {
		return project;
	}

	public String getName() {
		return name;
	}

	public String getTargetName() {
		return targetName;
	}

	public void run() throws Throwable {
		project.run(this);
	}

	public boolean isMulti() {
		return multi;
	}

}
