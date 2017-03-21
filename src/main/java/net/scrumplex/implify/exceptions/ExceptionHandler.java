package net.scrumplex.implify.exceptions;

public interface ExceptionHandler extends Thread.UncaughtExceptionHandler {

	void caughtException(Throwable e, String context);

}
