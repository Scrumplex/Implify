package net.scrumplex.implify.lang;

import net.scrumplex.implify.core.exchange.HTTPRequest;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.net.Socket;

public interface RawHandler {

	HTTPRequest handle(Socket socket) throws ImplifyException;

}
