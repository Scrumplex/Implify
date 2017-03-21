package net.scrumplex.implify.core.exchange.socket;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultSocketHandler implements SocketHandler {

	private final Pattern httpPattern;

	public DefaultSocketHandler() {
		this.httpPattern = Pattern.compile("(GET|POST)\\s(.*?)(?:\\?(.*))?\\sHTTP/(\\d\\.\\d)");
	}

	@Override
	public HTTPRequest handle(ImplifyServer serverInstance, Socket socket) throws ImplifyException {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String firstLine = in.readLine();
			if (firstLine == null)
				return null;
			Matcher matcher = httpPattern.matcher(firstLine);
			if (!matcher.matches()) {
				return null;
			}

			Map<String, String> headers = new HashMap<>();
			String line;
			while ((line = in.readLine()) != null) {
				if (line.length() == 0)
					break;

				String[] parts = line.split(":\\s", 2);
				String headerName = parts[0];
				String headerValue = parts[1];
				headers.put(headerName, headerValue);
			}

			HTTPRequest request = new HTTPRequest(serverInstance, socket);
			request.setMethod(matcher.group(1));
			request.setPath(matcher.group(2));
			if (matcher.group(3) != null && matcher.group(3).length() > 0)
				request.setGETParameterString(matcher.group(3));
			request.setHTTPVersion(matcher.group(4));
			request.setHeaders(headers);
			//TODO: Request Body

			return request;
		} catch (Exception e) {
			throw new ImplifyException(e);
		}
	}

}
