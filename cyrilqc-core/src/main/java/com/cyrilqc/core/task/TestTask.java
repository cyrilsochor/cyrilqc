package com.cyrilqc.core.task;

import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

import com.cyrilqc.core.RuntimeHelper;

public class TestTask extends Task implements TaskContainer {

	private String name;
	private List<Task> tasks = new LinkedList<Task>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addTask(Task nestedTask) {
		this.tasks.add(nestedTask);
	}

	@Override
	public void execute() throws BuildException {
		final RuntimeHelper runtimeHelper = getRuntimeHelper();
		if (runtimeHelper == null) {
			log("cyrilqc runtime helper not found, executing childs task", null, Project.MSG_WARN);
			executeChildTasks();
		} else {
			log("found cyrilqc runtime helper " + runtimeHelper, null, Project.MSG_DEBUG);
			switch (runtimeHelper.getMode()) {
			case LOOKUP_TESTS:
				runtimeHelper.addTest(name);
				break;
			case RUN_TEST:
				if (name.equals(runtimeHelper.getTestName())) {
					executeChildTasks();
				}
				break;
			default:
				break;
			}
		}
	}

	public RuntimeHelper getRuntimeHelper() {
		return (RuntimeHelper) getProject().getReference("cyrilqc.runtimeHelper");
	}

	private void executeChildTasks() {
		RuntimeHelper runtimeHelper = getRuntimeHelper();
		if (runtimeHelper != null) {
			runtimeHelper.fireBeforeTest();
		}
		for (Task task : tasks) {
			task.perform();
		}
		if (runtimeHelper != null) {
			runtimeHelper.fireAfterTest();
		}
	}

}
