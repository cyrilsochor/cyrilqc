package com.cyrilqc.core.exception;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;
import org.junit.ComparisonFailure;

public class BuildComparisonException extends BuildException {

	private final String expected;
	private final String actual;

	private static final long serialVersionUID = 3755856850534461383L;

	public BuildComparisonException(String message, String expected, String actual, Location location) {
		super(message, location);
		this.expected = expected;
		this.actual = actual;
	}

	public BuildComparisonException(ComparisonFailure e, Location location) {
		super(e.getMessage(), location);
		this.expected = e.getExpected();
		this.actual = e.getActual();
	}

	public String getExpected() {
		return expected;
	}

	public String getActual() {
		return actual;
	}

}
