package com.cyrilqc.core.task;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;

public class AssertTask extends ConditionBase {

	private String message = "Assertion failed";

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void execute() throws BuildException {
		int count = countConditions();
		if (count > 1) {
			throw new BuildException("At least one condition required", getLocation());
		}
		if (count < 1 || !((Condition) getConditions().nextElement()).eval()) {
			throw new BuildException(message, getLocation());
		}
	}

}