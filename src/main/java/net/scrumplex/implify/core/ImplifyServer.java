package net.scrumplex.implify.core;

import net.scrumplex.implify.concurrent.ImplifyThreadFactory;
import net.scrumplex.implify.core.lang.HTTPHandler;
import net.scrumplex.implify.core.lang.RawHandler;
import net.scrumplex.implify.core.request.HTTPRequestHandler;
import net.scrumplex.implify.core.request.RawSocketHandler;
import net.scrumplex.implify.exceptions.ImplifyExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ImplifyServer {

	private final String ip;
	private final int port;
	private final int backlog;
	private final int identifier;

	private ImplifyExceptionHandler exceptionHandler;
	private ImplifyThreadFactory threadFactory;
	private RawHandler rawSocketHandler;
	private HTTPHandler httpHandler;

	private ServerSocket serverSocket;
	private Thread mainThread;

	public ImplifyServer(int port, int identifier) {
		this("0.0.0.0", port, identifier);
	}

	public ImplifyServer(String ip, int port, int identifier) {
		this(ip, port, 1024, identifier);
	}

	public ImplifyServer(String ip, int port, int backlog, int identifier) {
		this.ip = ip;
		this.port = port;
		this.backlog = backlog;
		this.identifier = identifier;
		initialize();
	}

	private void initialize() {
		exceptionHandler = new ImplifyExceptionHandler(this);
		threadFactory = new ImplifyThreadFactory(this);
		rawSocketHandler = new RawSocketHandler(this);
		try {
			httpHandler = new HTTPRequestHandler(this, new File(new File(".").getCanonicalPath()));
		} catch (IOException ignored) {
		}
	}

	public void start() {
		try {
			serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(ip));
		} catch (IOException e) {
			getExceptionHandler().caughtException(e, "");
		}

		mainThread = getThreadFactory().newThread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						getRawSocketHandler().handle(socket);
					} catch (IOException e) {
						e.printStackTrace();
						getExceptionHandler().caughtException(e, "instance_" + identifier);
					}
				}
			}
		}, "implify_instance_" + identifier);

		mainThread.start();
	}

	public void stop() {
		mainThread.interrupt();
		try {
			if (!serverSocket.isClosed())
				serverSocket.close();
		} catch (IOException e) {
			getExceptionHandler().caughtException(e, "stop_instance_" + identifier);
		}
	}

	public ImplifyExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public ImplifyThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public HTTPHandler getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(HTTPHandler httpHandler) {
		this.httpHandler = httpHandler;
	}

	public RawHandler getRawSocketHandler() {
		return rawSocketHandler;
	}

	public void setRawSocketHandler(RawHandler rawSocketHandler) {
		this.rawSocketHandler = rawSocketHandler;
	}
}
