package com.cyrilqc.core.task;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.junit.Assert;

public class AssertEqualsTask extends Task {

	String message;
	String expected;
	String actual;

	@Override
	public void execute() throws BuildException {
		Assert.assertEquals(message, expected, actual);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

}
