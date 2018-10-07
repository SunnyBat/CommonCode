package com.github.sunnybat.commoncode.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;

/**
 * A server for listening for new connections. Note that this is thread-safe for
 * canceling listening for connections, but is not thread-safe in any other way.
 * This should only be used for listening for connections on one thread at a
 * time, however it does not make any checks to prevent this.
 *
 * @author SunnyBat
 */
public class OauthCallbackServer<T> {

    private int[] ports;
    private String callbackUrlBase;
    private ServerSocket listenOn;
    private boolean cancelListening = false;
    private final Object cancelListeningLock = new Object();
    private static final String RESPONSE_HTML_SUCCESS = "HTTP/1.0 200 OK\r\n\r\n<html><head><title>Success</title></head><body>Authentication successful. You may close this window.</body></html>";
    private static final String RESPONSE_HTML_FAILURE = "HTTP/1.0 401 Unauthorized\r\n\r\n<html><head><title>Failure</title></head><body>Unable to authenticate with the server. Please try again.</body></html>";

    public OauthCallbackServer(int[] portsToTry, String callbackUrlBase) {
        if (portsToTry == null || callbackUrlBase == null) {
            throw new NullPointerException("portsToTry and callbackUrlBase cannot be null");
        }
        this.ports = portsToTry;
        if (!callbackUrlBase.startsWith("/")) {
            callbackUrlBase = "/" + callbackUrlBase;
        }
        this.callbackUrlBase = callbackUrlBase;
    }

    /**
     * Opens a new callback listener. Does not listen for connections.
     *
     * @return The port the listener is listening on
     */
    public boolean openListener() {
        for (int port : ports) {
            try {
                listenOn = new ServerSocket(port);
                synchronized (cancelListeningLock) {
                    cancelListening = false;
                }
                return true;
            } catch (IOException ioe) {
                System.out.println("Unable to open listener on port " + port);
            }
        }
        return false;
    }

    /**
     * Closes the current callback listener if open.
     */
    public void closeListener() {
        try {
            if (listenOn != null) {
                listenOn.close();
            }
        } catch (IOException e) {
        }
    }

    /**
     * Gets the Callback URI that the OAuth application should send a requrest
     * to.
     *
     * @return The Callback URI, or null if no connection listener is
     * instantiated
     */
    public String getLocalCallbackUri() {
        if (listenOn == null) {
            return null;
        }
        return "http://localhost:" + listenOn.getLocalPort() + callbackUrlBase;
    }

    /**
     * Listens for new connections on the previously opened listener. Note that
     * all required fields in T must be marked with the {@link OauthRequired}.
     * All fields not marked with this (or with the required value in the
     * annotation set to false) will be considered optional.<br>
     * Note that this will always close the connection listener before this
     * method retuns.
     *
     * @param saveTo The T to save information to
     * @return True if all required fields are present, false if not
     */
    public boolean listenForConnection(T saveTo) {
        if (saveTo == null) {
            return false;
        }
        if (listenOn != null) {
            try {
                listenOn.setSoTimeout(250);
                boolean shouldCancel;
                synchronized (cancelListeningLock) {
                    shouldCancel = cancelListening;
                }
                while (!shouldCancel) {
                    try {
                        Socket accept = listenOn.accept();
                        String firstHttpLine = readFirstLineOfHttpResponse(accept.getInputStream());
                        boolean successful = parseGetCallbackUrlLine(firstHttpLine, saveTo);
                        respondWithPage(accept.getOutputStream(), successful);
                        synchronized (cancelListeningLock) {
                            cancelListening = false;
                        }
                        return successful;
                    } catch (SocketTimeoutException ste) {
                        // Do nothing, we just timed out so we can check if we should cancel listening
                    }
                    synchronized (cancelListeningLock) {
                        shouldCancel = cancelListening;
                    }
                }
                synchronized (cancelListeningLock) { // Reset cancelListening no matter what
                    cancelListening = false;
                }
            } catch (IOException ioe) {
                System.out.println("IOException while listening for OAuth callback");
                System.out.println(ioe.getMessage());
            } finally {
                try {
                    listenOn.close();
                } catch (IOException ioe) {
                    System.out.println("Unable to close OAuth listener");
                }
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * Cancels listening for a connection. This will close the current listener.
     * Note that this is not guaranteed to close the connection listener before
     * a connection is made, but it will close the connection listener
     * relatively quickly. You should always look for the return of
     * {@link #listenForConnection(java.lang.Object)} before assuming no
     * connection has been made.
     */
    public void cancelListeningForConnection() {
        synchronized (cancelListeningLock) {
            cancelListening = true;
        }
    }

    private String readFirstLineOfHttpResponse(InputStream toRead) throws IOException {
        byte[] buff = new byte[128];
        StringBuilder allReadContent = new StringBuilder();
        while (toRead.read(buff) != -1) {
            String currentInput = new String(buff);
            System.out.print(currentInput);
            if (currentInput.contains("\r\n")) {
                allReadContent.append(currentInput.substring(0, currentInput.indexOf("\r\n")));
                break;
            } else {
                allReadContent.append(currentInput);
            }
        }
        return allReadContent.toString();
    }

    private boolean parseGetCallbackUrlLine(String urlLine, T saveTo) {
        if (urlLine.contains(callbackUrlBase) // TODO: Check for exactly this
            && urlLine.contains("GET") // Ensure proper request
            && urlLine.contains("?") // Ensure URL parameters specified
            && urlLine.contains("HTTP/1.1")) { // Ensure valid HTTP request we understand

            // 1. Strip to start of URL parameters
            String urlParameters = urlLine.substring(urlLine.indexOf("?") + 1);
            // 2. Strip off end of GET request to only URL parameters
            urlParameters = urlParameters.substring(0, urlParameters.indexOf("HTTP/1.1") - 1);
            // 3. Split URL parameters by &, so we get "key=value" in each index
            String[] paramsSplit = urlParameters.split("&");
            // 4. Evaluate each Key and Value
            setObjectFields(paramsSplit, saveTo);

            // Verify that all of our required fields have been saved
            if (!verifyAllRequiredFieldsSet(saveTo)) {
                System.out.println("Unable to parse full OAuth callback response: " + urlLine);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void setObjectFields(String[] keyValues, T saveTo) {
        for (String keyValueString : keyValues) {
            // Split by = -- index 0 is the key and index 1 is the value
            String[] keyValueSplit = keyValueString.split("=");
            if (keyValueSplit.length == 2) {
                String keyName = keyValueSplit[0];
                String keyValue = keyValueSplit[1];

                // At this point, we need to URL decode the key and value
                // We can't do it before, since if there is a URL encoded
                // & or = in the key or value, it will mess up our splitting
                try {
                    keyName = URLDecoder.decode(keyName, "UTF-8");
                    keyValue = URLDecoder.decode(keyValue, "UTF-8");
                } catch (UnsupportedEncodingException uee) {
                    System.out.println("Unable to decode URL parameter value; attempting to parse and strip");
                }

                // Now, we need to assign the key and value to the field
                // in our OauthCallback object
                try {
                    // Reflect in and get the field that has the same name
                    // as the key
                    Field callbackField = saveTo.getClass().getField(keyName);
                    // And set the value
                    callbackField.set(saveTo, keyValue);
                } catch (NoSuchFieldException nsfe) {
                    System.out.println("Unrecognized keyValuePair in OAuth callback URL: " + keyValueString);
                } catch (IllegalAccessException | SecurityException e) {
                    System.out.println("Unable to set OAuthCallback value " + keyName + " to " + keyValue + " -- something has likely gone wrong.");
                    System.out.println("Exception: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid keyValuePair in OAuth callback URL: " + keyValueString);
            }
        }
    }

    private boolean verifyAllRequiredFieldsSet(T saveTo) {
        try {
            for (Field toCheck : saveTo.getClass().getFields()) { // Get all public fields
                if (toCheck.isAnnotationPresent(OauthRequired.class) // Annotation present
                    && toCheck.getAnnotation(OauthRequired.class).required() // We have a required attribute
                    && toCheck.get(saveTo) == null) { // Required attribute is not set
                    return false;
                }
            }
            return true;
        } catch (IllegalArgumentException | IllegalAccessException iae) {
            System.out.println("Internal error while verifying OAuth callback response");
            return false;
        }
    }

    private void respondWithPage(OutputStream toWrite, boolean success) throws IOException {
        if (success) {
            toWrite.write(RESPONSE_HTML_SUCCESS.getBytes());
        } else {
            toWrite.write(RESPONSE_HTML_FAILURE.getBytes());
        }
        toWrite.close();
    }
}
