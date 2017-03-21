package net.scrumplex.implify;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.io.File;
import java.util.logging.Level;

public class StartingWithoutConfiguration {

	public static void main(String[] args) {
		ImplifyServer implifyServer = new ImplifyServer(8080, "default");
		implifyServer.setLogLevel(Level.ALL);
		try {
			//Will not pause thread
			implifyServer.start();
		} catch (ImplifyException e) {
			e.printStackTrace();
		}
	}

}
