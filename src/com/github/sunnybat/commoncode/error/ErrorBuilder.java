package com.github.sunnybat.commoncode.error;

/**
 *
 * @author SunnyBat
 */
public class ErrorBuilder {

  private final String mainTitle;
  private String windowTitle;
  private String errorMessage;
  private String buttonText;
  private Throwable error;

  /**
   * Creates a new ErrorBuilder and sets the main title text.
   *
   * @param mainTitle The text to set the main title to
   */
  public ErrorBuilder(String mainTitle) {
    this.mainTitle = mainTitle;
  }

  public ErrorBuilder setWindowTitle(String title) {
    return this;
  }

  public ErrorBuilder setErrorMessage(String message) {
    return this;
  }

  public ErrorBuilder appendToErrorMessage(String message) {
    return this;
  }

  public ErrorBuilder setButtonText(String text) {
    return this;
  }

  public void buildWindow() {
    ErrorDisplay.showErrorWindow(windowTitle, mainTitle, errorMessage, error);
  }

}
