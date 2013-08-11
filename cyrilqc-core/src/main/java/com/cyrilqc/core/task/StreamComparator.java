package com.cyrilqc.core.task;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.Project;

public interface StreamComparator {

	void setAntProject(Project project);

	void setExpectedStream(InputStream expectedStream, String expectedLocation);

	void setActualStream(InputStream expectedStream, String actualLocation);

	void compare() throws IOException;

}
