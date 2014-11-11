package com.jorge.logging.tracemycode.client;

public interface TraceMyCodeClient {
	
	public boolean submit(final Throwable e);

	public boolean submit(final Throwable e, final ErrorDataCallback callback);
	
	public void close();

}
