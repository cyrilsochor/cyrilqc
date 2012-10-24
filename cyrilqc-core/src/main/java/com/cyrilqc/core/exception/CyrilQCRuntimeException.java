package com.cyrilqc.core.exception;

public class CyrilQCRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -8733018689935538156L;

	public CyrilQCRuntimeException() {
		super();
	}

	public CyrilQCRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CyrilQCRuntimeException(String message) {
		super(message);
	}

	public CyrilQCRuntimeException(Throwable cause) {
		super(cause);
	}

}
