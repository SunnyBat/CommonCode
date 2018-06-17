package com.github.sunnybat.commoncode.startup;

import java.io.PrintStream;

/**
 *
 * @author SunnyBat
 */
public class LoadingCLI implements Startup {

  private final PrintStream out;

  public LoadingCLI(PrintStream out) {
    this.out = out;
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

  @Override
  public void setStatus(String status) {
    if (out != null) {
      out.println(status);
    }
  }

}
