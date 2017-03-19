package net.scrumplex.implify.exceptions;

import org.jetbrains.annotations.NotNull;

public interface ExceptionHandler extends Thread.UncaughtExceptionHandler {

	void caughtException(Throwable e, String context);

}
