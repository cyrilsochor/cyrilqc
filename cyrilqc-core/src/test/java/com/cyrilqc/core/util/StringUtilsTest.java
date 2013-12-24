package com.cyrilqc.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testCharacterSequence() {
		assertEquals("::::::::", StringUtils.characterSequence(':', 8));
		assertEquals("", StringUtils.characterSequence('-', 0));
	}

	@Test
	public void testSurroundString() {
		assertEquals(":        A         :", StringUtils.surroundString("A", ':', 20, true));
	}

	@Test
	public void testFirstDifferenceEmpty() {
		assertEquals(-1, StringUtils.getFirstDifference("", ""));
	}

	@Test
	public void testFirstDifferenceSingleSame() {
		assertEquals(-1, StringUtils.getFirstDifference("a", "a"));
	}

	@Test
	public void testFirstDifferenceSingleDifferent() {
		assertEquals(0, StringUtils.getFirstDifference("a", "x"));
	}

	@Test
	public void testFirstDifferenceSingleEmpty() {
		assertEquals(0, StringUtils.getFirstDifference("a", ""));
	}

	@Test
	public void testFirstDifferenceEmptyLong() {
		assertEquals(0, StringUtils.getFirstDifference("", "123456"));
	}

	@Test
	public void testFirstDifferenceAutobus() {
		assertEquals(4, StringUtils.getFirstDifference("autobus", "autoBUS"));
	}

	@Test
	public void testFirstDifferenceAutobusSuffix() {
		assertEquals(7, StringUtils.getFirstDifference("autobus223", "autobus"));
	}

	@Test
	public void testFirstDifferenceAutobusNewLine() {
		assertEquals(7, StringUtils.getFirstDifference("autobus", "autobus\n"));
	}

	@Test
	public void testFirstDifferenceAutobusAuto() {
		assertEquals(4, StringUtils.getFirstDifference("autobus", "auto"));
	}

	@Test(expected = NullPointerException.class)
	public void testFirstDifferenceFirstNull() {
		StringUtils.getFirstDifference(null, "");
	}

	@Test(expected = NullPointerException.class)
	public void testFirstDifferenceSecondNull() {
		StringUtils.getFirstDifference("autobus", null);
	}
}
