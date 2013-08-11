package com.cyrilqc.core.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tools.ant.Project;

import com.cyrilqc.core.exception.BuildAssertionException;

public class AssertFileEqualsTask extends AssertionTask {

	private String expected;
	private String actual;
	private File expectedFile;
	private File actualFile;

	public AssertFileEqualsTask() {
		super();
	}

	@Override
	public void executeAssertion() throws IOException {
		log("Comparing files, expected " + expected + " and actual " + actual, Project.MSG_INFO);

		expectedFile = new File(expected);
		actualFile = new File(actual);

		boolean expectedExists = expectedFile.exists();
		boolean actualExists = actualFile.exists();

		if (!expectedExists && !actualExists) {
			return;
		}

		if (!expectedExists) {
			throw new BuildAssertionException("Expected file " + expectedFile.getAbsolutePath() + " not found", getLocation());
		}
		if (!actualExists) {
			throw new BuildAssertionException("Actual file " + actualFile.getAbsolutePath() + " not found", getLocation());
		}

		final String contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(getExpected());
		log("Autodetected content type '" + contentType + "' of file " + getExpected(), Project.MSG_INFO);

		final StreamComparator comparator = new TextStreamComparator();
		comparator.setAntProject(getProject());
		comparator.setExpectedStream(new FileInputStream(expectedFile), expected);
		comparator.setActualStream(new FileInputStream(actualFile), actual);
		comparator.compare();
	}

	public String getExpected() {
		return expected;
	}

	public void setExpected(String expected) {
		this.expected = expected;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	protected File getExpectedFile() {
		return expectedFile;
	}

	protected void setExpectedFile(File expectedFile) {
		this.expectedFile = expectedFile;
	}

	protected File getActualFile() {
		return actualFile;
	}

	protected void setActualFile(File actualFile) {
		this.actualFile = actualFile;
	}

}