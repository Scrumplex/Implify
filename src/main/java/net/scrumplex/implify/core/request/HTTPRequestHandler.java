package net.scrumplex.implify.core.request;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.lang.Client;
import net.scrumplex.implify.core.lang.HTTPHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;


public class HTTPRequestHandler implements HTTPHandler {

	private final ImplifyServer serverInstance;
	private final File parentDirectory;

	public HTTPRequestHandler(ImplifyServer serverInstance, File parentDirectory) {
		this.serverInstance = serverInstance;
		this.parentDirectory = parentDirectory;
	}

	public void handle(Client client) throws IOException {
		Socket sock = client.getSocket();
		OutputStream out = sock.getOutputStream();
		DataOutputStream headersOut = new DataOutputStream(out);

		//Options {
		boolean compress = false;
		String compressType = ""; //Only GZIP support currently
		//}

		Map<String, String> headers = client.getHeaders();
		if (headers.containsKey("Accept-Encoding")) {
			if (headers.get("Accept-Encoding").contains("gzip")) {
				compress = true;
				compressType = "gzip";
			}
		}

		File f = new File(parentDirectory, client.getRequestPath().substring(1));
		FileInputStream fis = new FileInputStream(f);

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		headersOut.writeBytes("HTTP/1.1 200 OK\n");
		headersOut.writeBytes("Accept-Ranges: bytes\n");
		headersOut.writeBytes("Connection: close\n");
		if (compress)
			headersOut.writeBytes("Content-Encoding: " + compressType + "\n");
		if (!compress)
			headersOut.writeBytes("Content-Length: " + f.length() + "\n");
		headersOut.writeBytes("Content-Type: text/plain; charset=UTF-8\n");
		headersOut.writeBytes("Date: " + dateFormat.format(calendar.getTime()) + "\n");
		headersOut.writeBytes("Server: Implify/1.0\n");
		headersOut.writeBytes("\n");
		headersOut.flush();

		OutputStream fileOut = compress ? new GZIPOutputStream(out) : out;
		IOUtils.copy(fis, fileOut);
		fileOut.close();
		sock.close();
	}
}
