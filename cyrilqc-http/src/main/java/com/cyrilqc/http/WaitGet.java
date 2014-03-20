package com.cyrilqc.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Get;

import com.cyrilqc.core.util.ConvertUtils;

public class WaitGet extends Get {

	private static final long DEFAULT_DELAY = 5000;
	private static final long DEFAULT_MAX_ATTEMMPTS = 3;

	private long delayBeforeFirst = 0;
	private long delay = DEFAULT_DELAY;
	private long maxAttempts = DEFAULT_MAX_ATTEMMPTS;

	private String notExpectedBodySubstring;

	private File waitDest;

	@Override
	public void execute() throws BuildException {

		if (delayBeforeFirst != 0) {
			log("Sleeping " + ConvertUtils.durationToString(delayBeforeFirst) + " before first attempt");
			sleep(delayBeforeFirst);
		}

		attempts: for (int seq = 1;; seq++) {
			try {
				super.execute();
				if (notExpectedBodySubstring != null) {
					if (!waitDest.exists()) {
						throw new BuildException("Destination " + waitDest.getAbsolutePath() + " + not exists");
					}
					checkNotExpectedBodySubstring();
				}
				break attempts;
			} catch (BuildException e) {
				if (seq == maxAttempts) {
					throw e;
				} else {
					log("Get attempt " + seq + " failed - sleeping " + ConvertUtils.durationToString(delay) + " for next attempt");
					if (delay != 0) {
						sleep(delay);
					}
				}
			}
		}
	}

	private void sleep(long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e1) {
			throw new BuildException("Wait interupted", e1);
		}
	}

	private void checkNotExpectedBodySubstring() {
		final BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(waitDest));
		} catch (FileNotFoundException e) {
			throw new BuildException(e);
		}
		try {
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains(notExpectedBodySubstring)) {
					throw new BuildException("Not expected substring found");
				}
			}
			return;
		} catch (IOException e) {
			throw new BuildException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new BuildException(e);
			}
		}
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(long maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	public String getNotExpectedBodySubstring() {
		return notExpectedBodySubstring;
	}

	public void setNotExpectedBodySubstring(String notExpectedBodySubstring) {
		this.notExpectedBodySubstring = notExpectedBodySubstring;
	}

	public long getDelayBeforeFirst() {
		return delayBeforeFirst;
	}

	public void setDelayBeforeFirst(long delayBeforeFirst) {
		this.delayBeforeFirst = delayBeforeFirst;
	}

	@Override
	public void setDest(File dest) {
		this.waitDest = dest;
		super.setDest(dest);
	}

}
