package com.cyrilqc.runner.junit4;

import org.junit.runners.model.Statement;

import com.cyrilqc.core.CyrilQCProject;

class RunAfterModule extends Statement {

	private final CyrilQCProject project;
	private final Statement next;

	public RunAfterModule(CyrilQCProject project, Statement next) {
		this.project = project;
		this.next = next;
	}

	@Override
	public void evaluate() throws Throwable {
		try {
			next.evaluate();
		} finally {
			project.runAfterModule();
		}
	}

}
