package net.scrumplex.implify.core.lang;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Sefa Eyeoglu on 10.03.2017.
 */
public interface RawHandler {

	void handle(Socket socket) throws IOException;

}
