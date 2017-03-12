package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;
import net.scrumplex.implify.lang.RawHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawSocketHandler implements RawHandler {

	private final ImplifyServer serverInstance;
	private final Pattern httpPattern;

	public RawSocketHandler(ImplifyServer serverInstance) {
		this.serverInstance = serverInstance;
		this.httpPattern = Pattern.compile("(GET|POST)\\s(.*)\\sHTTP\\/(\\d\\.\\d)");
	}

	@Override
	public HTTPRequest handle(Socket socket) throws ImplifyException {
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
			request.setRequestMethod(matcher.group(1));
			request.setRequestPath(matcher.group(2));
			request.setHttpVersion(matcher.group(3));
			request.setHeaders(headers);
			//TODO: Request Body

			return request;
		} catch (Exception e) {
			throw new ImplifyException(e);
		}
	}

}
