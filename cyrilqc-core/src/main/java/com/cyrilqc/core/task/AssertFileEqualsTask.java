package com.cyrilqc.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.cyrilqc.core.exception.TaskErrorException;

public class AssertFileEqualsTask extends Task {

	private String expected;
	private String actual;
	private File expectedFile;
	private File actualFile;

	@Override
	public void execute() throws BuildException {
		try {
			expectedFile = new File(expected);
			if (!expectedFile.exists()) {
				throw new TaskErrorException("Expected file " + expectedFile.getAbsolutePath() + " not found");
			}

			actualFile = new File(actual);
			if (!actualFile.exists()) {
				throw new TaskErrorException("Actual file " + actualFile.getAbsolutePath() + " not found");
			}

			log("Comparing text files " + expectedFile + " and " + actualFile, Project.MSG_INFO);
			final BufferedReader expectedReader = new BufferedReader(new FileReader(expectedFile));
			try {
				final BufferedReader actualReader = new BufferedReader(new FileReader(actualFile));
				try {
					compare(expectedReader, actualReader);
				} finally {
					actualReader.close();
				}
			} finally {
				expectedReader.close();
			}
		} catch (Throwable e) {
			throw new BuildException(e.getMessage(), e, getLocation());
		}
	}

	private void compare(BufferedReader expectedReader, BufferedReader actualReader) throws IOException {
		int line = 0;
		while (true) {
			line++;
			log("Comparing line " + line, Project.MSG_DEBUG);
			final String expectedLine = expectedReader.readLine();
			final String actualLine = actualReader.readLine();

			if (expectedLine == null) {
				if (actualLine == null) {
					log("Files have same content", Project.MSG_DEBUG);
					break;
				} else {
					fail("File " + expectedFile.getName() + " has only " + (line - 1) + " lines and file " + actualFile.getName()
							+ " has more");
				}
			} else {
				if (actualLine == null) {
					fail("File " + actualFile.getName() + " has only " + (line - 1) + " lines and file " + expectedFile.getName()
							+ " has more");
				} else {
					assertEquals("Files " + expectedFile.getName() + " and " + actualFile.getName() + " differ at line " + line,
							expectedLine, actualLine);
				}
			}
		}
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

}
