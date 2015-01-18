package com.github.sunnybat.commoncode.error;

import java.awt.GraphicsEnvironment;

/**
 * An alternative ExceptionHandler that shows uncaught exceptions in an ErrorWindow.
 *
 * @author SunnyBat
 */
public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {

  public ExceptionHandler() {
    if (GraphicsEnvironment.isHeadless()) {
      throw new IllegalStateException("Program is in headless mode.");
    }
  }

  @Override
  public void uncaughtException(Thread aThread, Throwable aThrowable) {
    ErrorDisplay.fatalError();
    ErrorDisplay.showErrorWindow("ERROR", "Uncaught Exception", "An uncaught exception has occurred in the program. Once all Error windows are closed, the program will exit.\nUncaught exception in Thread: " + aThread.getName(), aThrowable);
  }

}
