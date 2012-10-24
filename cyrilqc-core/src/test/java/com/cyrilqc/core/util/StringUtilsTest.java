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

}
