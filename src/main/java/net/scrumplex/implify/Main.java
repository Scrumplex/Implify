package net.scrumplex.implify;

import net.scrumplex.implify.core.ImplifyServer;
import net.scrumplex.implify.exceptions.ImplifyException;

public class Main {

	public static void main(String[] args) {
		ImplifyServer implifyServer = new ImplifyServer(8080, "default");
		try {
			implifyServer.start();
		} catch (ImplifyException e) {
			e.printStackTrace();
		}
	}

}
