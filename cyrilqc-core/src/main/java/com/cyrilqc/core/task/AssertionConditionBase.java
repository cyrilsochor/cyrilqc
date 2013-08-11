package com.cyrilqc.core.task;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.ConditionBase;
import org.junit.ComparisonFailure;

import com.cyrilqc.core.exception.BuildAssertionException;
import com.cyrilqc.core.exception.BuildComparisonException;

public class AssertionConditionBase extends ConditionBase {
	public void execute() throws BuildException {
		try {
			executeAssertion();
		} catch (final BuildException e) {
			throw e;
		} catch (final ComparisonFailure e) {
			throw new BuildComparisonException(e, getLocation());
		} catch (final AssertionError e) {
			throw new BuildAssertionException(e, getLocation());
		} catch (final Throwable e) {
			throw new BuildException(e.getMessage(), e, getLocation());
		}
	}

	protected void executeAssertion() throws Exception {

	}

}
