package com.github.sunnybat.commoncode.email.smtp;

/**
 * Stores information about a given email address.
 *
 * @author SunnyBat
 */
public class EmailAddress {

  private final String emailBeginning;
  private final String emailEnding;

  /**
   * Creates a new EmailAddress Object. Note that if there is no email ending, the email ending for AT&T (MMS) is used.
   *
   * @param address The EmailAccount Address to create it with
   * @throws IllegalArgumentException If address is null or address is less than 6 characters
   */
  protected EmailAddress(String address) {
    if (address == null || address.length() < 7) {
      throw new IllegalArgumentException("Email address must not be null and must be more than 6 characters long.");
    }
    if (!address.contains("@")) {
      //System.out.println("NOTE: Address " + address + " does not contain ending! Adding AT&T (MMS) ending.");
      address += getCarrierExtension("AT&T (MMS)");
    } else if (address.substring(address.indexOf("@")).length() < 5) {
      //System.out.println("Email address ending is not long enough -- setting to AT&T (MMS) ending.");
      address = address.substring(0, address.indexOf("@"));
      address += getCarrierExtension("AT&T (MMS)");
    }
    //System.out.println("Old Number: " + address);
    address = address.trim();
    String temp = address.substring(0, address.indexOf("@")); // Avoid replacing chars in @car.rier.ext
    temp = temp.replaceAll("-", "");
    temp = temp.replaceAll("\\(", "");
    temp = temp.replaceAll("\\)", "");
    temp = temp.replaceAll(" ", ""); // Emails can technically have "\ " in them, but that's a huge fringe case. If it comes up, fix it.
    address = temp + address.substring(address.indexOf("@"));
    //System.out.println("New Number: " + address);
    emailBeginning = address.substring(0, address.indexOf("@"));
    emailEnding = address.substring(address.indexOf("@"));
  }

  /**
   * Gets the complete email address.
   *
   * @return The complete email address
   */
  public String getCompleteAddress() {
    return emailBeginning + emailEnding;
  }

  /**
   * Gets the first part of the email address, before the @. For example, it returns the underlined part of the following email:<br>
   * => <u>Test</u>@gmail.com
   *
   * @return The email address beginning
   */
  public String getAddressBeginning() {
    return emailBeginning;
  }

  /**
   * Gets the second part of the email address, after and including the @. For example, it returns the underlined part of the following email:<br>
   * => Test<u>@gmail.com</u>
   *
   * @return The email address ending
   */
  public String getAddressEnding() {
    return emailEnding;
  }

  /**
   * Gets the common name of the cell carrier for this email address, or [Other] if unknown.
   *
   * @return The name of the cell carrier for this email address
   */
  public String getCarrierName() {
    return getProvider(emailEnding);
  }

  /**
   * Gets the provider name for a given cell number ending. Note that this is NOT case sensitive. This returns [Other] if no match is found.
   *
   * @param ending The email ending to check
   * @return The provider name of the given email ending
   */
  public static String getProvider(String ending) {
    try {
      if (ending.startsWith("@")) {
        ending = ending.substring(1);
      }
      switch (ending.toLowerCase()) {
        case "mms.att.net":
          return "AT&T (MMS)";
        case "txt.att.net":
          return "AT&T (SMS)";
        case "vtext.com":
          return "Verizon";
        case "messaging.sprintpcs.com":
          return "Sprint";
        case "tmomail.net":
          return "T-Mobile";
        case "email.uscc.net":
          return "U.S. Cellular";
        case "txt.bell.ca":
          return "Bell";
        case "pcs.rogers.com":
          return "Rogers";
        case "fido.ca":
          return "Fido";
        case "txt.koodomobile.com":
          return "Koodo";
        case "msg.telus.com":
          return "Telus";
        case "vmobile.ca":
          return "Virgin";
        case "txt.windmobile.ca":
          return "Wind";
        case "pcs.saktelmobility.com":
          return "SaskTel";
        default:
          return "[Other]";
      }
    } catch (Exception e) {
      return "AT&T (MMS)";
    }
  }

  /**
   * Gets the email ending for a given carrier. Note that this defaults to AT&T (MMS) if an invalid carrier is specified. This is not case sensitive.
   *
   * @param carrier The carrier name to get the ending of
   * @return The carrier ending, or the ending for AT&T (MMS) if not found.
   */
  public static String getCarrierExtension(String carrier) {
    switch (carrier.toLowerCase()) {
      case "at&t (mms)":
        return "@mms.att.net";
      case "at&t (sms)":
        return "@txt.att.net";
      case "verizon":
        return "@vtext.com";
      case "sprint":
        return "@messaging.sprintpcs.com";
      case "t-mobile":
        return "@tmomail.net";
      case "u.s. cellular":
        return "@email.uscc.net";
      case "bell":
        return "@txt.bell.ca";
      case "rogers":
        return "@pcs.rogers.com";
      case "fido":
        return "@fido.ca";
      case "koodo":
        return "@txt.koodomobile.com";
      case "telus":
        return "@msg.telus.com";
      case "virgin":
        return "@vmobile.ca";
      case "wind":
        return "@txt.windmobile.ca";
      case "sasktel":
        return "@pcs.saktelmobility.com";
      default:
        System.out.println("ERROR: Unable to identify carrier. Using default AT&T.");
        return getCarrierExtension("AT&T (MMS)");
    }
  }
}
