package com.github.sunnybat.commoncode.utilities;

/**
 * A utility to track the amount of data used.
 *
 * @author SunnyBat
 */
public class DataTracker {

  private long dataUsed;
  private long startTime;

  /**
   * Sets the start time for this DataTracker to the current system's time in milliseconds.
   */
  public synchronized void setStartTime() {
    this.startTime = System.currentTimeMillis();
  }

  /**
   * Sets the start time for this DataTracker to the given start time.
   *
   * @param startTime The start time to use
   */
  public synchronized void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * Gets this DataTracker's start time, or 0 if not set
   *
   * @return The start time
   */
  public synchronized long getStartTime() {
    return startTime;
  }

  /**
   * Adds an amount of data (in bytes) used by the program. This should be called whenever a network connection is made.
   *
   * @param data The amount of data (in bytes) to add to the total data used
   */
  public synchronized void addDataUsed(long data) {
    dataUsed += data;
  }

  /**
   * Gets the amount of data (in bytes) used by the program.
   *
   * @return The amount of data (in bytes) used by the program
   */
  public synchronized long getDataUsed() {
    return dataUsed;
  }

  /**
   * Gets the amount of data in megabytes used by the program. Note that the double only extends out two decimal places.
   *
   * @return The amount of data in megabytes used by the program
   */
  public synchronized double getDataUsedMB() {
    return (double) ((int) ((double) getDataUsed() / 1024 / 1024 * 100)) / 100; // *100 to make the double have two extra numbers, round with typecasting to integer, then divide that by 100 and typecast to double to get a double with two decimal places
  }
}
