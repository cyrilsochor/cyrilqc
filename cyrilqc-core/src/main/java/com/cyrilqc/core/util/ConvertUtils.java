package com.cyrilqc.core.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ConvertUtils {

	private ConvertUtils() {
	}

	private static final String[] DURATION_LABELS = new String[] { "d", "h", "m", "s", "ms" };

	private static transient ThreadLocal<DateFormat> timestampDateFormat = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		}

		@Override
		public DateFormat get() {
			return super.get();
		}

		@Override
		public void set(DateFormat value) {
			super.set(value);
		}

		@Override
		public void remove() {
			super.remove();
		}

	};

	public static Boolean parseBoolean(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return Boolean.parseBoolean(valueString);
		}
	}

	public static Integer parseInteger(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return Integer.valueOf(valueString);
		}
	}

	public static Long parseLong(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return Long.valueOf(valueString);
		}
	}

	public static Float parseFloat(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return Float.valueOf(valueString);
		}
	}

	public static Double parseDouble(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return Double.valueOf(valueString);
		}
	}

	public static BigDecimal parseBigDecimal(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return new BigDecimal(valueString);
		}
	}

	public static BigInteger parseBigInteger(final String valueString) {
		if (StringUtils.isEmpty(valueString)) {
			return null;
		} else {
			return new BigInteger(valueString);
		}
	}

	public static Character parseCharacter(String valueString) {
		if (valueString == null) {
			return null;
		} else {
			final String trimmed = valueString.trim();
			switch (trimmed.length()) {
			case 0:
				return null;
			case 1:
				return trimmed.charAt(0);
			default:
				throw new RuntimeException("There are multiple characters in string '" + trimmed + "'");
			}
		}
	}

	public static String formatTimestamp(long timestamp) {
		if (timestamp <= 0) {
			return "null";
		} else {
			return timestampDateFormat.get().format(new Date(timestamp));
		}
	}

	public final static String durationToString(Long duration) {
		if (duration == null) {
			return null;
		} else {
			return durationToString((long) duration);
		}
	}

	public final static String durationToString(long duration) {
		final long days = duration / 86400000;
		final long hours = (duration % 86400000) / 3600000;
		final long minutes = (duration % 3600000) / 60000;
		final long seconds = (duration % 60000) / 1000;
		final long milis = (duration % 1000);
		return displayParts(2, new long[] { days, hours, minutes, seconds, milis }, DURATION_LABELS);
	}

	private static String displayParts(int nonZeroParts, long[] values, String[] labels) {
		if (values.length != labels.length) {
			throw new IllegalArgumentException("Different numbers of values " + Arrays.asList(values) + " and labels "
					+ Arrays.asList(labels));
		}

		final StringBuilder ret = new StringBuilder();
		int displayed = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i] != 0) {
				displayed++;

				if (displayed > 1) {
					ret.append(", ");
				}
				ret.append(values[i]);
				ret.append(" ");
				ret.append(labels[i]);

				if (displayed == 2) {
					break;
				}
			}
		}

		return ret.toString();
	}

}
