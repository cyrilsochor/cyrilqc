package com.cyrilqc.runner.junit4;

import org.junit.runners.model.Statement;

import com.cyrilqc.core.CyrilQCProject;

public class RunBefores extends Statement {

	private final CyrilQCProject project;
	private final Statement next;

	public RunBefores(CyrilQCProject project, Statement next) {
		this.project = project;
		this.next = next;
	}

	@Override
	public void evaluate() throws Throwable {
		project.runBeforeModule();
		next.evaluate();
	}

}
