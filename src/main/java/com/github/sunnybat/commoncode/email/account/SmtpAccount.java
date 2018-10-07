package com.github.sunnybat.commoncode.email.account;

import com.github.sunnybat.commoncode.email.EmailAddress;
import com.github.sunnybat.commoncode.error.ErrorBuilder;
import java.util.List;
import java.util.Properties;
import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author SunnyBat
 */
public class SmtpAccount extends EmailAccount {

    private Properties props = new Properties();

    /**
     * Creates a new EmailAccount with the given information and sets a email
     * delay time of 30 seconds.<br>
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
     * @throws IllegalArgumentException If username or password is null or if
     * username is an invalid email address
     */
    public SmtpAccount(String username, String password) {
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
     * @throws IllegalArgumentException If username or password is null or if
     * username is an invalid email address
     */
    public SmtpAccount(String username, String password, int emailDelay) {
        this(username, password, emailDelay, null);
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
     * @throws IllegalArgumentException If username or password is null or if
     * username is an invalid email address
     */
    public SmtpAccount(String username, String password, int emailDelay, Properties customProperties) {
        super(emailDelay);

        if (username == null || username.length() < 7 || !username.contains("@") || username.startsWith("@") || password == null) {
            throw new IllegalArgumentException("Invalid username and/or password specified");
        }

        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", "587");
        // Enable TLS, authentication
        props.put("mail.smtp.starttls.enable", "true"); // Is this even doing anything?
        props.put("mail.smtp.auth", "true");

        // Set host
        String emailEnding = username.substring(username.indexOf("@")).toLowerCase();
        if (emailEnding.contains("::")) { // Custom host -- email@site.com::site.smtp.server:port
            String email = username.substring(0, username.indexOf("::"));
            String ending = username.substring(username.indexOf("::") + 2);
            String host = ending;
            String port = "587";
            if (ending.contains(":")) { // Custom port
                host = ending.substring(0, ending.indexOf(":"));
                port = ending.substring(ending.indexOf(":") + 1);
            }
            props.put("mail.smtp.user", email);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
        } else if (emailEnding.contains("@gmail.com")) {
            props.put("mail.smtp.host", "smtp.gmail.com");
        } else if (emailEnding.contains("@yahoo.com")) {
            props.put("mail.smtp.host", "smtp.mail.yahoo.com");
            props.put("mail.smtp.port", "465");
        } else if (emailEnding.contains("@hotmail.com") || emailEnding.contains("@live.com")) {
            props.put("mail.smtp.host", "smtp.live.com");
        } else if (emailEnding.contains("@aol.com")) {
            props.put("mail.smtp.host", "smtp.aol.com ");
        } else if (emailEnding.contains("@comcast.net")) {
            props.put("mail.smtp.host", "smtp.comcast.net ");
        } else {
            throw new IllegalArgumentException("You must specify a custom host to use email servers other than GMail, Yahoo, Hotmail, AOL, or Comcast.");
        }

        // Overwrite properties with custom specification
        if (customProperties != null) {
            for (Object property : customProperties.keySet()) {
                if (property instanceof String) {
                    props.put((String) property, customProperties.get(property));
                }
            }
        }
    }

    @Override
    public boolean checkAuthentication() {
        try {
            Session mySession = createNewSession();
            Transport auth = mySession.getTransport("smtp");
            auth.connect();
            auth.close();
            return true;
        } catch (MessagingException me) {
            processEmailException(me);
            return false;
        }
    }

    /**
     * Gets the username for this SmtpAccount.
     *
     * @return The username
     */
    @Override
    public String getEmailAddress() {
        return props.getProperty("mail.smtp.user");
    }

    /**
     * Gets the password for this SmtpAccount.
     *
     * @return The password
     */
    private String getPassword() {
        return props.getProperty("mail.smtp.password");
    }

    /**
     * Sets a custom Property to use while sending an email. This overrides any
     * program settings.<br>
     * <br>
     * Some common keys and values:<br>
     * <table border="1">
     * <tr>
     * <td>Key</td>
     * <td>Value</td>
     * </tr>
     * <tr>
     * <td>mail.smtp.starttls.enable</td>
     * <td>true or false, default false</td>
     * </tr>
     * <tr>
     * <td>mail.smtp.auth</td>
     * <td>true or false, default false</td>
     * </tr>
     * <tr>
     * <td>mail.smtp.ssl.trust</td>
     * <td>Server address to trust</td>
     * </tr>
     * </table>
     *
     * For a full list of properties you can use, see
     * <a href="https://www.google.com">here</a>.
     *
     * @param key The property key
     * @param value The property value
     */
    public void setProperty(String key, String value) {
        //props.put(key, value);
        if (key == null || value == null) {
            throw new IllegalArgumentException("The key and value given must not be null");
        } else if (!key.startsWith("javax.mail.") && !key.startsWith("mail.smtp.") && !key.equals("mail.debug")) {
            throw new IllegalArgumentException("You can only set Properties for javax.mail.* or mail.smtp.*");
        } else {
            props.setProperty(key, value);
        }
    }

    /**
     * Sends an email to the provided number(s) using the supplied login
     * information. This should only be called once
     * {@link #setUsername(java.lang.String)}, {@link #setPassword(java.lang.String)},
     * and ({@link #setCellNum(java.lang.String, java.lang.String)} or
     * {@link #setCellList(java.lang.String)}) have been called.
     *
     * @param subject The subject of the email to send
     * @param msg The contents of the message to send
     * @return True if the email was sent, false if an error occurs
     * @throws IllegalStateException If program is not ready to send an email
     */
    @Override
    public boolean sendEmail(String subject, String msg) {
        List<EmailAddress> toList = getToAddressList();
        List<EmailAddress> ccList = getCcAddressList();
        List<EmailAddress> bccList = getBccAddressList();
        if (toList.isEmpty() && ccList.isEmpty() && bccList.isEmpty()) {
            throw new IllegalStateException("No emails are specified to send this message to");
        } else if (!hasTimeDelayPassed()) {
            throw new IllegalStateException("Emails being sent to frequently");
        }

//        if (toList.isEmpty()) {
//            if (bccList.isEmpty()) {
//                toList.addAll(ccList);
//                ccList.clear();
//            } else if (ccList.isEmpty() && bccList.size() == 1) {
//                toList.add(bccList.get(0));
//                bccList.clear();
//            }
//        }

        Session mySession = createNewSession();
        try {
            // === Create message ===
            MimeMessage message = new MimeMessage(mySession);
            message.setFrom(new InternetAddress(getEmailAddress()));

            // === Add addresses ===
            for (EmailAddress address : getToAddressList()) {
                message.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(address.getCompleteAddress()));
            }
            for (EmailAddress address : getCcAddressList()) {
                message.addRecipient(javax.mail.Message.RecipientType.BCC,
                    new InternetAddress(address.getCompleteAddress()));
            }
            for (EmailAddress address : getBccAddressList()) {
                message.addRecipient(javax.mail.Message.RecipientType.BCC,
                    new InternetAddress(address.getCompleteAddress()));
            }

            // === Set mesage contents ===
            message.setSubject(subject);
            message.setText(msg);

            // === Send message ===
            Transport.send(message);
            super.emailSuccessfullySent();
            return true;
        } catch (Exception e) {
            processEmailException(e);
            return false;
        }
    }

    private void processEmailException(Exception e) {
        e.printStackTrace();
        if (e instanceof AuthenticationFailedException) { // Subclass of MessagingException, so it should go before it
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("Login Error")
                .setErrorMessage("Unable to log in. Double-check your username and password."
                    + "\nIf using Gmail, make sure you allow access to less secure apps:\nhttps://www.google.com/settings/security/lesssecureapps"
                    + "\nYou might also try unlocking a captcha:\nhttps://www.google.com/accounts/DisplayUnlockCaptcha")
                .buildWindow();
        } else if (e instanceof MessagingException) {
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("Connection Error")
                .setErrorMessage("Unable to connect to email server.")
                .buildWindow();
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("SSL Certificate Error")
                .setErrorMessage("Unable to connect to the mail server due to an invalid SSL certificate.")
                .buildWindow();
        } else {
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("Mail Error")
                .setErrorMessage("An unknown error has occurred while attempting to authenticate.")
                .buildWindow();
        }
    }

    /**
     * Gets the current instance of the JavaMail session for {@link #props}.
     * This should be called every time you send an email.
     *
     * @return The JavaMail Session with the currently set properties
     */
    private Session createNewSession() {
        return Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(getEmailAddress(), getPassword());
            }
        });
    }
}
