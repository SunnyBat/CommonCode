package com.github.sunnybat.error;

/**
 *
 * @author SunnyBat
 */
public class ErrorDisplay {

  private static byte errorWindowCount = 0;

  /**
   * Displays a window clearly indicating something has gone wrong. This should be used only when the program encounters an error that impedes its
   * function, not for notifications to the user.
   *
   * @param message The error message to display to the user
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
   */
  public static ErrorWindow showErrorWindow(String windowTitle, String title, String message, Throwable t) {
    if (errorWindowCount > 10) {
      System.out.println("Stopped showing error windows -- too many!");
      return null;
    }
    ErrorWindow errorWindow = new ErrorWindow(t);
    errorWindow.setTitleText(windowTitle);
    errorWindow.setErrorText(title);
    errorWindow.setInformationText(message);
    errorWindow.showWindow();
    errorWindowCount++;
    return errorWindow;
  }

  /**
   * Shows the error information of t. It outputs all the information into an {@link Error} window. This should only be accessible from a currently
   * open {@link Error}.
   *
   * @param t The error object
   */
  public static ErrorWindow detailedReport(Throwable t) {
    ErrorWindow errorWindow = new ErrorWindow();
    errorWindow.setTitleText("Error Information");
    errorWindow.setErrorText("StackTrace Information:");
    errorWindow.setLineWrap(false);
    errorWindow.setExtraButtonText("Copy to Clipboard");
    errorWindow.setExtraButtonEnabled(true);
    String message = t.toString() + "\n";
    StackTraceElement[] eE = t.getStackTrace();
    for (int a = 0; a < eE.length; a++) {
      message += "at ";
      message += eE[a];
      message += "\n";
    }
    errorWindow.setInformationText(message);
    errorWindow.showWindow();
    errorWindowCount++;
    System.out.println(t.getMessage());
    t.printStackTrace();
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
