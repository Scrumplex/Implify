package net.scrumplex.implify.core.exchange.handler;

import net.scrumplex.implify.core.HTTPUtils;
import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileSystemHTTPHandler implements HTTPHandler {
	private final File parentDirectory;
	private String[] indexFiles;

	public FileSystemHTTPHandler(File parentDirectory, String... indexFiles) {
		this.parentDirectory = parentDirectory;
		this.indexFiles = indexFiles;
	}

	public HTTPResponse handle(ImplifyServer serverInstance, HTTPRequest request, HTTPResponse response) throws ImplifyException {
		try {
			File f = new File(parentDirectory, request.getPath().substring(1));
			if (f.isDirectory()) {
				if(indexFiles.length == 0) {
					f = null;
				}
				for (String indexFile : indexFiles) {
					f = new File(parentDirectory, indexFile);
					if (f.exists())
						break;
				}
			}

			if (f == null || !f.exists()) {
				response.getHeaders().put("Content-Type", "text/plain; charset=UTF-8");
				response.setStatusCode(HTTPResponse.Code.NOT_FOUND);
				response.setResponseData("FILE NOT FOUND");
				response.save();
				return response;
			}

			response.setStatusCode(HTTPResponse.Code.OK);
			response.setContentLength(f.length());
			response.getHeaders().put("Content-Type", HTTPUtils.getContentTypeFromFile(f) + "; charset=UTF-8");
			response.setResponseData(new FileInputStream(f));
			response.save();
		} catch (IOException e) {
			throw new ImplifyException(e);
		}
		return response;
	}
}
