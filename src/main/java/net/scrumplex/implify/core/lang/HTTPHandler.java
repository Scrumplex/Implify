package net.scrumplex.implify.core.lang;

import java.io.IOException;
import java.net.Socket;

public interface HTTPHandler {

	void handle(Client client) throws IOException;

}
