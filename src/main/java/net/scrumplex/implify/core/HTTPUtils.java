package net.scrumplex.implify.core;

import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HTTPUtils {

	public static String getContentTypeFromFile(@NotNull File f) {
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

	public static String encodeString(@NotNull String string) {
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
		}
		return string;
	}

	public static String decodeString(@NotNull String string) {
		try {
			return URLDecoder.decode(string, "UTF-8");
		} catch (UnsupportedEncodingException ignored) {
		}
		return string;
	}

	public static Map<String, String> parseParameterString(@NotNull String parameterString) {
		Map<String, String> parameters = new HashMap<>();
		if (parameterString.contains("&")) {
			//Multiple parameters
			String[] params = parameterString.split("&");
			for (String param : params) {
				if(param.contains("=")) {
					String[] parts = param.split("=", 2);
					parameters.put(parts[0], HTTPUtils.decodeString(parts[1]));
				} else {
					parameters.put(param, null);
				}
			}
		} else {
			//Just one parameter
			if(parameterString.contains("=")) {
				String[] parts = parameterString.split("=", 2);
				parameters.put(parts[0], HTTPUtils.decodeString(parts[1]));
			} else {
				parameters.put(parameterString, null);
			}
		}
		return parameters;
	}

	public static HTTPResponse getInternalServerErrorResponse(@NotNull ImplifyServer serverInstance, @NotNull HTTPRequest request) {
		HTTPResponse response = new HTTPResponse(serverInstance, request);
		response.setStatusCode(HTTPResponse.Code.INTERNAL_SERVER_ERROR);
		response.setContentType("text/plain");
		response.setResponseData("Internal Server Error");
		response.save();
		return response;
	}

}
