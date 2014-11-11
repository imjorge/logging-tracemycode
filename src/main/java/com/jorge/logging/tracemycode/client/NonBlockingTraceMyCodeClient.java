package com.jorge.logging.tracemycode.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.jorge.logging.tracemycode.data.ErrorData;
import com.jorge.logging.tracemycode.data.ErrorDataException;

public class NonBlockingTraceMyCodeClient implements TraceMyCodeClient {
	
	private static final String osName = System.getProperty("os.name");
	
	private ExecutorService executorService_ = Executors.newCachedThreadPool();

	private final String clientId_;
	
	private String version_;
	
	private String device_;
	
	private String postUrl_ = "http://tracemycode.net/api/errors";
	
	private String errorDataCharset_ = "utf-8";
	
	private ErrorData prototype_ = null;
	
	
	public NonBlockingTraceMyCodeClient(final String clientId) {
		this(clientId, new Params());
	}

	public NonBlockingTraceMyCodeClient(final String clientId, final Params params) {
		this.clientId_ = clientId;
		this.version_ = params.version;
		this.device_ = params.device;
		if (params.errorDataCharset != null) {
			this.errorDataCharset_ = params.errorDataCharset;
		}
		if (params.postUrl != null) {
			this.postUrl_ = params.postUrl;
		}
	}
	
	public String getErrorDataCharset() {
		return errorDataCharset_;
	}

	public void setErrorDataCharset(String errorDataCharset) {
		this.errorDataCharset_ = errorDataCharset;
	}

	public String getPostUrl() {
		return postUrl_;
	}

	public void setPostUrl(String postUrl) {
		this.postUrl_ = postUrl;
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

	public String getClientId() {
		return clientId_;
	}

	protected ErrorData createFromPrototype(final ErrorDataCallback callback, Throwable... throwables) {
		if (prototype_ == null) {
			final ErrorData errorData = createPrototype();
			prototype_ = errorData;
		}
		final ErrorData errorData = new ErrorData(prototype_);
		errorData.setMessage(throwables.length == 1 ? throwables[0].getMessage() : null);
		for (Throwable e : throwables) {
			errorData.getExceptions().add(ErrorDataException.fromException(e));
		}
		if (callback != null) {
			if (callback.canGetMessage()) {
				errorData.setMessage(callback.getMessage());
			}
			if (callback.canGetCustomFields()) {
				errorData.getCustomFields().putAll(callback.getCustomFields());
			}
		}
		return errorData;
	}

	protected ErrorData createPrototype() {
		final ErrorData errorData = new ErrorData();
		errorData.setProject(clientId_);
		errorData.setVersion(version_);
		InetAddress addr = null;
		if (addr == null) {
			try {
				addr = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		if (addr != null) {
			String hostName = addr.getHostName();
			String hostAddress = addr.getHostAddress();
			if (device_ == null) {
				final StringBuilder builder = new StringBuilder(256);
				builder.append(hostName).append(" [").append(hostAddress).append("]");
				device_ = builder.toString();
			}
			errorData.getCustomFields().put("hostname", hostName);
			errorData.getCustomFields().put("hostaddress", hostAddress);
		}
		errorData.setDevice(device_);
		errorData.setOs(osName);
		return errorData;
	}
	
	protected StatusLine doSubmit(Throwable e, final ErrorDataCallback callback) throws Exception {
		final ErrorData errorData = createFromPrototype(callback, e);
		
		ObjectMapper mapper = new ObjectMapper();
		
		String errorDataJson = null;
		
		StringWriter stringWriter = new StringWriter(256);
		try {
			PrintWriter printWriter = new PrintWriter(stringWriter);
			try {				
				mapper.writerWithDefaultPrettyPrinter().writeValue(printWriter, errorData);
				errorDataJson = stringWriter.toString();
			} finally {
				printWriter.close();
			}
		} finally {
			stringWriter.close();
		}
		
		final HttpClient httpClient = new DefaultHttpClient();
		
		try {	
			final StringEntity entity = new StringEntity(errorDataJson, "utf-8");
			entity.setContentType("application/json");
			
			final HttpPost httpPost = new HttpPost(this.postUrl_);
			httpPost.setEntity(entity);
			
			final HttpResponse response = httpClient.execute(httpPost);
	
			final StatusLine statusLine = response.getStatusLine();
			
			if (statusLine.getStatusCode() != 201) {
				if (statusLine.getStatusCode() == 400) {
					HttpEntity responseEntity = response.getEntity();
					if (responseEntity != null) {
						String responseString = EntityUtils.toString(responseEntity);
						System.out.println(responseString);
					}
				}
			}
			
			return statusLine;
		} finally {
			try {
				httpClient.getConnectionManager().shutdown();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static class Params {
		
		private String version = "unknown";
		
		private String device;
		
		private String errorDataCharset;
		
		private String postUrl;

		public Params setVersion(String version) {
			this.version = version;
			return this;
		}

		public Params setDevice(String device) {
			this.device = device;
			return this;
		}

		public Params setErrorDataCharset(String errorDataCharset) {
			this.errorDataCharset = errorDataCharset;
			return this;
		}

		public Params setPostUrl(String postUrl) {
			this.postUrl = postUrl;
			return this;
		}
		
	}

	public boolean submit(final Throwable e) {
		return submit(e, null);
	}
	
	public boolean submit(final Throwable e, final ErrorDataCallback callback) {
		if (e != null) {
			try {
				executorService_.submit(new Callable<StatusLine>() {

					public StatusLine call() throws Exception {
						return doSubmit(e, callback);
					}
				});
				return true;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	public void close() {
		executorService_.shutdown();
	}

}

