package com.github.sunnybat.commoncode.error;

/**
 * Class to create new Error Windows. Note that this isn't thread-safe.
 *
 * @author SunnyBat
 */
public class ErrorBuilder {

  private static boolean forceCLine;

  /**
   * Forces all Error Windows that are built to be output to the console instead of displayed.
   */
  public static void forceCommandLine() {
    forceCLine = true;
  }

  private String windowTitle = "Error Information";
  private String errorTitle = "An error has occurred";
  private String errorMessage = ""; // So appendToErrorMessage doesn't throw NPE
  private String buttonText = "Error Information";
  private Throwable error;
  private boolean commandLine;
  private boolean fatalError;
  private boolean built;

  /**
   * Creates a new ErrorBuilder.
   */
  public ErrorBuilder() {
//    if (java.awt.GraphicsEnvironment.isHeadless()) {
//      throw new IllegalStateException("Program is in headless mode.");
//    } // Should instead just display on command-line?
  }

  /**
   * Sets the window title displayed by the Operating System.
   *
   * @param title The String to set the window title to
   * @return The modified ErrorBuilder object
   */
  public ErrorBuilder setWindowTitle(String title) {
    windowTitle = title;
    return this;
  }

  /**
   * Sets the prominent error text displayed within the error window.
   *
   * @param title The String to set the error text to
   * @return The modified ErrorBuilder object
   */
  public ErrorBuilder setErrorTitle(String title) {
    errorTitle = title;
    return this;
  }

  /**
   * Sets the Error Message displayed within the ErrorWindow. Note that this erases any previously set message. Also note that to put in a line break,
   * \n must be used.
   *
   * @param message The message to set
   * @return The modified ErrorBuilder object
   */
  public ErrorBuilder setErrorMessage(String message) {
    errorMessage = message;
    return this;
  }

  /**
   * Appends text to the currently set error message.
   *
   * @param message The message to append
   * @return The modified ErrorBuilder object
   */
  public ErrorBuilder appendToErrorMessage(String message) {
    errorMessage += message;
    return this;
  }

  /**
   * Sets the button text displayed on the Error Window
   *
   * @param text The text to set the button to
   * @return The modified ErrorBuilder object
   * @throws UnsupportedOperationException Because this method is currently unimplemented
   */
  public ErrorBuilder setButtonText(String text) { // Change to public later
    buttonText = text;
    return this;
  }

  /**
   * Sets the error associated with this error window. This will enable the "More Information" button by default.
   *
   * @param error The error to associate
   * @return The modified ErrorBuilder object
   */
  public ErrorBuilder setError(Throwable error) {
    this.error = error;
    return this;
  }

  public ErrorBuilder setCommandLine() {
    this.commandLine = true;
    return this;
  }

  /**
   * Sets the Fatal Error flag to true. Only goes into effect when this Error Window is built. When enabled, the program will be killed once all error
   * windows are closed.<br>
   * This has no effect if command-line has been enabled.
   *
   * @return The modified ErrorBuilder object
   */
  public ErrorBuilder fatalError() {
    fatalError = true;
    return this;
  }

  /**
   * Creates the Error Window and shows it.
   *
   * @throws IllegalStateException If the Error Window has already been built
   */
  public void buildWindow() {
    if (built) {
      throw new IllegalStateException("Error window has already been built.");
    }
    if (commandLine || forceCLine) {
      System.out.println("=== Command-Line Error Display ===");
      System.out.println("Window Title: " + windowTitle);
      System.out.println("Error Title: " + errorTitle);
      System.out.println("Error Message: " + errorMessage);
      if (error != null) {
        System.out.println("Stack Trace:");
        error.printStackTrace();
      }
    } else {
      if (fatalError) {
        ErrorDisplay.fatalError();
      }
      ErrorDisplay.showErrorWindow(windowTitle, errorTitle, errorMessage, buttonText, error);
    }
    built = true;
  }

}
