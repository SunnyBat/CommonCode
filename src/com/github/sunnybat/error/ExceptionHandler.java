/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sunnybat.error;

import java.awt.GraphicsEnvironment;

/**
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
    ErrorDisplay.showErrorWindow("ERROR", "Uncaught Exception", "Uncaught exception in Thread: " + aThread.getName(), aThrowable);
  }

}
