package com.cyrilqc.runner.junit4;

import java.util.List;

import org.apache.tools.ant.BuildException;
import org.junit.ComparisonFailure;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.Statement;

import com.cyrilqc.core.CyrilQCEngine;
import com.cyrilqc.core.CyrilQCProject;
import com.cyrilqc.core.CyrilQCTest;
import com.cyrilqc.core.exception.BuildAssertionException;
import com.cyrilqc.core.exception.BuildComparisonException;

public class JUnit4Runner extends ParentRunner<CyrilQCTest> implements Filterable, Sortable {

	private final Class<?> testClass;
	private final CyrilQCProject project;

	public JUnit4Runner(Class<?> testClass) throws Exception {
		super(testClass);
		this.testClass = testClass;
		final CyrilQCEngine engine = CyrilQCEngine.getInstance();
		project = engine.getProject();
	}

	@Override
	protected List<CyrilQCTest> getChildren() {
		return project.getTests();
	}

	@Override
	protected String getName() {
		return project.getName();
	}

	@Override
	protected Description describeChild(CyrilQCTest child) {
		return Description.createTestDescription(testClass, child.getName());
	}

	@Override
	protected void runChild(CyrilQCTest child, RunNotifier notifier) {
		notifier.fireTestStarted(describeChild(child));
		try {
			child.run();
		} catch (final Throwable e) {
			final Throwable fe;
			if (e instanceof BuildException) {
				BuildException firstBuildException = getFirstBuildException((BuildException) e);
				if (firstBuildException instanceof BuildComparisonException) {
					BuildComparisonException bce = (BuildComparisonException) firstBuildException;
					String bceMessage = e.getMessage();
					int expectedIndex = bceMessage.indexOf(" expected:");
					final String messageBase;
					if (expectedIndex == -1) {
						messageBase = bceMessage;
					} else {
						messageBase = bceMessage.substring(0, expectedIndex);
					}
					final ComparisonFailure ce = new ComparisonFailure(messageBase, bce.getExpected(), bce
							.getActual());
					ce.initCause(e);
					fe = ce;
				} else if (firstBuildException instanceof BuildAssertionException) {
					fe = new AssertionError(firstBuildException);
				} else {
					fe = e;
				}
			} else {
				fe = e;
			}
			notifier.fireTestFailure(new Failure(describeChild(child), fe));
		}
		notifier.fireTestFinished(describeChild(child));
	}

	@Override
	protected Statement withBeforeClasses(Statement statement) {
		return new RunBefores(project, statement);
	}

	private BuildException getFirstBuildException(BuildException e) {
		Throwable cause = e.getCause();
		if (cause instanceof BuildException) {
			return getFirstBuildException((BuildException) cause);
		} else {
			return e;
		}
	}
}
