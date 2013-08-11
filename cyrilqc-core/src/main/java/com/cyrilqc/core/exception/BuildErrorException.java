package com.cyrilqc.core.exception;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

public class BuildErrorException extends BuildException {

	private static final long serialVersionUID = -5563780732672897480L;

	public BuildErrorException() {
		super();
	}

	public BuildErrorException(String message, Location location) {
		super(message, location);
	}

	public BuildErrorException(String msg, Throwable cause, Location location) {
		super(msg, cause, location);
	}

	public BuildErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuildErrorException(String message) {
		super(message);
	}

	public BuildErrorException(Throwable cause, Location location) {
		super(cause, location);
	}

	public BuildErrorException(Throwable cause) {
		super(cause);
	}

}
