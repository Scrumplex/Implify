package net.scrumplex.implify.core;

import net.scrumplex.implify.concurrent.ImplifyThreadFactory;
import net.scrumplex.implify.core.exchange.*;
import net.scrumplex.implify.exceptions.ImplifyException;
import net.scrumplex.implify.exceptions.ImplifyExceptionHandler;
import net.scrumplex.implify.lang.HTTPHandler;
import net.scrumplex.implify.lang.HTTPPreprocessor;
import net.scrumplex.implify.lang.RawHandler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;

public class ImplifyServer {

	private final String ip;
	private final int port;
	private final int backlog;
	private final String identifier;

	private boolean running;

	private ImplifyExceptionHandler exceptionHandler;
	private ImplifyThreadFactory threadFactory;
	private RawHandler rawSocketHandler;
	private HTTPPreprocessor httpPreprocessor;
	private HTTPHandler httpHandler;
	private Logger logger;

	private ServerSocket serverSocket;
	private Thread mainThread;

	public ImplifyServer(int port, String identifier) {
		this("0.0.0.0", port, identifier);
	}

	public ImplifyServer(String ip, int port, String identifier) {
		this(ip, port, 1024, identifier);
	}

	public ImplifyServer(String ip, int port, int backlog, String identifier) {
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
		httpPreprocessor = new HTTPDefaultPreprocessor(this);
		try {
			//Default Configuration
			httpHandler = new HTTPFileSystemHandler(this, new File(".").getCanonicalFile());
		} catch (IOException ignored) {
		}
		this.logger = LogManager.getLogger("implify_" + identifier);
	}

	public void start() throws ImplifyException {
		if (running)
			throw new ImplifyException("Instance " + identifier + " already running!");

		try {
			serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(ip));
			running = true;
		} catch (IOException e) {
			getExceptionHandler().caughtException(e, getInstanceIdentifier());
		}

		mainThread = getThreadFactory().newThread(() -> {
			while (true) {
				try {
					Socket socket = serverSocket.accept();
					getThreadFactory().newThread(() -> {
						try {
							HTTPRequest request = getRawSocketHandler().handle(socket);
							if (request == null)
								if (!socket.isClosed())
									socket.close();
							HTTPResponse response = getHttpPreprocessor().process(request);
							if (response == null) {
								response = HTTPUtils.getInternalServerErrorResponse(this, request);
							}
							response = getHttpHandler().handle(request, response);
							if (response == null) {
								response = HTTPUtils.getInternalServerErrorResponse(this, request);
							}

							DataOutputStream out = new DataOutputStream(socket.getOutputStream());

							out.writeBytes("HTTP/1.1 " + response.getStatusCode().getCode() + " " + response.getStatusCode().getCodeName() + "\n");

							for (String headerKey : response.getHeaders().keySet()) {
								//TODO: URL ENCODING?
								String headerValue = response.getHeaders().get(headerKey);
								out.writeBytes(headerKey + ": " + headerValue + "\n");
							}
							out.writeBytes("\n");

							OutputStream dataOut = out;
							if (response.isCompressed()) {
								dataOut = new GZIPOutputStream(out);
							}
							IOUtils.copy(response.getResponseData(), dataOut);
							response.getResponseData().close();
							dataOut.close();
							out.close();
							response.close();
						} catch (ImplifyException | IOException e) {
							getExceptionHandler().caughtException(e, "client_handling");
						}
					}).start();
				} catch (IOException e) {
					getExceptionHandler().caughtException(e, "client_handling");
				}
			}
		}, "implify_" + getInstanceIdentifier());

		mainThread.start();
	}

	public void stop() {
		mainThread.interrupt();
		try {
			if (!serverSocket.isClosed())
				serverSocket.close();
			running = false;
		} catch (IOException e) {
			getExceptionHandler().caughtException(e, "stop_instance_" + identifier);
		}
	}

	public ImplifyExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getInstanceIdentifier() {
		return "instance_" + identifier;
	}

	public ImplifyThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public HTTPPreprocessor getHttpPreprocessor() {
		return httpPreprocessor;
	}

	public void setHttpPreprocessor(HTTPPreprocessor httpPreprocessor) {
		this.httpPreprocessor = httpPreprocessor;
	}

	public RawHandler getRawSocketHandler() {
		return rawSocketHandler;
	}

	public void setRawSocketHandler(RawHandler rawSocketHandler) {
		this.rawSocketHandler = rawSocketHandler;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public boolean isRunning() {
		return running;
	}

	public HTTPHandler getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(HTTPHandler httpHandler) {
		this.httpHandler = httpHandler;
	}
}
