package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;
import net.scrumplex.implify.lang.HTTPPreprocessor;

import java.text.SimpleDateFormat;
import java.util.*;


public class HTTPDefaultPreprocessor implements HTTPPreprocessor {

	@Override
	public HTTPResponse process(ImplifyServer serverInstance, HTTPRequest request) throws ImplifyException {
		try {
			HTTPResponse response = new HTTPResponse(serverInstance, request);

			if (!request.getHttpVersion().equals("1.1")) {
				response.setStatusCode(HTTPResponse.Code.HTTP_VERSION_NOT_SUPPORTED);
				response.setContentType("text/plain");
				response.setResponseData("HTTP VERSION NOT SUPPORTED");
			}

			Map<String, String> requestHeaders = request.getHeaders();
			if (requestHeaders.containsKey("Accept-Encoding") && requestHeaders.get("Accept-Encoding").contains("gzip")) {
				response.setCompressed(true);
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			Map<String, String> responseHeaders = new HashMap<>();
			responseHeaders.put("Accept-Ranges", "bytes");
			responseHeaders.put("Connection", "close"); //TODO: implement keep-alive
			if (response.isCompressed())
				responseHeaders.put("Content-Encoding", "gzip");
			responseHeaders.put("Date", dateFormat.format(calendar.getTime()));
			responseHeaders.put("Server", "Implify/1.0");
			response.setHeaders(responseHeaders);
			return response;
		} catch (Exception e) {
			throw new ImplifyException(e);
		}
	}
}
