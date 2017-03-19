package net.scrumplex.implify.exceptions;

import org.jetbrains.annotations.NotNull;

public class ImplifyException extends Exception {

	public ImplifyException(@NotNull String message) {
		super(message);
	}

	public ImplifyException(@NotNull Exception e) {
		super(e);
	}

	public ImplifyException(@NotNull String message, Exception e) {
		super(message, e);
	}

}
