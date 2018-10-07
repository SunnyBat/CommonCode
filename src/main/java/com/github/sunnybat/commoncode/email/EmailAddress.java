package com.github.sunnybat.commoncode.email;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores information about a given email address.
 *
 * @author SunnyBat
 */
public class EmailAddress {

    private static final BiMap<String, String> carrierNameToEmailEndings = HashBiMap.create();

    static {
        // NOTE: Case sensitive
        carrierNameToEmailEndings.put("AT&T (MMS)", "@mms.att.net");
        carrierNameToEmailEndings.put("AT&T (SMS)", "@txt.att.net");
        carrierNameToEmailEndings.put("Verizon", "@vtext.com");
        carrierNameToEmailEndings.put("Sprint", "@messaging.sprintpcs.com");
        carrierNameToEmailEndings.put("T-Mobile", "@tmomail.net");
        carrierNameToEmailEndings.put("U.S. Cellular", "@email.uscc.net");
        carrierNameToEmailEndings.put("Bell", "@txt.bell.ca");
        carrierNameToEmailEndings.put("Rogers", "@pcs.rogers.com");
        carrierNameToEmailEndings.put("Fido", "@fido.ca");
        carrierNameToEmailEndings.put("Koodo", "@txt.koodomobile.com");
        carrierNameToEmailEndings.put("Telus", "@msg.telus.com");
        carrierNameToEmailEndings.put("Virgin", "@vmobile.ca");
        carrierNameToEmailEndings.put("Wind", "@txt.windmobile.ca");
        carrierNameToEmailEndings.put("Sasktel", "@pcs.saktelmobility.com");
    }

    private final String emailBeginning;
    private final String emailEnding;

    /**
     * Creates a new EmailAddress Object. Note that if there is no email ending,
     * the email ending for AT&T (MMS) is used.
     *
     * @param address The EmailAccount Address to create it with
     * @throws IllegalArgumentException If address is null or address is less
     * than 6 characters
     */
    public EmailAddress(String address) {
        if (address == null || address.length() <= 5) {
            throw new IllegalArgumentException("Email address must not be null and must be more than 5 characters long.");
        }

        if (!address.contains("@")) { // No email ending; default it
            address += getCarrierExtension("AT&T (MMS)");
        } else if (address.substring(address.indexOf("@")).length() < 5) { // Invalid email ending; default it
            address = address.substring(0, address.indexOf("@"));
            address += getCarrierExtension("AT&T (MMS)");
        }

        address = address.trim();
        String tempBeginning = address.substring(0, address.indexOf("@")); // Avoid replacing chars in @car.rier.ext
        tempBeginning = tempBeginning.replaceAll("-|\\(|\\)|\\s", ""); // Emails can technically have "\ " in them, but that's a huge fringe case. If it comes up, fix it.

        emailBeginning = tempBeginning;
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
     * Gets the first part of the email address, before the @. For example, it
     * returns the underlined part of the following email:<br>
     * => <u>Test</u>@gmail.com
     *
     * @return The email address beginning
     */
    public String getAddressBeginning() {
        return emailBeginning;
    }

    /**
     * Gets the second part of the email address, after and including the @. For
     * example, it returns the underlined part of the following email:<br>
     * => Test<u>@gmail.com</u>
     *
     * @return The email address ending
     */
    public String getAddressEnding() {
        return emailEnding;
    }

    /**
     * Gets the common name of the cell carrier for this email address, or
     * [Other] if unknown.
     *
     * @return The name of the cell carrier for this email address
     */
    public String getCarrierName() {
        return getProvider(emailEnding);
    }

    /**
     * Gets the provider name for a given cell number ending. Note that this is
     * NOT case sensitive. This returns [other] if no match is found.
     *
     * @param ending The email ending to check
     * @return The provider name of the given email ending
     */
    public static String getProvider(String ending) {
        return carrierNameToEmailEndings.inverse().getOrDefault(ending.toLowerCase(), "[Other]");
    }

    /**
     * Gets the email ending for a given carrier. Note that this defaults to
     * AT&T (MMS) if an invalid carrier is specified. This is not case
     * sensitive.
     *
     * @param carrier The carrier name to get the ending of
     * @return The carrier ending, or the ending for AT&T (MMS) if not found.
     */
    public static String getCarrierExtension(String carrier) {
        return carrierNameToEmailEndings.getOrDefault(carrier, "@mms.att.net");
    }

    /**
     * Converts the given addresses to a List\<EmailAddress\> of addresses. Note
     * that these addresses should be separated with semicolons (';'). Any
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
     * Converts a given List<EmailAddress> to a String containing the raw
     * addresses. Note that each address will be separated by a semicolon and a
     * space ("; ").
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
     * Splits the given email address up into email and provider. This always
     * returns an array with two values. Both, one or none of these values may
     * be null.
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
