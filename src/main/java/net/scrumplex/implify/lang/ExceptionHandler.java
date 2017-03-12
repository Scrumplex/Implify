package net.scrumplex.implify.lang;

public interface ExceptionHandler extends Thread.UncaughtExceptionHandler {

	void caughtException(Throwable e, String context);

}
