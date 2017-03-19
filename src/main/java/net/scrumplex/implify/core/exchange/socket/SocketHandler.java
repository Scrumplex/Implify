package net.scrumplex.implify.core.exchange.socket;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.net.Socket;

public interface SocketHandler {

	HTTPRequest handle(ImplifyServer serverInstance, Socket socket) throws ImplifyException;

}
