package net.scrumplex.implify.exceptions;

import net.scrumplex.implify.core.ImplifyServer;

import java.util.logging.Level;

public class ImplifyExceptionHandler implements ExceptionHandler {

	private final ImplifyServer serverInstance;

	public ImplifyExceptionHandler(ImplifyServer serverInstance) {
		this.serverInstance = serverInstance;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		serverInstance.getLogger().log(Level.SEVERE, "An error occurred in thread " + t.getName());
		e.printStackTrace();
	}

	public void caughtException(Throwable e, String context) {
		serverInstance.getLogger().log(Level.SEVERE, "An error occurred in context " + context);
		e.printStackTrace();
	}
}
