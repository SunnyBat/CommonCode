package com.github.sunnybat.commoncode.error;

import java.awt.GraphicsEnvironment;

/**
 * An alternative ExceptionHandler that shows uncaught exceptions in an ErrorWindow.
 *
 * @author SunnyBat
 */
public final class ExceptionHandler implements Thread.UncaughtExceptionHandler {

  /**
   * Creates a new ExceptionHandler instance that creates an ErrorWindow instead of printing to system.err. Note that this CANNOT run in headless
   * environments.
   */
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
