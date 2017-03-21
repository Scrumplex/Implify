package net.scrumplex.implify;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.handler.FileSystemHTTPHandler;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.io.File;
import java.util.logging.Level;

class ImplifyDebugging {

	public static void main(String[] args) {
		ImplifyServer implifyServer = new ImplifyServer(8080, "default");
		implifyServer.setLogLevel(Level.ALL);
		implifyServer.setHttpHandler(new FileSystemHTTPHandler(new File("").getAbsoluteFile(), "index.html"));
		try {
			implifyServer.start();
		} catch (ImplifyException e) {
			e.printStackTrace();
		}
	}

}
