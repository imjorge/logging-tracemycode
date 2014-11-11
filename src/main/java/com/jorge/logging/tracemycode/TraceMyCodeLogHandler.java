package com.jorge.logging.tracemycode;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import com.jorge.logging.tracemycode.client.ErrorDataCallback;
import com.jorge.logging.tracemycode.client.NonBlockingTraceMyCodeClient;
import com.jorge.logging.tracemycode.client.TraceMyCodeClient;
import com.jorge.logging.tracemycode.client.NonBlockingTraceMyCodeClient.Params;

public class TraceMyCodeLogHandler extends Handler {
	
	private TraceMyCodeClient client;

	private String clientId_;
	
	private String version_;
	
	private String device_;
	
	private String postUrl_ = "http://tracemycode.net/api/errors";
	
	private String errorDataCharset_ = "utf-8";

	public String getClientId() {
		return clientId_;
	}

	public void setClientId(String clientId) {
		this.clientId_ = clientId;
	}

	public String getVersion() {
		return version_;
	}

	public void setVersion(String version) {
		this.version_ = version;
	}

	public String getDevice() {
		return device_;
	}

	public void setDevice(String device) {
		this.device_ = device;
	}

	public String getPostUrl() {
		return postUrl_;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl_ = postUrl;
	}

	public String getErrorDataCharset() {
		return errorDataCharset_;
	}

	public void setErrorDataCharset(String errorDataCharset) {
		this.errorDataCharset_ = errorDataCharset;
	}

	protected TraceMyCodeClient getTraceMyCodeClient() {
		if (client == null) {
			client = new NonBlockingTraceMyCodeClient(clientId_,
						new Params()
							.setDevice(device_)
							.setErrorDataCharset(errorDataCharset_)
							.setPostUrl(postUrl_)
							.setVersion(version_));
		}
		return client;
	}

	@Override
	public void publish(final LogRecord record) {
		if (record.getThrown() != null) {
			getTraceMyCodeClient().submit(record.getThrown(), new ErrorDataCallback() {
				
				public boolean canGetMessage() {
					return true;
				}
				
				public String getMessage() {
					return record.getMessage();
				}
				
				@Override
				public boolean canGetCustomFields() {
					return true;
				}
				
				@Override
				public Map<String, String> getCustomFields() {
					final Map<String, String> c = new HashMap<String, String>(10, 1);
					c.put("loggerName", record.getLoggerName());
					c.put("resourceBundleName", record.getResourceBundleName());
					c.put("sourceClassName", record.getSourceClassName());
					c.put("sourceMethodName", record.getSourceMethodName());
					c.put("level", record.getLevel() == null ? null : record.getLevel().toString());
					c.put("threadID", String.valueOf(record.getThreadID()));
					return c;
				}
				
			});
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
		if (client != null) {
			client.close();
		}
	}

}
