package com.cyrilqc.core.util;

public class StringUtils {

	private StringUtils() {
	}

	public static String characterSequence(char character, int length) {
		final StringBuilder ret = new StringBuilder();
		appendCharacterSequence(ret, character, length);
		return ret.toString();
	}

	public static void appendCharacterSequence(final StringBuilder buffer, char character, int length) {
		for (int i = 0; i < length; i++) {
			buffer.append(character);
		}
	}

	public static String surroundString(String msg, char character, int length, boolean center) {
		final StringBuilder ret = new StringBuilder();
		ret.append(character);
		ret.append("  ");
		int i = 3;
		if (!center || msg.length() + i * 2 > length) {
			ret.append(msg);
			i += msg.length();
			if (i < length) {
				for (; i < length - 1; i++) {
					ret.append(" ");
				}
				ret.append(character);
			}
		} else {
			final int spacesBefore = (length - msg.length() - i * 2) / 2;
			final int spacesAfter = (length - msg.length() - i * 2 - spacesBefore);
			appendCharacterSequence(ret, ' ', spacesBefore);
			ret.append(msg);
			appendCharacterSequence(ret, ' ', spacesAfter);
			ret.append("  ");
			ret.append(character);
		}
		return ret.toString();
	}

	public static boolean isNotEmpty(String s) {
		return s != null && s.length() > 0;
	}
}
