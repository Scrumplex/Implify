package net.scrumplex.implify.lang;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.net.Socket;

public interface RawHandler {

	HTTPRequest handle(ImplifyServer serverInstance, Socket socket) throws ImplifyException;

}
