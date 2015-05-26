package com.github.sunnybat.commoncode.error;

/**
 * A handler to show Error Windows when an error occurs. This should be the only place that program error windows are shown.
 *
 * @author SunnyBat
 */
class ErrorDisplay {

  private static boolean isFatalError;
  private static byte errorWindowCount = 0;
  /**
   * The maximum amount of Error Windows open at one time. Any extra calls to showErrorWindow() will be ignored until there are less than this amount
   * open.
   */
  public static final int MAX_ERROR_WINDOWS = 10;

  /**
   * Displays a window clearly indicating something has gone wrong. This should be used only when the program encounters an error that impedes its
   * function, not for notifications to the user.
   *
   * @param windowTitle The title of the window (displayed on the taskbar)
   * @param title The title of the error message
   * @param message The error message to display to the user
   * @param t The error to display, or null if none
   * @throws IllegalArgumentException If any String arguments are null
   */
  static void showErrorWindow(String windowTitle, String title, String message, String buttonText, Throwable t) {
    if (errorWindowCount >= MAX_ERROR_WINDOWS) {
      System.out.println("Stopped showing error windows -- too many!");
      return;
    }
    if (windowTitle == null || title == null || message == null || buttonText == null) {
      throw new IllegalArgumentException("Cannot supply null arguments (except t)");
    }
    ErrorWindow errorWindow = createErrorWindow(t);
    errorWindow.setWindowTitle(windowTitle);
    errorWindow.setErrorText(title);
    errorWindow.setInformationText(message);
    errorWindow.setExtraButtonText(buttonText);
    errorWindow.showWindow();
  }

  /**
   * Shows the error information of t. It outputs all the information into an {@link Error} window. This should only be accessible from a currently
   * open {@link Error}.
   *
   * @param t The error object
   */
  protected static void detailedReport(Throwable t) {
    if (t == null) {
      throw new IllegalArgumentException("Throwable Object cannot be null.");
    }
    // Create StackTrace information for GUI
    StringBuilder message = new StringBuilder(t.toString() + "\n\n"); // Second \n almost purely for Reddit
    for (StackTraceElement eE1 : t.getStackTrace()) {
      message.append("    at ");
      message.append(eE1);
      message.append("\n");
    }
    ErrorWindow errorWindow = createErrorWindow(null);
    errorWindow.setWindowTitle("Error Information");
    errorWindow.setErrorText("StackTrace Information");
    errorWindow.setInformationText(message.toString());
    errorWindow.setLineWrap(false);
    errorWindow.setExtraButtonText("Copy to Clipboard");
    errorWindow.setExtraButtonEnabled(true);
    errorWindow.showWindow();
  }

  /**
   * Creates a new ErrorWindow. Note that this does not automatically display the ErrorWindow.
   *
   * @param t The Throwable object to associate with this ErrorWindow, or null if none
   * @param title The window title to use
   * @param error The main error to display
   * @param message The error message to display
   * @return The Errorwindow Object with these parameters
   */
  private static ErrorWindow createErrorWindow(Throwable t) {
    ErrorWindow errorWindow = new ErrorWindow(t);
    errorWindowCount++;
    return errorWindow;
  }

  /**
   * Called when an ErrorWindow is closed.
   */
  protected static void errWindowClosed() {
    errorWindowCount--;
    if (isFatalError && areAllClosed()) {
      System.out.println("ErrorDisplay: All error windows closed, exiting program.");
      System.exit(1);
    }
  }

  /**
   * Checks whether or not all error windows have been closed.
   *
   * @return True if all error windows are closed, false if not
   */
  public static boolean areAllClosed() {
    return errorWindowCount == 0;
  }

  /**
   * Sets the Fatal Error flag to true. Once called, the program will be killed once all error windows are closed. Note that this will not have any
   * effect if no Error Windows are subsequently displayed.
   */
  public static void fatalError() {
    isFatalError = true;
  }
}
