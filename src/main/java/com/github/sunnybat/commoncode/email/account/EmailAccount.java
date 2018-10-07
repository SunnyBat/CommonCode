package com.github.sunnybat.commoncode.email.account;

import com.github.sunnybat.commoncode.email.EmailAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author SunnyBat
 */
public abstract class EmailAccount {

    private static final int DEFAULT_EMAIL_DELAY_SECONDS = 30;
    private final long emailDelayMillis;
    private long timeLastSent;
    private List<EmailAddress> toAddresses = new ArrayList<>();
    private List<EmailAddress> ccAddresses = new ArrayList<>();
    private List<EmailAddress> bccAddresses = new ArrayList<>();

    /**
     * Creates a new EmailAccount with the default amount of seconds between
     * emails (30).
     */
    public EmailAccount() {
        this(DEFAULT_EMAIL_DELAY_SECONDS);
    }

    /**
     * Creates a new EmailAccount.
     *
     * @param minimumDelayBetweenEmails The minimum amount of seconds between
     * sending emails
     */
    public EmailAccount(int minimumDelayBetweenEmails) {
        this.emailDelayMillis = minimumDelayBetweenEmails * 1000;
    }

    /**
     * Attempts to authenticate this EmailAccount. Not necessary to call before
     * sending an email.
     *
     * @return True if successfully authenticated, false otherwise
     */
    public abstract boolean checkAuthentication();

    /**
     * Gets the email address associated with this EmailAccount.
     *
     * @return The email address
     */
    public abstract String getEmailAddress();

    /**
     * Sends an email using the configured account and authentication method.
     * To, CC, and BCC addresses should be configured using
     * {@link #addToEmailAddress(java.lang.String)}, {@link #addCcEmailAddress(java.lang.String)},
     * and {@link #addBccEmailAddress(java.lang.String)}, respectively.
     *
     * @param subject The subject of the email to send
     * @param body The contents of the email to send
     * @return True if the email was successfully sent, false if not
     * @throws IllegalStateException if this EmailAccount is not correctly
     * configured, or if attempting to send emails too frequently
     */
    public abstract boolean sendEmail(String subject, String body);

    /**
     * Adds the given email address to the list of addresses to send to. Note
     * that this does NOT check for duplicates.<br>
     * You may specify multiple email addresses to add using a semicolon (';')
     *
     * @param add The address to add to the list
     * @throws IllegalArgumentException If the email address(es) given is
     * invalid
     */
    public void addToEmailAddress(String add) {
        if (add != null) {
            for (EmailAddress e : EmailAddress.convertToList(add)) {
                toAddresses.add(e);
            }
        }
    }

    /**
     * Adds the given email address to the list of addresses to send to. Note
     * that this does NOT check for duplicates.<br>
     *
     * @param add The address to add to the list
     * @throws IllegalArgumentException If the email address given is invalid
     */
    public void addToEmailAddress(EmailAddress add) {
        if (add != null) {
            toAddresses.add(add);
        }
    }

    /**
     * Adds the given email address to the list of addresses to CC to. Note that
     * this does NOT check for duplicates.<br>
     * You may specify multiple email addresses to add using a semicolon (';')
     *
     * @param add The address to add to the list
     * @throws IllegalArgumentException If the email address(es) given is
     * invalid
     */
    public void addCcEmailAddress(String add) {
        if (add != null) {
            for (EmailAddress e : EmailAddress.convertToList(add)) {
                ccAddresses.add(e);
            }
        }
    }

    /**
     * Adds the given email address to the list of addresses to CC to. Note that
     * this does NOT check for duplicates.<br>
     *
     * @param add The address to add to the list
     * @throws IllegalArgumentException If the email address given is invalid
     */
    public void addCcEmailAddress(EmailAddress add) {
        if (add != null) {
            ccAddresses.add(add);
        }
    }

    /**
     * Adds the given email address to the list of addresses to BCC to. Note
     * that this does NOT check for duplicates.<br>
     * You may specify multiple email addresses to add using a semicolon (';')
     *
     * @param add The address to add to the list
     * @throws IllegalArgumentException If the email address(es) given is
     * invalid
     */
    public void addBccEmailAddress(String add) {
        if (add != null) {
            for (EmailAddress e : EmailAddress.convertToList(add)) {
                bccAddresses.add(e);
            }
        }
    }

    /**
     * Adds the given email address to the list of addresses to BCC to. Note
     * that this does NOT check for duplicates.<br>
     *
     * @param add The address to add to the list
     * @throws IllegalArgumentException If the email address given is invalid
     */
    public void addBccEmailAddress(EmailAddress add) {
        if (add != null) {
            bccAddresses.add(add);
        }
    }

    /**
     * Removes the given email address from the send to list. If not found, this
     * method does nothing.
     *
     * @param remove The email to remove
     */
    public void removeToEmailAddress(String remove) {
        if (remove == null || remove.length() == 0) {
            return;
        }
        for (int i = 0; i < toAddresses.size(); i++) {
            if (toAddresses.get(i).getCompleteAddress().equalsIgnoreCase(remove)) {
                toAddresses.remove(i);
                return;
            }
        }
    }

    /**
     * Removes the given email address from the CC list. If not found, this
     * method does nothing.
     *
     * @param remove The email to remove
     */
    public void removeCcEmailAddress(String remove) {
        if (remove == null || remove.length() == 0) {
            return;
        }
        for (int i = 0; i < ccAddresses.size(); i++) {
            if (ccAddresses.get(i).getCompleteAddress().equalsIgnoreCase(remove)) {
                ccAddresses.remove(i);
                return;
            }
        }
    }

    /**
     * Removes the given email address from the BCC list. If not found, this
     * method does nothing.
     *
     * @param remove The email to remove
     */
    public void removeBccEmailAddress(String remove) {
        if (remove == null || remove.length() == 0) {
            return;
        }
        for (int i = 0; i < bccAddresses.size(); i++) {
            if (bccAddresses.get(i).getCompleteAddress().equalsIgnoreCase(remove)) {
                bccAddresses.remove(i);
                return;
            }
        }
    }

    /**
     * Gets the current List of addresses that the program will send emails to.
     * You may modify this List in any way.
     *
     * @return The List of addresses the program will use
     */
    public List<EmailAddress> getToAddressList() {
        return new ArrayList<>(toAddresses);
    }

    /**
     * Gets the current List of addresses that the program will CC emails to.
     * You may modify this List in any way.
     *
     * @return The List of addresses the program will use
     */
    public List<EmailAddress> getCcAddressList() {
        return new ArrayList<>(ccAddresses);
    }

    /**
     * Gets the current List of addresses that the program will BCC emails to.
     * You may modify this List in any way.
     *
     * @return The List of addresses the program will use
     */
    public List<EmailAddress> getBccAddressList() {
        return new ArrayList<>(bccAddresses);
    }

    /**
     * Checks whether there are any addresses registered to send an email to.
     * This checks all three lists -- to, cc, and bcc.
     *
     * @return True if any emails are present, false if not
     */
    public boolean isAnySendAddressPresent() {
        return !(toAddresses.isEmpty() && ccAddresses.isEmpty() && bccAddresses.isEmpty());
    }

    /**
     * Clears all emails from all lists -- to, cc, and bcc.
     */
    public void clearAllSendAddresses() {
        toAddresses.clear();
        ccAddresses.clear();
        bccAddresses.clear();
    }

    protected final void emailSuccessfullySent() {
        timeLastSent = System.currentTimeMillis();
    }

    protected final boolean hasTimeDelayPassed() {
        return System.currentTimeMillis() - timeLastSent > emailDelayMillis;
    }

}
