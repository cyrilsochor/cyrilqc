package com.cyrilqc.core.task;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.tools.ant.Project;

import com.cyrilqc.core.exception.BuildAssertionException;
import com.cyrilqc.core.util.ConvertUtils;

public class AssertFileTask extends AssertionTask {

	private String actual;
	private File actualFile;
	private BigInteger expectedSize;
	private BigInteger expectedSizeMin;
	private BigInteger expectedSizeMax;

	public AssertFileTask() {
		super();
	}

	@Override
	public void executeAssertion() throws IOException {
		log("Checking file " + actual, Project.MSG_INFO);

		actualFile = new File(actual);

		boolean actualExists = actualFile.exists();

		if (!actualExists) {
			throw new BuildAssertionException("File " + actualFile.getAbsolutePath() + " not found", getLocation());
		}

		final long actualSize = actualFile.length();

		if (expectedSize != null) {
			if (actualSize != expectedSize.longValue()) {
				throw new BuildAssertionException("File " + actualFile.getAbsolutePath() + " has invalid size, expected "
						+ expectedSize + " bytes, actual " + actualSize + " bytes", getLocation());
			}
		}

		if (expectedSizeMin != null) {
			if (actualSize < expectedSizeMin.longValue()) {
				throw new BuildAssertionException("File " + actualFile.getAbsolutePath()
						+ " has invalid size, expected at least "
						+ expectedSizeMin + " bytes, actual " + actualSize + " bytes", getLocation());
			}
		}

		if (expectedSizeMax != null) {
			if (actualSize > expectedSizeMax.longValue()) {
				throw new BuildAssertionException("File " + actualFile.getAbsolutePath() + " has invalid size, expected maximum "
						+ expectedSizeMax + " bytes, actual " + actualSize + " bytes", getLocation());
			}
		}
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public BigInteger getExpectedSize() {
		return expectedSize;
	}

	public void setExpectedSize(String expectedSize) {
		this.expectedSize = ConvertUtils.parseSize(expectedSize);
	}

	public BigInteger getExpectedSizeMin() {
		return expectedSizeMin;
	}

	public void setExpectedSizeMin(String expectedSizeMin) {
		this.expectedSizeMin = ConvertUtils.parseSize(expectedSizeMin);
	}

	public BigInteger getExpectedSizeMax() {
		return expectedSizeMax;
	}

	public void setExpectedSizeMax(String expectedSizeMax) {
		this.expectedSizeMax = ConvertUtils.parseSize(expectedSizeMax);
	}

}