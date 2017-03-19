package net.scrumplex.implify;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.core.exchange.handler.FileSystemHTTPHandler;
import net.scrumplex.implify.exceptions.ImplifyException;

import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		ImplifyServer implifyServer = new ImplifyServer(8080, "default");
		try {
			implifyServer.start();
			implifyServer.setHttpHandler(new FileSystemHTTPHandler(Paths.get("").toFile(), "index.html"));
		} catch (ImplifyException e) {
			e.printStackTrace();
		}
	}

}
