package com.github.sunnybat.error;

/**
 * A handler to show Error Windows when an error occurs. This should be the only place
 *
 * @author SunnyBat
 */
public class ErrorDisplay {

  private static byte errorWindowCount = 0;
  public static final int MAX_ERROR_WINDOWS = 10;

  /**
   * Displays a window clearly indicating something has gone wrong. This should be used only when the program encounters an error that impedes its
   * function, not for notifications to the user.
   *
   * @param message The error message to display to the user
   * @return The new ErrorWindow that is created
   */
  public static ErrorWindow showErrorWindow(String message) {
    return showErrorWindow("Error", "ERROR", message, null);
  }

  /**
   * Displays a window clearly indicating something has gone wrong. This should be used only when the program encounters an error that impedes its
   * function, not for notifications to the user.
   *
   * @param message The error message to display to the user
   * @param t The error to display
   * @return The new ErrorWindow that is created
   */
  public static ErrorWindow showErrorWindow(String message, Throwable t) {
    return showErrorWindow("Error", "ERROR", message, t);
  }

  /**
   * Displays a window clearly indicating something has gone wrong. This should be used only when the program encounters an error that impedes its
   * function, not for notifications to the user.
   *
   * @param title The title of the error message
   * @param message The error message to display to the user
   * @param t The error to display
   * @return The new ErrorWindow that is created
   */
  public static ErrorWindow showErrorWindow(String title, String message, Throwable t) {
    return showErrorWindow("Error", title, message, t);
  }

  /**
   * Displays a window clearly indicating something has gone wrong. This should be used only when the program encounters an error that impedes its
   * function, not for notifications to the user.
   *
   * @param windowTitle The title of the window (displayed on the taskbar)
   * @param title The title of the error message
   * @param message The error message to display to the user
   * @param t The error to display
   * @return The new ErrorWindow that is created
   */
  public static ErrorWindow showErrorWindow(String windowTitle, String title, String message, Throwable t) {
    if (errorWindowCount > MAX_ERROR_WINDOWS) {
      System.out.println("Stopped showing error windows -- too many!");
      return null;
    }
    return createErrorWindow(t, windowTitle, title, message);
  }

  /**
   * Shows the error information of t. It outputs all the information into an {@link Error} window. This should only be accessible from a currently
   * open {@link Error}.
   *
   * @param t The error object
   * @return The new ErrorWindow that is created
   */
  public static ErrorWindow detailedReport(Throwable t) {
    // Create StackTrace information for GUI
    StringBuilder message = new StringBuilder(t.toString() + "\n");
    for (StackTraceElement eE1 : t.getStackTrace()) {
      message.append("  at ");
      message.append(eE1);
      message.append("\n");
    }
    ErrorWindow errorWindow = createErrorWindow(t, "Error Information", "StackTrace Information:", message.toString());
    errorWindow.setLineWrap(false);
    errorWindow.setExtraButtonText("Copy to Clipboard");
    errorWindow.setExtraButtonEnabled(true);
    t.printStackTrace();
    return errorWindow;
  }

  private static ErrorWindow createErrorWindow(Throwable t, String title, String error, String message) {
    ErrorWindow errorWindow = new ErrorWindow();
    errorWindow.setTitleText(title);
    errorWindow.setErrorText(error);
    errorWindow.setInformationText(message);
    errorWindow.showWindow();
    errorWindowCount++;
    return errorWindow;
  }

  /**
   * Called when an ErrorWindow is closed.
   */
  protected static void errWindowClosed() {
    errorWindowCount--;
  }

  /**
   * Checks whether or not all error windows have been closed.
   *
   * @return True if all error windows are closed, false if not
   */
  public static boolean areAllClosed() {
    return errorWindowCount == 0;
  }
}
