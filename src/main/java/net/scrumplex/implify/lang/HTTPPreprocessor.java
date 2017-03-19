package net.scrumplex.implify.lang;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;
import net.scrumplex.implify.exceptions.ImplifyException;

public interface HTTPPreprocessor {

	HTTPResponse process(ImplifyServer serverInstance, HTTPRequest request) throws ImplifyException;

}
