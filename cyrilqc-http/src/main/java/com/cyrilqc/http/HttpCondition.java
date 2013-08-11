package com.cyrilqc.http;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.condition.Condition;

public class HttpCondition extends ProjectComponent implements Condition {
	private static final int ERROR_BEGINS = 400;
	private static final String DEFAULT_REQUEST_METHOD = "GET";
	private static final int DEFAULT_TIMEOUT = 30000;

	private String spec = null;
	private String requestMethod = DEFAULT_REQUEST_METHOD;

	private int timeout = DEFAULT_TIMEOUT;
	private String outputProperty;

	/**
	 * @return true if the HTTP request succeeds
	 * @exception BuildException
	 *                if an error occurs
	 */
	public boolean eval() throws BuildException {
		if (spec == null) {
			throw new BuildException("No url specified in http condition");
		}
		log("Checking for " + spec, Project.MSG_VERBOSE);
		try {
			final URL url = new URL(spec);
			try {
				final URLConnection conn = url.openConnection();
				if (conn instanceof HttpURLConnection) {
					final HttpURLConnection http = (HttpURLConnection) conn;
					http.setRequestMethod(requestMethod);
					http.setConnectTimeout(timeout);
					http.setReadTimeout(timeout);
					final String userInfo = url.getUserInfo();
					if (userInfo != null) {
						final String userInfoEncoded = new String(Base64.encodeBase64(userInfo.getBytes()));
						http.setRequestProperty("Authorization", "Basic " + userInfoEncoded);
					}
					final int code = http.getResponseCode();
					log("Result code for " + spec + " was " + code, Project.MSG_VERBOSE);
					if (outputProperty != null) {
						final String response = IOUtils.toString(http.getInputStream()).trim();
						getProject().setProperty(outputProperty, response);
						log("Setting output property " + outputProperty, Project.MSG_VERBOSE);
					}
					if (code > 0 && code < errorsBeginAt) {
						return true;
					}
					return false;
				}
			} catch (final java.net.ProtocolException pe) {
				throw new BuildException("Invalid HTTP protocol: "
						+ requestMethod, pe);
			} catch (final java.io.IOException e) {
				return false;
			}
		} catch (final MalformedURLException e) {
			throw new BuildException("Badly formed URL: " + spec, e);
		}
		return true;
	}

	/**
	 * Set the url attribute
	 * 
	 * @param url
	 *            the url of the request
	 */
	public void setUrl(String url) {
		spec = url;
	}

	private int errorsBeginAt = ERROR_BEGINS;

	/**
	 * Set the errorsBeginAt attribute
	 * 
	 * @param errorsBeginAt
	 *            number at which errors begin at, default is 400
	 */
	public void setErrorsBeginAt(int errorsBeginAt) {
		this.errorsBeginAt = errorsBeginAt;
	}

	/**
	 * Sets the method to be used when issuing the HTTP request.
	 * 
	 * @param method
	 *            The HTTP request method to use. Valid values are the same as
	 *            those accepted by the HttpURLConnection.setRequestMetho d()
	 *            method, such as "GET", "HEAD", "TRACE", etc. The default if
	 *            not specified is "GET".
	 * 
	 * @see java.net.HttpURLConnection#setRequestMethod
	 * @since Ant 1.8.0
	 */
	public void setRequestMethod(String method) {
		this.requestMethod = method == null ? DEFAULT_REQUEST_METHOD : method.toUpperCase(Locale.ENGLISH);
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getOutputProperty() {
		return outputProperty;
	}

	public void setOutputProperty(String outputProperty) {
		this.outputProperty = outputProperty;
	}

}
