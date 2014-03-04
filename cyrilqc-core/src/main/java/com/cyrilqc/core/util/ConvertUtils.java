package com.cyrilqc.core.util;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern SIZE_PATTERN = Pattern.compile("\\s*(\\d+)\\s*(\\w*)\\s*", Pattern.CASE_INSENSITIVE);

	private static final Map<String, BigInteger> FILE_SIZE_MULTI;

	static {
		FILE_SIZE_MULTI = new HashMap<String, BigInteger>();

		FILE_SIZE_MULTI.put("", BigInteger.valueOf(1));
		FILE_SIZE_MULTI.put("bytes", BigInteger.valueOf(1));
		FILE_SIZE_MULTI.put("b", BigInteger.valueOf(1));

		FILE_SIZE_MULTI.put("kilobyte", BigInteger.valueOf(10).pow(3));
		FILE_SIZE_MULTI.put("kb", BigInteger.valueOf(10).pow(3));
		FILE_SIZE_MULTI.put("megabyte", BigInteger.valueOf(10).pow(6));
		FILE_SIZE_MULTI.put("mb", BigInteger.valueOf(10).pow(6));
		FILE_SIZE_MULTI.put("gigabyte", BigInteger.valueOf(10).pow(9));
		FILE_SIZE_MULTI.put("gb", BigInteger.valueOf(10).pow(9));
		FILE_SIZE_MULTI.put("terabyte", BigInteger.valueOf(10).pow(12));
		FILE_SIZE_MULTI.put("tr", BigInteger.valueOf(10).pow(12));
		FILE_SIZE_MULTI.put("petabyte", BigInteger.valueOf(10).pow(15));
		FILE_SIZE_MULTI.put("pb", BigInteger.valueOf(10).pow(15));
		FILE_SIZE_MULTI.put("exabyte", BigInteger.valueOf(10).pow(18));
		FILE_SIZE_MULTI.put("eb", BigInteger.valueOf(10).pow(18));
		FILE_SIZE_MULTI.put("zettabyte", BigInteger.valueOf(10).pow(21));
		FILE_SIZE_MULTI.put("zb", BigInteger.valueOf(10).pow(21));
		FILE_SIZE_MULTI.put("yottabyte", BigInteger.valueOf(10).pow(24));
		FILE_SIZE_MULTI.put("yb", BigInteger.valueOf(10).pow(24));

		FILE_SIZE_MULTI.put("kibibyte", BigInteger.valueOf(2).pow(10));
		FILE_SIZE_MULTI.put("kib", BigInteger.valueOf(2).pow(10));
		FILE_SIZE_MULTI.put("k", BigInteger.valueOf(2).pow(10));
		FILE_SIZE_MULTI.put("mebibyte", BigInteger.valueOf(2).pow(20));
		FILE_SIZE_MULTI.put("mib", BigInteger.valueOf(2).pow(20));
		FILE_SIZE_MULTI.put("m", BigInteger.valueOf(2).pow(20));
		FILE_SIZE_MULTI.put("gibibyte", BigInteger.valueOf(2).pow(30));
		FILE_SIZE_MULTI.put("gib", BigInteger.valueOf(2).pow(30));
		FILE_SIZE_MULTI.put("g", BigInteger.valueOf(2).pow(30));
		FILE_SIZE_MULTI.put("tebibyte", BigInteger.valueOf(2).pow(40));
		FILE_SIZE_MULTI.put("tib", BigInteger.valueOf(2).pow(40));
		FILE_SIZE_MULTI.put("t", BigInteger.valueOf(2).pow(40));
		FILE_SIZE_MULTI.put("pebibyte", BigInteger.valueOf(2).pow(50));
		FILE_SIZE_MULTI.put("pib", BigInteger.valueOf(2).pow(50));
		FILE_SIZE_MULTI.put("p", BigInteger.valueOf(2).pow(50));
		FILE_SIZE_MULTI.put("exbibyte", BigInteger.valueOf(2).pow(60));
		FILE_SIZE_MULTI.put("eib", BigInteger.valueOf(2).pow(60));
		FILE_SIZE_MULTI.put("e", BigInteger.valueOf(2).pow(60));
		FILE_SIZE_MULTI.put("zebibyte", BigInteger.valueOf(2).pow(70));
		FILE_SIZE_MULTI.put("zib", BigInteger.valueOf(2).pow(70));
		FILE_SIZE_MULTI.put("z", BigInteger.valueOf(2).pow(70));
		FILE_SIZE_MULTI.put("yobibyte", BigInteger.valueOf(2).pow(80));
		FILE_SIZE_MULTI.put("yib", BigInteger.valueOf(2).pow(80));
		FILE_SIZE_MULTI.put("y", BigInteger.valueOf(2).pow(80));
	}

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

	public static PrintStream parsePrintStream(String streamDefinition) {
		if ("system:out".equals(streamDefinition)) {
			return System.out;
		} else if ("system:err".equals(streamDefinition)) {
			return System.err;
		} else {
			throw new IllegalArgumentException("Invalid stream definition '" + streamDefinition + "'");
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

	/**
	 * kilobyte (kB) 10 3 kibibyte (KiB) 2 10 megabyte (MB) 10 6 mebibyte(MiB) 2
	 * 20 gigabyte (GB) 10 9 gibibyte (GiB) 2 30 terabyte (TB) 10 12 tebibyte
	 * (TiB) 2 40 petabyte (PB) 10 15 pebibyte (PiB) 2 50 exabyte (EB) 10 18
	 * exbibyte (EiB) 2 60 zettabyte (ZB) 10 21 zebibyte (ZiB) 2 70 yottabyte
	 * (YB) 10 24 yobibyte (YiB) 2 80
	 **/
	public static BigInteger parseSize(String input) {
		if (input == null) {
			return null;
		}

		final String norm = input.toLowerCase();

		final Matcher matcher = SIZE_PATTERN.matcher(norm);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid format of size '" + input + "'");
		}

		final String numberPart = matcher.group(1);
		final String multiPart = matcher.group(2);
		BigInteger ret = BigInteger.valueOf(Long.valueOf(numberPart));
		final BigInteger multi = FILE_SIZE_MULTI.get(multiPart);
		if (multi == null) {
			;
			throw new IllegalArgumentException("Unknown size suffix '" + multiPart + "', valid values are "
					+ new TreeSet<String>(FILE_SIZE_MULTI.keySet()));
		}

		return ret.multiply(multi);
	}

}
