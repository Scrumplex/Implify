package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.HTTPUtils;
import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;
import net.scrumplex.implify.lang.HTTPHandler;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class HTTPFileSystemHandler implements HTTPHandler {
	private final ImplifyServer serverInstance;
	private final File parentDirectory;

	public HTTPFileSystemHandler(ImplifyServer serverInstance, File parentDirectory) {
		this.serverInstance = serverInstance;
		this.parentDirectory = parentDirectory;
	}

	public HTTPResponse handle(HTTPRequest request, HTTPResponse response) throws ImplifyException {
		try {
			File f = new File(parentDirectory, request.getRequestPath().substring(1));
			if (f.isDirectory())
				f = new File(f, "index.html");

			if (!f.exists()) {
				response.getHeaders().put("Content-Type", "text/plain; charset=UTF-8");
				response.setStatusCode(HTTPResponse.Code.NOT_FOUND);
				response.setResponseData("FILE NOT FOUND");
				return response;
			}

			response.setStatusCode(HTTPResponse.Code.OK);
			response.setContentLength(f.length());
			response.getHeaders().put("Content-Type", HTTPUtils.getContentTypeFromFile(f) + "; charset=UTF-8");
			response.setResponseData(new FileInputStream(f));
		} catch (IOException e) {
			throw new ImplifyException(e);
		}
		return response;
	}
}
