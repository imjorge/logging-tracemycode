package com.jorge.logging.tracemycode.data;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDataException {
	
	private String message;
	
	private String type;
	
	private String stackTrace;

	public ErrorDataException() {
		
	}
	
	public ErrorDataException(ErrorDataException e) {
		this.message = e.message;
		this.type = e.type;
		this.stackTrace = e.stackTrace;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	public static ErrorDataException fromException(final Throwable t) {
		final ErrorDataException result = new ErrorDataException();
		result.setMessage(t.getMessage());
		result.setType(t.getClass().getName());
		final StringWriter s = new StringWriter(256);
		try {
			final PrintWriter p = new PrintWriter(s);
			try {
				t.printStackTrace(p);
				result.setStackTrace(s.toString());
				return result;
			} finally {
				p.close();
			}
		} finally {
			try {
				s.close();
			} catch (IOException ignored) { }
		}
	}

}
