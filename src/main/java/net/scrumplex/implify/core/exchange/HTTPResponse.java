package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {

	private ImplifyServer serverInstance;
	private HTTPRequest request;
	private Map<String, String> headers;
	private Code statusCode;
	private boolean compressed;
	private InputStream responseData;
	private boolean saved;

	public HTTPResponse(@NotNull ImplifyServer serverInstance, @NotNull HTTPRequest request) {
		this.serverInstance = serverInstance;
		this.request = request;
		if (request.getServerInstance() != serverInstance)
			throw new RuntimeException(new ImplifyException("Response does not belong to the specified HTTPRequest"));
		request.setResponse(this);
		this.headers = new HashMap<>();
		this.statusCode = Code.INTERNAL_SERVER_ERROR;
		this.compressed = false;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(@NotNull Map<String, String> headers) {
		this.headers = headers;
	}

	public long getContentLength() {
		return Long.parseLong(getHeaders().get("Content-Length"));
	}

	public void setContentLength(long length) {
		if (isSaved())
			return;
		if (isCompressed())
			return;
		getHeaders().put("Content-Length", String.valueOf(length));
	}

	public long getContentType() {
		return Long.parseLong(getHeaders().get("Content-Type"));
	}

	public void setContentType(@NotNull String type) {
		if (isSaved())
			return;
		getHeaders().put("Content-Type", type);
	}

	public Code getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		Code c = Code.fromStatusCodeNumber(statusCode);
		if (c != null)
			setStatusCode(c);
		else
			throw new NullPointerException("Status code " + statusCode + " not recognized.");
	}

	public void setStatusCode(@NotNull Code statusCode) {
		if (isSaved() || isClosed())
			return;
		this.statusCode = statusCode;
	}

	public boolean isCompressed() {
		return compressed;
	}

	public void setCompressed(boolean compressed) {
		if (isSaved() || isClosed())
			return;
		this.compressed = compressed;
	}

	public HTTPRequest getRequest() {
		return request;
	}

	public ImplifyServer getServerInstance() {
		return serverInstance;
	}

	public InputStream getResponseData() {
		return responseData;
	}

	public void setResponseData(@NotNull String responseData) {
		if (isSaved() || isClosed())
			return;
		setResponseData(responseData.getBytes());
	}

	public void setResponseData(@NotNull InputStream responseData) {
		if (isSaved() || isClosed())
			return;
		this.responseData = responseData;
	}

	public void setResponseData(@NotNull byte[] responseData) {
		if (isSaved() || isClosed())
			return;
		setContentLength(responseData.length);
		setResponseData(new ByteArrayInputStream(responseData));
	}

	public void close() throws IOException {
		request.close();

		this.serverInstance = null;
		this.request = null;
		this.headers = null;
		this.statusCode = null;
		this.compressed = false;
	}

	public boolean isClosed() {
		return request == null || request.isClosed();
	}

	public boolean isSaved() {
		return saved;
	}

	public void save() {
		this.saved = true;
	}

	public enum Code {
		CONTINUE(100, "Continue"),
		SWITCHING_PROTOCOLS(101, "Switching Protocols"),
		OK(200, "OK"),
		CREATED(201, "Created"),
		ACCEPTED(202, "Accepted"),
		NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
		NO_CONTENT(204, "No Content"),
		RESET_CONTENT(205, "Reset Content"),
		PARTIAL_CONTENT(206, "Partial Content"),
		MULTIPLE_CHOICES(300, "Multiple Choices"),
		MOVED_PERMANENTLY(301, "Moved Permanently"),
		FOUND(302, "Found"),
		SEE_OTHER(303, "See Other"),
		NOT_MODIFIED(304, "Not Modified"),
		USE_PROXY(305, "Use Proxy"),
		TEMPORARY_REDIRECT(307, "Temporary Redirect"),
		BAD_REQUEST(400, "Bad Request"),
		UNAUTHORIZED(401, "Unauthorized"),
		PAYMENT_REQUIRED(402, "Payment Required"),
		FORBIDDEN(403, "Forbidden"),
		NOT_FOUND(404, "Not Found"),
		METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
		NOT_ACCEPTABLE(406, "Not Acceptable"),
		PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
		REQUEST_TIMEOUT(408, "Request Time-out"),
		CONFLICT(409, "Conflict"),
		GONE(410, "Gone"),
		LENGTH_REQUIRED(411, "Length Required"),
		PRECONDITION_FAILED(412, "Precondition Failed"),
		REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
		REQUEST_URI_TOO_LARGE(414, "Request-URI Too Large"),
		UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
		REQUESTED_RANGE_NOT_SATISFABLE(416, "Requested range not satisfiable"),
		EXPECTATION_FAILED(417, "Expectation Failed"),
		INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
		NOT_IMPLEMENTED(501, "Not Implemented"),
		BAD_GATEWAY(502, "Bad Gateway"),
		SERVICE_UNAVAILABLE(503, "Service Unavailable"),
		GATEWAY_TIMEOUT(504, "Gateway Time-out"),
		HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version not supported");

		private final int code;
		private final String codeName;

		Code(int code, String codeName) {
			this.code = code;
			this.codeName = codeName;
		}

		public static Code fromStatusCodeNumber(int statusCode) {
			for (Code code : Code.values()) {
				if (code.getCode() == statusCode)
					return code;
			}
			return null;
		}

		public String getCodeName() {
			return codeName;
		}

		public int getCode() {
			return code;
		}
	}
}
