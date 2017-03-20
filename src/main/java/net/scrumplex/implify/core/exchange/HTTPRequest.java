package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.HTTPUtils;
import net.scrumplex.implify.core.ImplifyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {

	private ImplifyServer serverInstance;
	private Socket socket;
	private String path;
	private Method method;
	private String httpVersion;
	private Map<String, String> headers;
	private Map<String, String> getParameters;
	private HTTPResponse response;

	public HTTPRequest(ImplifyServer serverInstance, Socket socket) {
		this.serverInstance = serverInstance;
		this.socket = socket;
		this.path = "/";
		this.method = Method.GET;
		this.httpVersion = "1.1";
		this.headers = new HashMap<>();
	}

	public String getPath() {
		return path;
	}

	/**
	 * Setter method for field path. It will HTTP decode the given path.
	 *
	 * @param path requested path
	 */
	public void setPath(@NotNull String path) {
		this.path = HTTPUtils.decodeString(path);
	}

	/**
	 * Getter method for method.
	 *
	 * @return method as {@link java.lang.String}
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * Setter method for field method. This wil wrap the String with the {@link HTTPRequest.Method} class.
	 *
	 * @param method method used to request
	 * @see HTTPRequest#setMethod(Method)
	 */
	public void setMethod(@NotNull String method) {
		Method m = Method.fromName(method);
		if (m != null)
			setMethod(m);
		else
			throw new NullPointerException("Method " + method + " not recognized.");
	}

	/**
	 * Setter method for field method.
	 *
	 * @param method HTTP Method used for the request
	 */
	public void setMethod(@NotNull Method method) {
		this.method = method;
	}

	/**
	 * Getter method for socket.
	 *
	 * @return socket as {@link java.net.Socket}
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Getter method for httpVersion.
	 *
	 * @return httpVersion as {@link java.lang.String}
	 */
	public String getHTTPVersion() {
		return httpVersion;
	}

	/**
	 * Setter method for field httpVersion.
	 *
	 * @param httpVersion HTTP Version used by client
	 */
	public void setHTTPVersion(@NotNull String httpVersion) {
		this.httpVersion = httpVersion;
	}

	/**
	 * Getter method for headers.
	 *
	 * @return headers as {@link java.util.Map}
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * Setter method for field headers.
	 *
	 * @param headers Headers sent by client
	 */
	public void setHeaders(@Nullable Map<String, String> headers) {
		if (headers == null)
			this.headers = new HashMap<>();
		this.headers = headers;
	}

	public ImplifyServer getServerInstance() {
		return serverInstance;
	}

	public HTTPResponse getResponse() {
		return response;
	}

	public void setResponse(@NotNull HTTPResponse response) {
		this.response = response;
	}

	/**
	 * Closes the HTTPRequest and if not already closed the socket.
	 *
	 * @throws IOException if an I/O error occurs when closing this socket.
	 */
	public void close() throws IOException {
		if (!isClosed())
			socket.close();
		this.serverInstance = null;
		this.socket = null;
		this.path = null;
		this.method = null;
		this.httpVersion = null;
		this.headers = null;
	}

	/**
	 * Checks if the request is closed.
	 *
	 * @return if HTTPRequest was closed by {@link HTTPRequest#close()}.
	 */
	public boolean isClosed() {
		return socket == null || socket.isClosed();
	}

	public Map<String, String> getGETParameters() {
		return getParameters;
	}

	public void setGETParameterString(@NotNull String parameterString) {
		this.getParameters = HTTPUtils.parseParameterString(parameterString);
	}

	public enum Method {
		GET("GET"),
		POST("POST");

		private String method;

		Method(String method) {
			this.method = method;
		}

		/**
		 * Returns the method's name in uppercase.
		 * <p>
		 * Example:
		 * Method.GET.method = "GET";
		 *
		 * @return the method's name in uppercase.
		 */
		public String getMethodName() {
			return method;
		}

		/**
		 * Returns the Method from given methodName.
		 *
		 * @param name method's name
		 * @return Method
		 * @see Method#getMethodName()
		 */
		public static Method fromName(String name) {
			for (Method m : Method.values()) {
				if (m.getMethodName().equalsIgnoreCase(name))
					return m;
			}
			return null;
		}
	}
}
