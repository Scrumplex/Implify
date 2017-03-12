package net.scrumplex.implify.core;

import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class HTTPUtils {

	public static String getContentTypeFromFile(File f) {
		String name = f.getName();

		if (name.endsWith(".js")) {
			return "application/javascript";
		}
		if (name.endsWith(".json")) {
			return "application/json";
		}
		try {
			return Files.probeContentType(f.toPath());
		} catch (IOException ignored) {
		}
		return "application/octet-stream";
	}

	public static String encodeString(String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
		}
		return string;
	}

	public static String decodeString(String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
		}
		return string;
	}

	public static HTTPResponse getInternalServerErrorResponse(ImplifyServer serverInstance, HTTPRequest request) {
		HTTPResponse response = new HTTPResponse(serverInstance, request);
		response.setStatusCode(HTTPResponse.Code.INTERNAL_SERVER_ERROR);
		response.setContentType("text/plain");
		response.setResponseData("Internal Server Error");
		return response;
	}

}
