package net.scrumplex.implify.concurrent;

import net.scrumplex.implify.core.ImplifyServer;

import java.util.concurrent.ThreadFactory;

public class ImplifyThreadFactory implements ThreadFactory {

	private ImplifyServer serverInstance;

	public ImplifyThreadFactory(ImplifyServer serverInstance) {
		this.serverInstance = serverInstance;
	}

	public Thread newThread(Runnable r) {
		return newThread(r, null);
	}

	public Thread newThread(Runnable r, String context) {
		Thread t = new Thread(r);
		t.setName(context == null ? "implify_" + t.getId() : context);
		t.setPriority(8);
		t.setUncaughtExceptionHandler(serverInstance.getExceptionHandler());
		return t;
	}
}
