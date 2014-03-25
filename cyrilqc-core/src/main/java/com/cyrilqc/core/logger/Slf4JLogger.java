package com.cyrilqc.core.logger;

import java.io.PrintStream;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyrilqc.core.exception.BuildAssertionException;
import com.cyrilqc.core.util.ConvertUtils;

public class Slf4JLogger implements BuildLogger {

	private int msgOutputLevel = 0;
	private long startTime = System.currentTimeMillis();
	private boolean categoryHierarchy = true;

	public void buildStarted(BuildEvent event) {
		final Logger logger = getLogger(event);
		logger.debug("Build started");
		this.startTime = System.currentTimeMillis();
	}

	public void buildFinished(BuildEvent event) {
		final Logger logger = getLogger(event);
		final long endTime = System.currentTimeMillis();
		final String durationToString = ConvertUtils.durationToString(endTime - this.startTime);
		final Throwable exception = event.getException();
		if (exception == null) {
			logger.info("BUILD SUCCESSFUL\t\tTotal time: " + durationToString);
		} else if (exception instanceof BuildAssertionException) {
			logger.error(exception.getMessage());
			logger.error("BUILD FAILED\t\tTotal time: " + durationToString);
		} else {
			logger.error("Build error", exception);
			logger.error("BUILD FAILED\t\tTotal time: " + durationToString);
		}
	}

	public void targetStarted(BuildEvent event) {
		if (2 <= this.msgOutputLevel) {
			final Logger logger = getLogger(event);
			logger.info("Target \"" + event.getTarget().getName() + "\" started");
		}
	}

	public void targetFinished(BuildEvent event) {
		if (3 <= this.msgOutputLevel) {
			String targetName = event.getTarget().getName();
			final Logger logger = getLogger(event);
			if (event.getException() == null) {
				logger.info("Target \"" + targetName + "\" finished");
			} else {
				logger.error("Target \"" + targetName + "\" finished with error", event.getException());
			}
		}
	}

	public void taskStarted(BuildEvent event) {
		if (3 <= this.msgOutputLevel) {
			Task task = event.getTask();
			final Logger logger = getLogger(event);
			logger.info("Task \"" + task.getTaskName() + "\" started");
		}
	}

	public void taskFinished(BuildEvent event) {
		if (3 <= this.msgOutputLevel) {
			Task task = event.getTask();
			final Logger logger = getLogger(event);
			if (event.getException() == null) {
				logger.info("Task \"" + task.getTaskName() + "\" finished");
			} else {
				logger.error("Task \"" + task.getTaskName() + "\" finished with error", event.getException());
			}
		}
	}

	public void messageLogged(BuildEvent event) {
		int priority = event.getPriority();
		if (priority > this.msgOutputLevel) {
			return;
		}

		final Logger logger = getLogger(event);

		switch (priority) {
		case 0:
			logger.error(event.getMessage());
			break;
		case 1:
			logger.warn(event.getMessage());
			break;
		case 2:
			logger.info(event.getMessage());
			break;
		case 3:
			logger.debug(event.getMessage());
			break;
		case 4:
			logger.trace(event.getMessage());
			break;
		default:
			logger.error(event.getMessage());
		}
	}

	protected Logger getLogger(BuildEvent event) {
		if (categoryHierarchy) {
			final StringBuffer cat = new StringBuffer();
			cat.append("cyrilqc");
			if (null != event.getProject().getName()) {
				cat.append(".");
				cat.append(event.getProject().getName());
			}
			if (null != event.getTarget()) {
				cat.append(".");
				cat.append(event.getTarget().getName());
			}
			if (null != event.getTask()) {
				cat.append(".");
				cat.append(event.getTask().getTaskName());
			}
			return LoggerFactory.getLogger(cat.toString());
		} else {
			final Logger logger;
			if (null != event.getTask()) {
				logger = LoggerFactory.getLogger(event.getTask().getTaskName());
			} else if (null != event.getTarget()) {
				logger = LoggerFactory.getLogger(event.getTarget().getName());
			} else if (null != event.getProject().getName()) {
				logger = LoggerFactory.getLogger(event.getProject().getName());
			} else {
				logger = LoggerFactory.getLogger("cyrilqc");
			}
			return logger;
		}
	}

	public void setMessageOutputLevel(int level) {
		this.msgOutputLevel = level;
	}

	public int getMsgOutputLevel() {
		return msgOutputLevel;
	}

	public void setMsgOutputLevel(int msgOutputLevel) {
		this.msgOutputLevel = msgOutputLevel;
	}

	public void setOutputPrintStream(PrintStream output) {
	}

	public void setErrorPrintStream(PrintStream err) {
	}

	public void setEmacsMode(boolean emacsMode) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	public boolean isCategoryHierarchy() {
		return categoryHierarchy;
	}

	public void setCategoryHierarchy(boolean categoryHierarchy) {
		this.categoryHierarchy = categoryHierarchy;
	}

}
