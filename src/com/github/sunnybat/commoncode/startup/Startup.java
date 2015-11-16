package com.github.sunnybat.commoncode.startup;

/**
 * A Statup interface.
 *
 * @author SunnyBat
 */
public interface Startup {

  /**
   * Starts the Statup interface. This tells the program that the interface should be readied to give the user information.
   */
  public void start();

  /**
   * Stops the Startup interface. This tells the program that the interface should be removed from the user's view.
   */
  public void stop();

  /**
   * Sets the status of the program. This should represent what the program is currently doing. This should not be null.
   *
   * @param status The status to set
   */
  public void setStatus(String status);

}
