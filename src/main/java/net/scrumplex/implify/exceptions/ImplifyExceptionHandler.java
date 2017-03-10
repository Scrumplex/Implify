package net.scrumplex.implify.exceptions;

import net.scrumplex.implify.core.ImplifyServer;

public class ImplifyExceptionHandler implements Thread.UncaughtExceptionHandler {

	private final ImplifyServer serverInstance;

	public ImplifyExceptionHandler(ImplifyServer serverInstance) {
		this.serverInstance = serverInstance;
	}


	public void uncaughtException(Thread t, Throwable e) {

	}

	public void caughtException(Throwable e, String context) {

	}

}
