package com.cyrilqc.runner;

import java.util.ArrayList;

import org.junit.runner.JUnitCore;

import com.cyrilqc.runner.junit4.CyrilQCJUnit4Test;

public class CyrilQC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> junitArgs = new ArrayList<String>();
		junitArgs.add(CyrilQCJUnit4Test.class.getName());
		JUnitCore.main(junitArgs.toArray(new String[junitArgs.size()]));
	}

}
