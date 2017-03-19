package net.scrumplex.implify.exceptions;

import net.scrumplex.implify.core.ImplifyServer;

public class ImplifyExceptionHandler implements ExceptionHandler {

	private final ImplifyServer serverInstance;

	public ImplifyExceptionHandler(ImplifyServer serverInstance) {
		this.serverInstance = serverInstance;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		serverInstance.getLogger().error("An error occurred in thread " + t.getName());
		e.printStackTrace();
	}

	public void caughtException(Throwable e, String context) {
		serverInstance.getLogger().error("An error occurred in context " + context);
		e.printStackTrace();
	}
}
