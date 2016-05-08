package com.github.sunnybat.commoncode.utilities;

import java.io.IOException;

/**
 *
 * @author SunnyBat
 */
public class IPAddress {

  private static final String CHECK_IP_SITE = "http://checkip.amazonaws.com";

  /**
   * Gets the internal IP address of the given machine.
   *
   * @return The visible IP address, or [Not Found] if unable to find it
   */
  public static String getInternalIP() {
    try {
      return java.net.Inet4Address.getLocalHost().getHostAddress();
    } catch (IOException e) {
      return "[Not Found]";
    }
  }

  /**
   * Gets the external IP address of the given machine.
   *
   * @return The visible IP address, or [Not Found] if unable to find it
   */
  public static String getExternalIP() {
    // Credit to StackOverflow user bakkal
    try {
      java.net.URL whatismyip = new java.net.URL(CHECK_IP_SITE);
      java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(whatismyip.openStream()));
      return in.readLine(); // Only line returned is the IP
    } catch (IOException e) {
      return "[Not Found]";
    }
  }

}
