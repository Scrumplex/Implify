package net.scrumplex.implify.core.exchange.preprocess;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.text.SimpleDateFormat;
import java.util.*;


public class DefaultHTTPPreprocessor implements HTTPPreprocessor {

	@Override
	public HTTPResponse process(ImplifyServer serverInstance, HTTPRequest request) throws ImplifyException {
		try {
			HTTPResponse response = new HTTPResponse(serverInstance, request);

			if (!request.getHTTPVersion().equals("1.1")) {
				response.setStatusCode(HTTPResponse.Code.HTTP_VERSION_NOT_SUPPORTED);
				response.setContentType("text/plain");
				response.setResponseData("HTTP VERSION NOT SUPPORTED");
				response.save();
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
			if (response.isCompressed() && serverInstance.isGzipEnabled())
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
