package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.ImplifyServer;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

	private ImplifyServer serverInstance;
	private Socket socket;
	private String requestPath;
	private String requestMethod;
	private String httpVersion;
	private Map<String, String> headers;
	private HTTPResponse response;

	public HTTPRequest(ImplifyServer serverInstance, Socket socket) {
		this.serverInstance = serverInstance;
		this.socket = socket;
		this.requestPath = "/";
		this.requestMethod = "GET";
		this.httpVersion = "1.1";
		this.headers = new HashMap<>();
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

	public HTTPResponse getResponse() {
		return response;
	}

	public void setResponse(HTTPResponse response) {
		this.response = response;
	}

	public void close() throws IOException {
		if (isClosed())
			socket.close();
		this.serverInstance = null;
		this.socket = null;
		this.requestPath = null;
		this.requestMethod = null;
		this.httpVersion = null;
		this.headers = null;
	}

	public boolean isClosed() {
		return socket == null || socket.isClosed();
	}
}
