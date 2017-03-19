package net.scrumplex.implify.core;

import net.scrumplex.implify.concurrent.ImplifyThreadFactory;
import net.scrumplex.implify.core.exchange.*;
import net.scrumplex.implify.core.exchange.handler.DefaultHTTPHandler;
import net.scrumplex.implify.core.exchange.preprocess.DefaultHTTPPreprocessor;
import net.scrumplex.implify.core.exchange.socket.DefaultSocketHandler;
import net.scrumplex.implify.exceptions.ImplifyException;
import net.scrumplex.implify.exceptions.ImplifyExceptionHandler;
import net.scrumplex.implify.exceptions.ExceptionHandler;
import net.scrumplex.implify.core.exchange.handler.HTTPHandler;
import net.scrumplex.implify.core.exchange.preprocess.HTTPPreprocessor;
import net.scrumplex.implify.core.exchange.socket.SocketHandler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
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

	private ImplifyThreadFactory threadFactory;
	private ExceptionHandler exceptionHandler;
	private SocketHandler socketHandler;
	private HTTPPreprocessor httpPreprocessor;
	private HTTPHandler httpHandler;
	private Logger logger;

	private ServerSocket serverSocket;
	private Thread mainThread;

	public ImplifyServer(int port, @NotNull String identifier) {
		this("0.0.0.0", port, identifier);
	}

	public ImplifyServer(@NotNull String ip, int port, @NotNull String identifier) {
		this(ip, port, 1024, identifier);
	}

	public ImplifyServer(@NotNull String ip, int port, int backlog, @NotNull String identifier) {
		this.ip = ip;
		this.port = port;
		this.backlog = backlog;
		this.identifier = identifier;
		exceptionHandler = new ImplifyExceptionHandler(this);
		threadFactory = new ImplifyThreadFactory(this);
		//May be changed by user if needed
		socketHandler = new DefaultSocketHandler();
		httpPreprocessor = new DefaultHTTPPreprocessor();
		//Should be changed by user
		httpHandler = new DefaultHTTPHandler();

		this.logger = LogManager.getLogger("implify_" + identifier);
	}

	/**
	 * Starts the current Implify instance. It will start a new thread, which will start other threads if needed.
	 * Exceptions will be handled by an {@link ExceptionHandler}, which can be changed with {@link ImplifyServer#setExceptionHandler(ExceptionHandler)}
	 *
	 * @throws ImplifyException if instance already running or an I/O error occurs.
	 */
	public void start() throws ImplifyException {
		if (running)
			throw new ImplifyException("Instance " + identifier + " already running!");

		try {
			serverSocket = new ServerSocket(port, backlog, InetAddress.getByName(ip));
			running = true;
		} catch (IOException e) {
			throw new ImplifyException(e);
		}

		mainThread = getThreadFactory().newThread(() -> {
			while (running) {
				try {
					Socket socket = serverSocket.accept();
					getThreadFactory().newThread(() -> {
						try {
							HTTPRequest request = getSocketHandler().handle(this, socket);
							if (request == null) {
								if (!socket.isClosed())
									socket.close();
								return;
							}

							HTTPResponse response = getHttpPreprocessor().process(this, request);
							if (response == null)
								response = HTTPUtils.getInternalServerErrorResponse(this, request);

							if (!response.isSaved()) {
								response = getHttpHandler().handle(this, request, response);
								if (response == null)
									response = HTTPUtils.getInternalServerErrorResponse(this, request);
								if(!response.isSaved())
									response.save();
							}

							//Send to client
							DataOutputStream out = new DataOutputStream(socket.getOutputStream());

							out.writeBytes("HTTP/1.1 " + response.getStatusCode().getCode() + " " + response.getStatusCode().getCodeName() + "\n");

							for (String headerKey : response.getHeaders().keySet()) {
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

	/**
	 * Stops current implify instance.
	 *
	 * @throws ImplifyException if instance not running or an I/O error occurs.
	 */
	public void stop() throws ImplifyException {
		if (!running)
			throw new ImplifyException("Instance " + identifier + " not running!");
		mainThread.interrupt();
		try {
			if (!serverSocket.isClosed())
				serverSocket.close();
			running = false;
		} catch (IOException e) {
			throw new ImplifyException(e);
		}
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	private void setExceptionHandler(@NotNull ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
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

	public void setHttpPreprocessor(@NotNull HTTPPreprocessor httpPreprocessor) {
		this.httpPreprocessor = httpPreprocessor;
	}

	public SocketHandler getSocketHandler() {
		return socketHandler;
	}

	public void setSocketHandler(@NotNull SocketHandler socketHandler) {
		this.socketHandler = socketHandler;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(@NotNull Logger logger) {
		this.logger = logger;
	}

	public boolean isRunning() {
		return running;
	}

	public HTTPHandler getHttpHandler() {
		return httpHandler;
	}

	public void setHttpHandler(@NotNull HTTPHandler httpHandler) {
		this.httpHandler = httpHandler;
	}
}
