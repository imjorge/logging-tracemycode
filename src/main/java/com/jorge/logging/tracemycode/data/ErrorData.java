package com.jorge.logging.tracemycode.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorData {

	private String project;
	
	private String version;
	
	private String device;
	
	private String os;
	
	private String location;
	
	private String message;
	
	private final List<ErrorDataException> exceptions;
	
	private final Map<String, String> customFields;

	public ErrorData() {
		exceptions = new ArrayList<ErrorDataException>();
		customFields = new HashMap<String, String>();
	}
	
	public ErrorData(ErrorData prototype) {
		this.project = prototype.project;
		this.version = prototype.version;
		this.device = prototype.device;
		this.os = prototype.os;
		this.location = prototype.location;
		this.message = prototype.message;
		this.exceptions = new ArrayList<ErrorDataException>(prototype.exceptions.size());
		for (final ErrorDataException e : prototype.exceptions) {
			this.exceptions.add(new ErrorDataException(e));
		}
		this.customFields = new HashMap<String, String>(prototype.customFields);
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ErrorDataException> getExceptions() {
		return exceptions;
	}

	public Map<String, String> getCustomFields() {
		return customFields;
	}

}
