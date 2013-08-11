package com.cyrilqc.core.task;

import java.io.InputStream;

import org.apache.tools.ant.Project;

public abstract class StreamComparatorBase implements StreamComparator {

	private Project antProject;
	private InputStream expectedStream;
	private String expectedLocation;
	private InputStream actualStream;
	private String actualLocation;

	public Project getAntProject() {
		return antProject;
	}

	public void setAntProject(Project antProject) {
		this.antProject = antProject;
	}

	public void setExpectedStream(InputStream expectedStream, String expectedLocation) {
		this.expectedStream = expectedStream;
		this.expectedLocation = expectedLocation;
	}

	public void setActualStream(InputStream actualStream, String actualLocation) {
		this.actualStream = actualStream;
		this.actualLocation = actualLocation;
	}

	protected void log(String message) {
		antProject.log(message);
	}

	protected void log(String message, int msgLevel) {
		antProject.log(message, msgLevel);
	}

	public InputStream getExpectedStream() {
		return expectedStream;
	}

	public String getExpectedLocation() {
		return expectedLocation;
	}

	public InputStream getActualStream() {
		return actualStream;
	}

	public String getActualLocation() {
		return actualLocation;
	}

}
