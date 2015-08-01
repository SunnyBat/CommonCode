package com.github.sunnybat.commoncode.email;

import com.github.sunnybat.commoncode.error.ErrorBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author SunnyBat
 */
public class EmailAccount {

  private final int EMAIL_DELAY;
  private long timeLastSent;
  private Properties props = new Properties();
  private List<EmailAddress> addressList = new ArrayList<>();

  /**
   * Creates a new EmailAccount with the given information and sets a email delay time of 30 seconds.<br>
   * EmailAccount currently supports the following email providers:<br>
   * <ul>
   * <li>GMail</li>
   * <li>Yahoo1</li>
   * <li>Hotmail (Experimental)</li>
   * <li>AOL (Experimental)</li>
   * <li>Comcast (Experimental)</li>
   * </ul>
   * Note that the given username must be a valid and active email address.
   *
   * @param username The username to use
   * @param password The password to use
   * @throws IllegalArgumentException If username or password is null or if username is an invalid email address
   */
  public EmailAccount(String username, String password) {
    this(username, password, 30);
  }

  /**
   * Creates a new EmailAccount with the given information.<br>
   * EmailAccount currently supports the following email providers:<br>
   * <ul>
   * <li>GMail</li>
   * <li>Yahoo1</li>
   * <li>Hotmail (Experimental)</li>
   * <li>AOL (Experimental)</li>
   * <li>Comcast (Experimental)</li>
   * </ul>
   * Note that the given username must be a valid and active email address.
   *
   * @param username The username to use
   * @param password The password to use
   * @param emailDelay The minimum time (in seconds) between emails
   * @throws IllegalArgumentException If username or password is null or if username is an invalid email address
   */
  public EmailAccount(String username, String password, int emailDelay) {
    if (username == null || username.length() < 7 || !username.contains("@") || username.startsWith("@") || password == null) {
      throw new IllegalArgumentException("Invalid username and/or password specified");
    }
    // Set host
    String emailEnding = username.substring(username.indexOf("@")).toLowerCase();
    if (emailEnding.contains("::")) { // Custom host -- email@site.com::site.smtp.server:port
      String ending = username.substring(username.indexOf("::") + 2);
      String host = ending;
      String port = "465";
      if (ending.contains(":")) {
        host = ending.substring(0, ending.indexOf(":"));
        port = ending.substring(ending.indexOf(":") + 1);
      }
      props.put("mail.smtps.host", host);
      props.put("mail.smtps.port", port);
      throw new UnsupportedOperationException("Program currently unable to use custom hosts.");
    } else if (emailEnding.contains("@gmail.com")) {
      props.put("mail.smtps.host", "smtp.gmail.com");
    } else if (emailEnding.contains("@yahoo.com")) {
      props.put("mail.smtps.host", "smtp.mail.yahoo.com");
    } else if (emailEnding.contains("@hotmail.com") || emailEnding.contains("@live.com")) {
      props.put("mail.smtps.host", "smtp.live.com");
    } else if (emailEnding.contains("@aol.com")) {
      props.put("mail.smtps.host", "smtp.aol.com ");
    } else if (emailEnding.contains("@comcast.net")) {
      props.put("mail.smtps.host", "smtp.comcast.net ");
    } else {
      throw new IllegalArgumentException("You must specify a custom host to use email servers other than Yahoo! or GMail.");
    }
    // Setup the rest
    EMAIL_DELAY = emailDelay;
    props.put("mail.smtps.user", username);
    props.put("mail.smtps.password", password);
    // TLS -- not using anymore
//    props.put("mail.smtps.starttls.enable", "true"); // Is this even doing anything?
//    props.put("mail.smtps.auth", "true");
//    props.put("mail.smtps.port", "587"); // TODO: Replace with custom port option later
  }

  /**
   * Gets the username for this EmailAccount.
   *
   * @return The username
   */
  public String getUsername() {
    return props.getProperty("mail.smtps.user");
  }

  /**
   * Sets a custom Property to use while sending an email. This overrides any program settings.<br>
   * <br>
   * Some common keys and values:<br>
   * <table border="1">
   * <tr>
   * <td>Key</td>
   * <td>Value</td>
   * </tr>
   * <tr>
   * <td>mail.smtps.starttls.enable</td>
   * <td>true or false, default false</td>
   * </tr>
   * <tr>
   * <td>mail.smtps.auth</td>
   * <td>true or false, default false</td>
   * </tr>
   * <tr>
   * <td>mail.smtp.ssl.trust</td>
   * <td>Server address to trust</td>
   * </tr>
   * </table>
   *
   * For a full list of properties you can use, see <a href="https://www.google.com">here</a>.
   *
   * @param key The property key
   * @param value The property value
   */
  public void setProperty(String key, String value) {
    //props.put(key, value);
    if (key == null || value == null) {
      throw new IllegalArgumentException("The key and value given must not be null");
    } else if (!key.startsWith("javax.mail.") && !key.startsWith("mail.smtps.")) {
      throw new IllegalArgumentException("You can only set Properties for javax.mail.* or mail.smtps.*");
    } else {
      props.setProperty(key, value);
    }
  }

  /**
   * Gets the current instance of the JavaMail session for {@link #props}. This should be called every time you send an email.
   *
   * @return The JavaMail Session with the currently set properties
   */
  private Session createNewSession() {
    return Session.getInstance(props);
  }

  /**
   * Sends an email to the provided number(s) using the supplied login information. This should only be called once
   * {@link #setUsername(java.lang.String)}, {@link #setPassword(java.lang.String)}, and ({@link #setCellNum(java.lang.String, java.lang.String)} or
   * {@link #setCellList(java.lang.String)}) have been called.
   *
   * @param subject The subject of the email to send
   * @param msg The contents of the message to send
   * @return True if the email was sent, false if an error occurs
   * @throws IllegalStateException If program is not ready to send an email
   */
  public boolean sendMessage(String subject, String msg) {
    if (addressList.isEmpty()) {
      throw new IllegalStateException("You must specify at least one email to send to");
    }
    if (System.currentTimeMillis() - timeLastSent < EMAIL_DELAY * 1000) {
      throw new IllegalStateException("You must wait " + EMAIL_DELAY + " seconds between sending emails");
    }
    Session mySession = createNewSession();
    try {
      //System.out.println("Initializing message...");
      MimeMessage message = new MimeMessage(mySession);
      message.setFrom(new InternetAddress(getUsername()));
      if (addressList.size() == 1) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(addressList.get(0).getCompleteAddress()));
      } else {
        for (EmailAddress next : addressList) {
          //System.out.println("Address: " + address.getCompleteAddress());
          message.addRecipient(Message.RecipientType.BCC, new InternetAddress(next.getCompleteAddress()));
        }
      }
      message.setSubject(subject);
      message.setText(msg);
      //System.out.println("Message created. Getting Transport session.");
      Transport transport = mySession.getTransport("smtps");
      //System.out.println("Transport created. Loggin in...");
      System.out.println(props);
      transport.connect();
      //System.out.println("Logged in. Sending message...");
      transport.sendMessage(message, message.getAllRecipients());
      //System.out.println("Message Sent!");
      transport.close();
      timeLastSent = System.currentTimeMillis();
    } catch (Exception e) {
      e.printStackTrace();
      if (e instanceof AuthenticationFailedException) { // Subclass of MessagingException, so it should go before it
        new ErrorBuilder()
            .setError(e)
            .setErrorTitle("Login Error")
            .setErrorMessage("Unable to log in. Double-check your username and password."
                + "\nIf using Gmail, make sure you allow access to less secure apps:\nhttps://www.google.com/settings/security/lesssecureapps"
                + "\nYou might also try unlocking a captcha:\nhttp://www.google.com/accounts/DisplayUnlockCaptcha")
            .buildWindow();
      } else if (e instanceof MessagingException) {
        new ErrorBuilder()
            .setError(e)
            .setErrorTitle("Connection Error")
            .setErrorMessage("Unable to connect to email server.")
            .buildWindow();
        //} else if (mex.getLocalizedMessage().contains("javax.mail.AuthenticationFailedException:")) {
      } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
        new ErrorBuilder()
            .setError(e)
            .setErrorTitle("SSL Certificate Error")
            .setErrorMessage("The PAXChecker was unable to connect to the mail server due to an invalid SSL certificate.")
            .buildWindow();
      } else {
        new ErrorBuilder()
            .setError(e)
            .setErrorTitle("Mail Error")
            .setErrorMessage("An unknown error has occurred while attempting to send the message.")
            .buildWindow();
        return false;
      }
    }//end catch block
    //System.out.println("Finished sending message.");
    return true;
  }

  /**
   * Adds the given email address to the list of addresses to send to. Note that this does NOT check for duplicates.<br>
   * You may specify multiple email addresses to add using a semicolon (';')
   *
   * @param add The address to add to the list
   * @throws IllegalArgumentException If the email address(es) given is invalid
   */
  public void addEmailAddress(String add) {
    if (add.contains(";")) {
      for (EmailAddress e : convertToList(add)) {
        addressList.add(e);
      }
    } else {
      addressList.add(new EmailAddress(add));
    }
  }

  /**
   * Removes the given email address from the send to list. If not found, this method does nothing.
   *
   * @param remove The email to remove
   */
  public void removeEmailAddress(String remove) {
    if (remove == null) {
      return;
    }
    for (EmailAddress next : addressList) {
      if (next.getCompleteAddress().matches(remove)) {
        addressList.remove(next);
        //System.out.println("Removed email address " + remove + " from the list of addresses.");
        return;
      }
    }
    //System.out.println("Could not remove address " + remove + " from list of addresses.");
  }

  /**
   * Gets the current List of addresses that the program will send texts to. You may modify this List in any way.
   *
   * @return The List of addresses the program will use
   */
  public List<EmailAddress> getAddressList() {
    return new ArrayList<>(addressList);
  }

  /**
   * Converts the given addresses to a List\<EmailAddress\> of addresses. Note that these addresses should be separated with semicolons (';'). Any
   * invalid email addresses are ignored.
   *
   * @param addresses The addresses to convert to a list
   * @return A List\<EmailAddress\> of addresses parsed from the given String
   */
  public static List<EmailAddress> convertToList(String addresses) {
    List<EmailAddress> tempList = new ArrayList<>();
    String[] split = addresses.split(";");
    for (String split1 : split) {
      try {
        tempList.add(new EmailAddress(split1.trim()));
      } catch (IllegalArgumentException e) {
        // Address is invalid -- ignoring
      }
    }
    return tempList;
  }

  /**
   * Converts a given List<EmailAddress> to a String containing the raw addresses. Note that each address will be separated by a semicolon and a space
   * ("; ").
   *
   * @param list The List to convert
   * @return A String, separated by semicolons, of the given List
   */
  public static String convertToString(List<EmailAddress> list) {
    StringBuilder builder = new StringBuilder();
    for (EmailAddress next : list) {
      builder.append(next.getCompleteAddress());
      builder.append("; ");
    }
    return builder.toString();
  }

  /**
   * Splits the given email address up into email and provider. This always returns an array with two values. Both, one or none of these values may be
   * null.
   *
   * @param emailToSplit The email address to split
   * @return An array with the email and provider separated
   */
  public static String[] splitEmail(String emailToSplit) {
    if (emailToSplit == null) {
      return new String[]{null, null};
    }
    if (emailToSplit.contains("@")) {
      String temp1 = emailToSplit.substring(0, emailToSplit.indexOf("@")).trim();
      String temp2 = emailToSplit.substring(emailToSplit.indexOf("@")).trim();
      return new String[]{temp1, temp2};
    } else {
      return new String[]{emailToSplit.trim(), null};
    }
  }
}
