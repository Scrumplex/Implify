package net.scrumplex.implify;

import net.scrumplex.implify.core.ImplifyServer;

public class Main {

	public static void main(String[] args) {
		ImplifyServer httpd = new ImplifyServer(8080, 1234);
		httpd.start();


	}

}
