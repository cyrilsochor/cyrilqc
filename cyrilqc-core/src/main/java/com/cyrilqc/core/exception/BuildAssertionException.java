package com.cyrilqc.core.exception;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

public class BuildAssertionException extends BuildException {

	private static final long serialVersionUID = -1600836546589117758L;

	public BuildAssertionException() {
		super();
	}

	public BuildAssertionException(String message, Location location) {
		super(message, location);
	}

	public BuildAssertionException(String msg, Throwable cause, Location location) {
		super(msg, cause, location);
	}

	public BuildAssertionException(Throwable cause, Location location) {
		super(cause, location);
	}

}
