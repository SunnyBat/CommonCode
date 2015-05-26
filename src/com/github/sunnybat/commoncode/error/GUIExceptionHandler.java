package com.github.sunnybat.commoncode.error;

import java.awt.GraphicsEnvironment;

/**
 * An alternative GUIExceptionHandler that shows uncaught exceptions in an ErrorWindow.
 *
 * @author SunnyBat
 */
public final class GUIExceptionHandler implements Thread.UncaughtExceptionHandler {

  /**
   * Creates a new ExceptionHandler instance that creates an ErrorWindow instead of printing to system.err. Note that this CANNOT run in headless
   * environments. Also note that once this catches an error, it will terminate the program once all Error Windows have been closed.
   */
  public GUIExceptionHandler() {
    if (GraphicsEnvironment.isHeadless()) {
      throw new IllegalStateException("Program is in headless mode.");
    }
  }

  @Override
  public void uncaughtException(Thread aThread, Throwable aThrowable) {
    new ErrorBuilder()
        .setError(aThrowable)
        .setErrorTitle("Fatal Program Error")
        .setErrorMessage("An uncaught exception has occurred in the program. Once all Error windows are"
            + " closed, the program will exit.\nUncaught exception in Thread: " + aThread.getName())
        .buildWindow();
  }

}
