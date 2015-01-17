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
    ErrorDisplay.showErrorWindow("ERROR", "Uncaught Exception", "This is probably program-breaking. Restart recommended.\nUncaught exception in Thread: " + aThread.getName(), aThrowable);
  }

}
