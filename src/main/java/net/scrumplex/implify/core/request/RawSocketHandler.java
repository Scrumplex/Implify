package net.scrumplex.implify.core.request;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.lang.Client;
import net.scrumplex.implify.core.lang.RawHandler;

import java.io.BufferedReader;
import java.io.IOException;
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
		this.httpPattern = Pattern.compile("(GET|POST)\\s(.*)HTTP\\/(\\d\\.\\d)");
	}

	public void handle(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		String firstLine = in.readLine();
		Matcher matcher = httpPattern.matcher(firstLine);
		if (!matcher.matches()) {
			socket.close();
			return;
		}

		if (!matcher.group(3).equals("1.1")) {
			//TODO: HTTP Version unsupported HTTP505 error
			socket.close();
			return;
		}

		Map<String, String> headers = new HashMap<>();
		String line;
		while ((line = in.readLine()) != null) {
			if (line.length() == 0)
				break;

			String[] parts = line.split(":\\s");
			String headerName = parts[0];
			String headerValue = parts[1];
			headers.put(headerName, headerValue);
		}

		Client client = new Client(serverInstance, socket);
		client.setRequestMethod(matcher.group(1));
		client.setRequestPath(matcher.group(2));
		client.setHttpVersion(matcher.group(3));
		client.setHeaders(headers);
		//TODO: Request Body
		serverInstance.getHttpHandler().handle(client);
	}

}
