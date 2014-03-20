package com.cyrilqc.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class Random extends Task {

	private String property;
	private long min;
	private long max;

	private java.util.Random random = new java.util.Random();

	@Override
	public void execute() throws BuildException {
		final long value;
		if (min == max) {
			value = min;
		} else if (min > max) {
			throw new BuildException("Minimum (" + min
					+ ") must be greather then maximum (" + max + ")");
		} else {
			value = min + nextLong(max - min);
		}
		getProject().setProperty(property, Long.toString(value));
	}

	protected long nextLong(long n) {
		long bits, val;
		do {
			bits = (random.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

}
