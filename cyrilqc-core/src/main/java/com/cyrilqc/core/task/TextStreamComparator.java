package com.cyrilqc.core.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.tools.ant.Project;

public class TextStreamComparator extends StreamComparatorBase {

	public void compare() throws IOException {
		final BufferedReader expectedReader = new BufferedReader(new InputStreamReader(getExpectedStream()));
		try {
			final BufferedReader actualReader = new BufferedReader(new InputStreamReader(getActualStream()));
			try {
				compare(expectedReader, actualReader);
			} finally {
				actualReader.close();
			}
		} finally {
			expectedReader.close();
		}
	}

	protected void compare(BufferedReader expectedReader, BufferedReader actualReader) throws IOException {
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
					fail("File " + getExpectedLocation() + " has only " + (line - 1) + " lines and file "
							+ getActualLocation()
							+ " has more");
				}
			} else {
				if (actualLine == null) {
					fail("File " + getActualLocation() + " has only " + (line - 1) + " lines and file "
							+ getExpectedLocation()
							+ " has more");
				} else {
					assertEquals("Files " + getExpectedLocation() + " and " + getActualLocation()
							+ " differ at line " + line,
							expectedLine, actualLine);
				}
			}
		}
	}

}
