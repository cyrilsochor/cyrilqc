package com.cyrilqc.core;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.cyrilqc.core.exception.CyrilQCRuntimeException;
import com.cyrilqc.core.util.StringUtils;

public class CyrilQCProject {

	private static final String RUNTIME_HELPER_KEY = "cyrilqc.runtimeHelper";
	private static final String TEST_NAME_KEY = "TEST_NAME";

	private final CyrilQCEngine engine;
	private File projectFile;
	private Project defaultAntProject;

	private List<CyrilQCTest> allTests;
	private List<CyrilQCTest> enabledTests;
	private List<String> beforeModuleTargets;
	private List<String> afterModuleTargets;
	private List<String> beforeTestTargets;
	private List<String> afterTestTargets;

	private BuildLogger buildLogger;
	private final LinkedList<Integer> messageOutputLevels = new LinkedList<Integer>();

	public CyrilQCProject(CyrilQCEngine engine, File projectFile) throws Exception {
		this.engine = engine;
		this.projectFile = projectFile;
	}

	public void init() throws Exception {
		buildLogger = engine.getConfiguration().getLoggingLogger();
		if (buildLogger == null) {
			throw new CyrilQCRuntimeException("Build logger not configured");
		}
		buildLogger.setOutputPrintStream(engine.getConfiguration().getOutputPrintStream());
		buildLogger.setErrorPrintStream(engine.getConfiguration().getErrorPrintStream());

		this.defaultAntProject = prepareAntProject();

		logBannerStartEnd();
		logBannerLine("CyrilQC", true);
		logBannerLine("Loading project " + projectFile.getAbsolutePath());

		parseProject(defaultAntProject);
		if (defaultAntProject.getName() != null) {
			logBannerLine("Project name " + defaultAntProject.getName());
		}

		scanProject();
		for (final CyrilQCTest test : allTests) {
			final String suffix = enabledTests.contains(test) ? "" : " (excluded)";
			logBannerLine("Found " + test.getName() + suffix);
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
		return enabledTests;
	}

	protected org.apache.tools.ant.Project getAntCleanProject() throws Exception {
		final Project antProject = prepareAntProject();
		parseProject(antProject);
		return antProject;
	}

	protected Project prepareAntProject() {
		final Project antProject = new SingleCheckAntProject();
		final CyrilQCRuntimeHelper cyrilQCRuntimeHelper = new CyrilQCRuntimeHelper(this);

		setMessageOutputLevel(engine.getConfiguration().getLoggingLevelDefault());
		antProject.addBuildListener(buildLogger);

		antProject.setUserProperty("ant.file", projectFile.getAbsolutePath());
		antProject.setProperty("name", "CyrilQC");
		antProject.init();
		antProject.addReference("ant.projectHelper", ProjectHelper.getProjectHelper());
		antProject.addReference(RUNTIME_HELPER_KEY, cyrilQCRuntimeHelper);
		return antProject;
	}

	protected void parseProject(final Project antProject) {
		final ProjectHelper antHelper = antProject.getReference("ant.projectHelper");
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

		allTests = new LinkedList<CyrilQCTest>();
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

		Collections.sort(allTests, new CyrilQCTestNameComparator());

		enabledTests = filterTests(allTests);

	}

	protected List<CyrilQCTest> filterTests(List<CyrilQCTest> tests) {
		final List<CyrilQCTest> ret = new LinkedList<CyrilQCTest>();
		ret.addAll(tests);

		if (null != getEngine().getConfiguration().getTestsInclude()) {
			final Pattern includePattern = Pattern.compile(getEngine().getConfiguration().getTestsInclude());
			for (Iterator<CyrilQCTest> it = ret.iterator(); it.hasNext();) {
				final CyrilQCTest test = it.next();
				final Matcher matcher = includePattern.matcher(test.getName());
				if (!matcher.matches()) {
					it.remove();
				}
			}
		}

		if (null != getEngine().getConfiguration().getTestsExclude()) {
			final Pattern excludePattern = Pattern.compile(getEngine().getConfiguration().getTestsExclude());
			for (Iterator<CyrilQCTest> it = ret.iterator(); it.hasNext();) {
				final CyrilQCTest test = it.next();
				final Matcher matcher = excludePattern.matcher(test.getName());
				if (matcher.matches()) {
					it.remove();
				}
			}
		}

		final List<String> configredTests = getEngine().getConfiguration().getTests();
		if (!configredTests.isEmpty()) {

			final Map<String, CyrilQCTest> m = new HashMap<String, CyrilQCTest>();
			for (CyrilQCTest test : ret) {
				m.put(test.getName(), test);
			}

			ret.clear();
			for (String testName : configredTests) {
				final CyrilQCTest test = m.get(testName);
				if (test == null) {
					throw new CyrilQCRuntimeException("Test '" + testName + "' not found");
				} else {
					ret.add(test);
				}
			}

		}

		return ret;
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
		allTests.add(test);
	}

	void run(CyrilQCTest test) throws Exception {
		logSimpleBanner(test.getName());

		final Project antProject = getAntCleanProject();
		final CyrilQCRuntimeHelper runtimeHelper = getRuntimeHelper(antProject);
		runtimeHelper.setMode(RuntimeMode.RUN_TEST);
		runtimeHelper.setTestName(test.getName());
		antProject.setUserProperty(TEST_NAME_KEY, test.getName());

		try {
			setMessageOutputLevel(engine.getConfiguration().getLoggingLevelTest());

			try {
				final List<String> testTargets = new LinkedList<String>();
				testTargets.addAll(beforeTestTargets);
				testTargets.add(test.getTargetName());
				runTargets(antProject, testTargets, false, null);
			} finally {
				runTargets(antProject, afterTestTargets, true, null);
			}

			antProject.fireBuildFinished(null);
		} catch (final BuildException e) {
			throw e;
		} catch (final Throwable e) {
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
					project.setKeepGoingMode(true);
					executeTargets(project, targets);
					project.setKeepGoingMode(false);
				} else {
					executeTargets(project, targets);
				}
			} finally {
				restoreMessageOutputLevel();
			}
		}
	}

	protected void executeTargets(Project project, List<String> targets) throws Throwable {
		try {
			final Vector<String> v = new Vector<String>();
			v.addAll(targets);
			project.executeTargets(v);
		} catch (Throwable e) {
			project.fireBuildFinished(e);
			throw e;
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
		synchronized (messageOutputLevels) {
			messageOutputLevels.add(msgLevel);
			buildLogger.setMessageOutputLevel(msgLevel);
		}
	}

	public void restoreMessageOutputLevel() {
		synchronized (messageOutputLevels) {
			messageOutputLevels.removeLast();
			final Integer msgLevel = messageOutputLevels.getLast();
			buildLogger.setMessageOutputLevel(msgLevel);
		}
	}

	public static CyrilQCRuntimeHelper getRuntimeHelper(Project antProject) {
		return (CyrilQCRuntimeHelper) antProject.getReference(RUNTIME_HELPER_KEY);
	}
}
