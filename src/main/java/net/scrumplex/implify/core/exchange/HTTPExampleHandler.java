package net.scrumplex.implify.core.exchange;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;
import net.scrumplex.implify.lang.HTTPHandler;

public class HTTPExampleHandler implements HTTPHandler {

	@Override
	public HTTPResponse handle(ImplifyServer serverInstance, HTTPRequest request, HTTPResponse response) throws ImplifyException {
		response.setResponseData(getClass().getResourceAsStream("/demo.html"));
		return response;
	}
}
