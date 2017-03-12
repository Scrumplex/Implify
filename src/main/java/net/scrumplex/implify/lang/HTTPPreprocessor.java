package net.scrumplex.implify.lang;

import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.core.exchange.HTTPResponse;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.io.IOException;

public interface HTTPPreprocessor {

	HTTPResponse process(HTTPRequest request) throws ImplifyException;

}
