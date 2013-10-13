package com.cyrilqc.core;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;

public class SingleCheckAntProject extends Project {
	protected Set<String> succeededTargets = new HashSet<String>();

	@Override
	public void executeSortedTargets(Vector<Target> sortedTargets) throws BuildException {
		BuildException buildException = null; // first build exception
		for (Target curtarget : sortedTargets) {
			if (succeededTargets.contains(curtarget.getName())) {
				continue;
			}

			boolean canExecute = true;
			for (Enumeration<String> depIter = curtarget.getDependencies(); depIter.hasMoreElements();) {
				String dependencyName = depIter.nextElement();
				if (!succeededTargets.contains(dependencyName)) {
					canExecute = false;
					log(curtarget,
							"Cannot execute '" + curtarget.getName() + "' - '"
									+ dependencyName + "' failed or was not executed.",
							MSG_ERR);
					break;
				}
			}
			if (canExecute) {
				Throwable thrownException = null;
				try {
					curtarget.performTasks();
					succeededTargets.add(curtarget.getName());
				} catch (RuntimeException ex) {
					if (!(isKeepGoingMode())) {
						throw ex; // throw further
					}
					thrownException = ex;
				} catch (Throwable ex) {
					if (!(isKeepGoingMode())) {
						throw new BuildException(ex);
					}
					thrownException = ex;
				}
				if (thrownException != null) {
					if (thrownException instanceof BuildException) {
						log(curtarget,
								"Target '" + curtarget.getName()
										+ "' failed with message '"
										+ thrownException.getMessage() + "'.", MSG_ERR);
						// only the first build exception is reported
						if (buildException == null) {
							buildException = (BuildException) thrownException;
						}
					} else {
						log(curtarget,
								"Target '" + curtarget.getName()
										+ "' failed with message '"
										+ thrownException.getMessage() + "'.", MSG_ERR);
						thrownException.printStackTrace(System.err);
						if (buildException == null) {
							buildException =
									new BuildException(thrownException);
						}
					}
				}
			}
		}
		if (buildException != null) {
			throw buildException;
		}
	}

}
