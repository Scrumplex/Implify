package net.scrumplex.implify.core.lang;

import net.scrumplex.implify.core.ImplifyServer;

import java.net.Socket;
import java.util.Map;

public class Client {

	private final ImplifyServer serverInstance;
	private final Socket socket;
	private String requestPath;
	private String requestMethod;
	private String httpVersion;
	private Map<String, String> headers;

	public Client(ImplifyServer serverInstance, java.net.Socket socket) {
		this.serverInstance = serverInstance;
		this.socket = socket;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public Socket getSocket() {
		return socket;
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public ImplifyServer getServerInstance() {
		return serverInstance;
	}
}
