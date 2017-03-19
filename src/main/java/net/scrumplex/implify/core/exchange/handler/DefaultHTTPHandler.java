package net.scrumplex.implify.core.exchange.handler;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;
import net.scrumplex.implify.exceptions.ImplifyException;

public class DefaultHTTPHandler implements HTTPHandler {

	@Override
	public HTTPResponse handle(ImplifyServer serverInstance, HTTPRequest request, HTTPResponse response) throws ImplifyException {
		response.setResponseData(getClass().getResourceAsStream("/demo.html"));
		response.save();
		return response;
	}
}
