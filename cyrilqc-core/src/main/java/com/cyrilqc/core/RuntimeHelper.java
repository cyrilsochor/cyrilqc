package com.cyrilqc.core;

public class RuntimeHelper {

	private final CyrilQCProject project;
	private RuntimeMode mode;
	private String targetName;
	private String testName;

	public RuntimeHelper(CyrilQCProject project) {
		super();
		this.project = project;
	}

	public RuntimeMode getMode() {
		return mode;
	}

	public void setMode(RuntimeMode mode) {
		this.mode = mode;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public void addTest(String name) {
		project.addTest(targetName, true, name);
	}

	public void fireBeforeTest() {
		project.fireBeforeTest();
	}

	public void fireAfterTest() {
		project.fireAfterTest();
	}

}
