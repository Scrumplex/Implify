package net.scrumplex.implify.exceptions;

public class ImplifyException extends Exception {

	public ImplifyException(String message) {
		super(message);
	}

	public ImplifyException(Exception e) {
		super(e);
	}

	public ImplifyException(String message, Exception e) {
		super(message, e);
	}

}
