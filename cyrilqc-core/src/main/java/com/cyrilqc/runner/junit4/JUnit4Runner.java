package com.cyrilqc.runner.junit4;

import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;

import com.cyrilqc.core.CyrilQCEngine;
import com.cyrilqc.core.CyrilQCProject;
import com.cyrilqc.core.CyrilQCTest;

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
		} catch (Throwable e) {
			notifier.fireTestFailure(new Failure(describeChild(child), e));
		}
		notifier.fireTestFinished(describeChild(child));
	}

}
