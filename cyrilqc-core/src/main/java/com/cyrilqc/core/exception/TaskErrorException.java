package com.cyrilqc.core.exception;

public class TaskErrorException extends RuntimeException {

	private static final long serialVersionUID = 8645086719179662518L;

	public TaskErrorException() {
		super();
	}

	public TaskErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskErrorException(String message) {
		super(message);
	}

	public TaskErrorException(Throwable cause) {
		super(cause);
	}

}
