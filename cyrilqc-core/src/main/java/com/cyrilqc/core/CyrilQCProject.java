package com.cyrilqc.core;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.cyrilqc.core.util.StringUtils;

public class CyrilQCProject {

	private static final String RUNTIME_HELPER_KEY = "cyrilqc.runtimeHelper";

	private final CyrilQCEngine engine;
	private final URL projectURL;

	private Project defaultAntProject;

	private List<CyrilQCTest> tests;
	private List<String> beforeModuleTargets;
	private List<String> afterModuleTargets;
	private List<String> beforeTestTargets;
	private List<String> afterTestTargets;

	private File projectFile;
	private DefaultLogger consoleLogger;
	private ProjectHelper antHelper;
	private final BlockingDeque<Integer> messageOutputLevels = new LinkedBlockingDeque<Integer>();

	public CyrilQCProject(CyrilQCEngine engine, URL projectURL) throws Exception {
		this.engine = engine;
		this.projectURL = projectURL;
	}

	public void init() throws Exception {
		// TODO solve universal URL
		projectFile = new File(projectURL.getFile());

		consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);

		antHelper = ProjectHelper.getProjectHelper();

		this.defaultAntProject = prepareAntProject();

		logBannerStartEnd();
		logBannerLine("CyrilQC", true);
		logBannerLine("Loading project " + projectFile.getAbsolutePath());

		parseProject(defaultAntProject);
		if (defaultAntProject.getName() != null) {
			logBannerLine("Project name " + defaultAntProject.getName());
		}

		scanProject();
		for (final CyrilQCTest test : tests) {
			logBannerLine("Found " + test.getName());
		}

		logBannerStartEnd();
	}

	public String getName() {
		String name;
		final String antName = defaultAntProject.getName();
		if (antName == null) {
			name = engine.getConfiguration().getProjectNamePrefix();
		} else {
			name = engine.getConfiguration().getProjectNamePrefix() + antName;
		}
		return name;
	}

	public List<CyrilQCTest> getTests() {
		return tests;
	}

	private org.apache.tools.ant.Project getAntCleanProject() throws Exception {
		final Project antProject = prepareAntProject();
		parseProject(antProject);
		return antProject;
	}

	protected Project prepareAntProject() {
		final Project antProject = new org.apache.tools.ant.Project();
		final CyrilQCRuntimeHelper cyrilQCRuntimeHelper = new CyrilQCRuntimeHelper(this);

		setMessageOutputLevel(engine.getConfiguration().getLoggingLevelDefault());
		antProject.addBuildListener(consoleLogger);

		antProject.setUserProperty("ant.file", projectFile.getAbsolutePath());
		antProject.setProperty("name", "CyrilQC");
		antProject.init();
		antProject.addReference("ant.projectHelper", antHelper);
		antProject.addReference(RUNTIME_HELPER_KEY, cyrilQCRuntimeHelper);
		return antProject;
	}

	protected void parseProject(final Project antProject) {
		antHelper.parse(antProject, projectFile);
	}

	private List<String> getSortedTargets() {
		final List<String> ret = new LinkedList<String>();
		ret.addAll(defaultAntProject.getTargets().keySet());
		Collections.sort(ret);
		return ret;
	}

	private void scanProject() throws Exception {
		final Project antProject = getAntCleanProject();

		tests = new LinkedList<CyrilQCTest>();
		beforeModuleTargets = new LinkedList<String>();
		afterModuleTargets = new LinkedList<String>();
		beforeTestTargets = new LinkedList<String>();
		afterTestTargets = new LinkedList<String>();

		final List<String> multiTestTargets = new LinkedList<String>();

		// scan all targets and add target names to proper collection
		for (final String targetName : getSortedTargets()) {
			if (targetName.startsWith(engine.getConfiguration().getTestTargetPrefix())) {
				addTest(targetName, false, targetName);
			} else if (targetName.startsWith(engine.getConfiguration().getMultiTestTargetPrefix())) {
				multiTestTargets.add(targetName);
			} else if (targetName.startsWith(engine.getConfiguration().getBeforeModuleTargetPrefix())) {
				beforeModuleTargets.add(targetName);
			} else if (targetName.startsWith(engine.getConfiguration().getAfterModuleTargetPrefix())) {
				afterModuleTargets.add(targetName);
			} else if (targetName.startsWith(engine.getConfiguration().getBeforeTestTargetPrefix())) {
				beforeTestTargets.add(targetName);
			} else if (targetName.startsWith(engine.getConfiguration().getAfterTestTargetPrefix())) {
				afterTestTargets.add(targetName);
			}
		}

		// add multitests
		for (final String targetName : multiTestTargets) {
			scanTargetForMultiTest(antProject, targetName);
		}

		Collections.sort(tests, new CyrilQCTestNameComparator());
	}

	protected void scanTargetForMultiTest(final Project antProject, final String targetName) {
		final CyrilQCRuntimeHelper runtimeHelper = getRuntimeHelper(antProject);
		runtimeHelper.setMode(RuntimeMode.LOOKUP_TESTS);
		runtimeHelper.setTargetName(targetName);
		setMessageOutputLevel(engine.getConfiguration().getLoggingLevelInfrastructure());
		antProject.executeTarget(targetName);
		restoreMessageOutputLevel();
	}

	void addTest(final String targetName, final boolean multi, final String name) {
		final CyrilQCTest test = new CyrilQCTest(this, targetName, multi, name);
		tests.add(test);
	}

	void run(CyrilQCTest test) throws Exception {
		logSimpleBanner(test.getName());

		final Project antProject = getAntCleanProject();
		final CyrilQCRuntimeHelper runtimeHelper = getRuntimeHelper(antProject);
		runtimeHelper.setMode(RuntimeMode.RUN_TEST);
		runtimeHelper.setTestName(test.getName());

		try {

			try {
				runTargets(antProject, beforeTestTargets, false, null);
				if (test.isMulti()) {
					setMessageOutputLevel(engine.getConfiguration().getLoggingLevelInfrastructure());
				} else {
					setMessageOutputLevel(engine.getConfiguration().getLoggingLevelTest());
				}
				antProject.executeTarget(test.getTargetName());
			} finally {
				runTargets(antProject, afterTestTargets, true, null);
			}

			antProject.fireBuildFinished(null);
		} catch (final BuildException e) {
			antProject.fireBuildFinished(e);
			throw e;
		} catch (final Throwable e) {
			antProject.fireBuildFinished(e);
			throw new BuildException(e);
		} finally {
			restoreMessageOutputLevel();
		}
	}

	public void runBeforeModule() throws Throwable {
		runTargets(getAntCleanProject(), beforeModuleTargets, false, getEngine().getConfiguration().getBeforeModuleTargetPrefix());
	}

	public void runAfterModule() throws Throwable {
		runTargets(getAntCleanProject(), afterModuleTargets, true, getEngine().getConfiguration().getAfterModuleTargetPrefix());
	}

	protected void runTargets(final Project project, final List<String> targets, final boolean recoverError,
			final String bannerMessage) throws Throwable {
		if (targets != null && !targets.isEmpty()) {
			if (bannerMessage != null) {
				logSimpleBanner(bannerMessage);
			}

			setMessageOutputLevel(engine.getConfiguration().getLoggingLevelTest());
			try {
				if (recoverError) {
					executeTargetsRecoverError(project, targets);
				} else {
					executeTargetsToFirstError(project, targets);
				}
			} finally {
				restoreMessageOutputLevel();
			}
		}
	}

	private void executeTargetsToFirstError(Project project, List<String> targets) throws Throwable {
		try {
			final Vector<String> v = new Vector<String>();
			v.addAll(targets);
			project.executeTargets(v);
		} catch (Throwable e) {
			project.fireBuildFinished(e);
			throw e;
		}
	}

	private void executeTargetsRecoverError(Project project, List<String> targets) throws Throwable {
		for (String target : targets) {
			try {
				final Vector<String> v = new Vector<String>();
				v.add(target);
				project.executeTargets(v);
			} catch (Throwable e) {
				project.fireBuildFinished(e);
			}
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

	public void logSimpleBanner(String sigleLine) {
		defaultAntProject.log("", engine.getConfiguration().getLoggingLevelBanner());
		logBannerStartEnd();
		logBannerLine(sigleLine, true);
		logBannerStartEnd();
	}

	private void logBannerStartEnd() {
		final String text = StringUtils.characterSequence(
				engine.getConfiguration().getBannerCharacter(),
				engine.getConfiguration().getBannerLength());
		printBannerLine(text);
	}

	private void logBannerLine(String msg) {
		logBannerLine(msg, false);
	}

	private void logBannerLine(String msg, boolean center) {
		final String text = StringUtils.surroundString(
				msg,
				engine.getConfiguration().getBannerCharacter(),
				engine.getConfiguration().getBannerLength(),
				center);
		printBannerLine(text);
	}

	private void printBannerLine(String text) {
		defaultAntProject.log(text, engine.getConfiguration().getLoggingLevelBanner());
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

	public static CyrilQCRuntimeHelper getRuntimeHelper(Project antProject) {
		return (CyrilQCRuntimeHelper) antProject.getReference(RUNTIME_HELPER_KEY);
	}
}
