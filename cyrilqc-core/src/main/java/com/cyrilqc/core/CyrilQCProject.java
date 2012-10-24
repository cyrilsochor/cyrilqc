package com.cyrilqc.core;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.cyrilqc.core.util.StringUtils;

public class CyrilQCProject {

	private final CyrilQCEngine engine;
	private final URL projectURL;
	private Project antProject;
	private List<CyrilQCTest> tests;
	private RuntimeHelper runtimeHelper;

	private DefaultLogger consoleLogger;
	private ProjectHelper antHelper;
	private BlockingDeque<Integer> messageOutputLevels = new LinkedBlockingDeque<Integer>();

	public CyrilQCProject(CyrilQCEngine engine, URL projectURL) throws Exception {
		this.engine = engine;
		this.projectURL = projectURL;
		loadProject();
	}

	public String getName() {
		final String antName = antProject.getName();
		if (antName == null) {
			return engine.getConfiguration().getProjectNamePrefix();
		} else {
			return engine.getConfiguration().getProjectNamePrefix() + ":" + antName;
		}
	}

	public List<CyrilQCTest> getTests() {
		return tests;
	}

	private void loadProject() throws Exception {
		antProject = new org.apache.tools.ant.Project();
		runtimeHelper = new RuntimeHelper(this);
		consoleLogger = new DefaultLogger();
		antHelper = ProjectHelper.getProjectHelper();

		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		setMessageOutputLevel(engine.getConfiguration().getLoggingLevelDefault());
		antProject.addBuildListener(consoleLogger);

		logLogoStartEnd();
		// TODO solve universal URL
		final File projectFile = new File(projectURL.getFile());
		logLogoLine("CyrilQC", true);
		logLogoLine("Loading project " + projectFile.getAbsolutePath());
		antProject.setUserProperty("ant.file", projectFile.getAbsolutePath());
		antProject.setProperty("name", "CyrilQC");
		// antProject.fireBuildStarted();
		antProject.init();
		antProject.addReference("ant.projectHelper", antHelper);
		antProject.addReference("cyrilqc.runtimeHelper", runtimeHelper);
		antHelper.parse(antProject, projectFile);
		if (antProject.getName() != null) {
			logLogoLine("Project name " + antProject.getName());
		}

		cookProjectTests();
		// project.executeTarget(project.getDefaultTarget());
		// project.fireBuildFinished(null);

		for (CyrilQCTest test : tests) {
			logLogoLine("Found " + test.getName());
		}
		logLogoStartEnd();
	}

	@SuppressWarnings("unchecked")
	private List<String> getSortedTargets() {
		List<String> ret = new LinkedList<String>();
		ret.addAll(antProject.getTargets().keySet());
		Collections.sort(ret);
		return ret;
	}

	private void cookProjectTests() {
		tests = new LinkedList<CyrilQCTest>();
		for (final String targetName : getSortedTargets()) {
			if (targetName.startsWith(engine.getConfiguration().getTestTargetPrefix())) {
				addTest(targetName, false, targetName);
			} else if (targetName.startsWith(engine.getConfiguration().getMultiTestTargetPrefix())) {
				runtimeHelper.setMode(RuntimeMode.LOOKUP_TESTS);
				runtimeHelper.setTargetName(targetName);
				setMessageOutputLevel(engine.getConfiguration().getLoggingLevelInfrastructure());
				antProject.executeTarget(targetName);
				restoreMessageOutputLevel();
			}

		}
	}

	public void addTest(final String targetName, final boolean multi, final String name) {
		final CyrilQCTest test = new CyrilQCTest(this, targetName, multi, name);
		tests.add(test);
	}

	public void run(CyrilQCTest test) throws Throwable {
		logLogoStartEnd();
		logLogoLine(test.getName(), true);
		logLogoStartEnd();
		runtimeHelper.setMode(RuntimeMode.RUN_TEST);
		runtimeHelper.setTestName(test.getName());
		if (test.isMulti()) {
			setMessageOutputLevel(engine.getConfiguration().getLoggingLevelInfrastructure());
		} else {
			setMessageOutputLevel(engine.getConfiguration().getLoggingLevelTest());
		}
		try {
			antProject.executeTarget(test.getTargetName());
			antProject.fireBuildFinished(null);
		} catch (Throwable e) {
			Throwable rootCause = unpackBuildExceptions(e);
			antProject.fireBuildFinished(e);
			throw rootCause;
		} finally {
			restoreMessageOutputLevel();
			antProject.log("", engine.getConfiguration().getLoggingLevelLogo());
		}
	}

	private Throwable unpackBuildExceptions(Throwable e) {
		if (e.getCause() != null && e instanceof BuildException) {
			return unpackBuildExceptions(e.getCause());
		} else {
			return e;
		}
	}

	public CyrilQCEngine getEngine() {
		return engine;
	}

	public void fireBeforeTest() {
		setMessageOutputLevel(engine.getConfiguration().getLoggingLevelTest());
	}

	public void fireAfterTest() {
		restoreMessageOutputLevel();
	}

	private void logLogo(String msg) {
		antProject.log(msg, engine.getConfiguration().getLoggingLevelLogo());
	}

	private void logLogoStartEnd() {
		logLogo(StringUtils.characterSequence(engine.getConfiguration().getLogoCharacter(), engine.getConfiguration()
				.getLogoLength()));
	}

	private void logLogoLine(String msg) {
		logLogoLine(msg, false);
	}

	private void logLogoLine(String msg, boolean center) {
		logLogo(StringUtils.surroundString(msg, engine.getConfiguration().getLogoCharacter(), engine.getConfiguration()
				.getLogoLength(), center));
	}

	public void setMessageOutputLevel(int msgLevel) {
		messageOutputLevels.add(msgLevel);
		consoleLogger.setMessageOutputLevel(msgLevel);
	}

	public void restoreMessageOutputLevel() {
		messageOutputLevels.removeLast();
		final Integer msgLevel = messageOutputLevels.peekLast();
		consoleLogger.setMessageOutputLevel(msgLevel);
	}

}
