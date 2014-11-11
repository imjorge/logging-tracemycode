package com.jorge.logging.tracemycode.client;

import java.util.Map;

public abstract class ErrorDataCallback {
	
	public boolean canGetMessage() {
		return false;
	}
	
	public String getMessage() {
		return null;
	}
	
	public boolean canGetCustomFields() {
		return false;
	}
	
	public Map<String, String> getCustomFields() {
		return null;
	}
	
}
