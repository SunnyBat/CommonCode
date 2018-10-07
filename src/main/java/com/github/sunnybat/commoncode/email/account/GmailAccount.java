package com.github.sunnybat.commoncode.email.account;

import com.github.sunnybat.commoncode.email.EmailAddress;
import com.github.sunnybat.commoncode.error.ErrorBuilder;
import com.github.sunnybat.commoncode.oauth.OauthCallbackServer;
import com.github.sunnybat.commoncode.oauth.OauthRequired;
import com.github.sunnybat.commoncode.oauth.OauthStatusUpdater;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Profile;
import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author SunnyBat
 */
public class GmailAccount extends EmailAccount {

    private static final String DEFAULT_APPLICATION_NAME = "CommonCode";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String DEFAULT_CREDENTIALS_FOLDER = "credentials"; // Directory to store user credentials.
    private static final String DEFAULT_CLIENT_SECRET_PATH = "/resources/keys/client_secret.json";
    private static final int[] DEFAULT_PORTS = new int[]{43230, 43231, 43232, 43233, 43234, 43235, 43236, 43237, 43238, 43239};
    private static final String DEFAULT_CALLBACK_URL_BASE = "/PAXChecker/gmailcallback";
    /**
     * Global instance of the scopes required to use this class. If modifying
     * these scopes, delete your previously saved credentials/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_COMPOSE);

    // "Constants"
    private String applicationName;
    private String credentialsFolder;
    private String clientSecretJsonPath;
    private Gmail gmailService;
    private OauthCallbackServer<GmailCredentials> callbackServer;
    private boolean cancelAuthentication;

    // Loaded at runtime
    private String username;

    /**
     * Creates a new GmailAccount.
     */
    public GmailAccount() {
        this(DEFAULT_APPLICATION_NAME, DEFAULT_CREDENTIALS_FOLDER, DEFAULT_CLIENT_SECRET_PATH);
    }

    /**
     * Creates a new GmailAccount.
     *
     * @param applicationName The name of the application to display to the user
     */
    public GmailAccount(String applicationName) {
        this(applicationName, DEFAULT_CREDENTIALS_FOLDER, DEFAULT_CLIENT_SECRET_PATH);
    }

    /**
     * Creates a new GmailAccount.
     *
     * @param applicationName The name of the application to display to the user
     * @param credentialsFolder The folder to store credentials in
     */
    public GmailAccount(String applicationName, String credentialsFolder) {
        this(applicationName, credentialsFolder, DEFAULT_CLIENT_SECRET_PATH);
    }

    /**
     * Creates a new GmailAccount.
     *
     * @param applicationName The name of the application to display to the user
     * @param credentialsFolder The folder to store credentials in
     * @param clientSecretJsonPath The relative path to the client secret JSON
     * file stored in the classpath
     */
    public GmailAccount(String applicationName, String credentialsFolder, String clientSecretJsonPath) {
        this.applicationName = applicationName;
        this.credentialsFolder = credentialsFolder;
        this.clientSecretJsonPath = clientSecretJsonPath;
        this.callbackServer = new OauthCallbackServer<>(DEFAULT_PORTS, DEFAULT_CALLBACK_URL_BASE);
    }

    @Override
    public boolean checkAuthentication() {
        return checkAuthentication(true, true, false, null);
    }

    public boolean checkAuthentication(boolean useLocalCredentials, boolean useCallbackAuth, boolean useOobAuth, OauthStatusUpdater userInteractor) {
        try {
            // We've already logged in, we're good
            if (gmailService != null) {
                if (userInteractor != null) {
                    userInteractor.authSuccess();
                }
                return true;
            }

            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(clientSecretJsonPath);
            return checkAuthentication(in, useLocalCredentials, useCallbackAuth, useOobAuth, userInteractor);
        } catch (Exception e) {
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("Internal Error")
                .setErrorMessage("Unable to read client secret JSON from stream path " + clientSecretJsonPath)
                .buildWindow();
            return false;
        }
    }

    public boolean checkAuthentication(InputStream clientSecretJsonInputStream, boolean useLocalCredentials, boolean useCallbackAuth, boolean useOobAuth, OauthStatusUpdater userInteractor) {
        try {
            // We've already logged in, we're good
            if (gmailService != null) {
                if (userInteractor != null) {
                    userInteractor.authSuccess();
                }
                return true;
            }

            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Credential appCredentials = getCredentials(HTTP_TRANSPORT, clientSecretJsonInputStream, useLocalCredentials, useCallbackAuth, useOobAuth, userInteractor);
            if (appCredentials == null) {
                if (userInteractor != null) {
                    if (cancelAuthentication) {
                        userInteractor.updateStatus("Authentication canceled");
                    } else {
                        userInteractor.updateStatus("Unable to authenticate with Gmail");
                    }
                    userInteractor.authFailure();
                }
                return false;
            }
            if (userInteractor != null) {
                userInteractor.updateStatus("Validating credentials");
            }
            gmailService = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, appCredentials)
                .setApplicationName(applicationName)
                .build(); // Prompts for authentication if we don't have auth credentials

            Profile myProfile = gmailService.users().getProfile("me").execute();
            username = myProfile.getEmailAddress();
            if (userInteractor != null) {
                userInteractor.authSuccess();
            }

            return true;
        } catch (Exception e) {
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("Authentication Error")
                .setErrorMessage("Error while authenticating. See log for technical details.")
                .buildWindow();
            if (userInteractor != null) {
                userInteractor.updateStatus("Unable to log in");
                userInteractor.authFailure();
            }
            return false;
        }
    }

    @Override
    public String getEmailAddress() {
        return username;
    }

    @Override
    public boolean sendEmail(String subject, String body) {
        try {
            if (gmailService == null && !GmailAccount.this.checkAuthentication()) { // Attempt to authenticate to send email
                return false;
            }

            Message message = createEmail(subject, body);
            gmailService.users().messages().send("me", message).execute();
            super.emailSuccessfullySent();
            return true;
        } catch (Exception e) {
            new ErrorBuilder()
                .setError(e)
                .setErrorTitle("Send Email Error")
                .setErrorMessage("Unable to send email. Check error log for details.")
                .buildWindow();
            return false;
        }
    }

    public boolean checkAutoAuth() {
        return checkAuthentication(true, false, false, null);
    }

    /**
     * Deletes the credentials that have been saved to the disc. Invalidates any
     * further actions with this GmailAccount.
     *
     * @throws IOException if unable to delete the credentials
     */
    public void deleteCredentials() throws IOException {
        if (!new File(credentialsFolder + "StoredCredential").delete()) {
            System.out.println("Unable to delete StoredCredential");
        }
        gmailService = null;
        username = null;
    }

    public void interrupt() {
        cancelAuthentication = true;
        callbackServer.cancelListeningForConnection();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @param clientSecretData The InputStream that contains the
     * client_secret.json data
     * @return An authorized Credential object.
     * @throws IOException If there is no client_secret.
     */
    private Credential getCredentials(NetHttpTransport httpTransport, InputStream clientSecretData, boolean useLocalCredentials, boolean useCallbackAuth, boolean useOobAuth, OauthStatusUpdater userInteractor) throws IOException {
        try {
            // Load client secrets.
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(clientSecretData));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow.Builder myBuilder = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(credentialsFolder)))
                .setAccessType("offline");
            GoogleAuthorizationCodeFlow flow = myBuilder.build();
            String userId = "user";
            Credential ret;
            cancelAuthentication = false;
            if ((!cancelAuthentication && useLocalCredentials && (ret = loadAuthorizationFromLocalStorage(userId, flow, userInteractor)) != null)
                || (!cancelAuthentication && useCallbackAuth && (ret = loadAuthorizationWithCallbackUri(userId, flow, callbackServer, userInteractor)) != null)
                || (!cancelAuthentication && useOobAuth && (ret = loadAuthorizationWithOobAuth(userId, flow, System.in, userInteractor)) != null)) {
                return ret;
            } else {
                if (userInteractor != null) {
                    userInteractor.authFailure();
                    userInteractor.updateStatus("Unable to authenticate");
                }
                return null;
            }
        } catch (Exception npe) {
            if (userInteractor != null) {
                userInteractor.authFailure();
                userInteractor.updateStatus("Error while authenticating");
            }
            return null;
        }
    }

    private Credential loadAuthorizationFromLocalStorage(String userId, GoogleAuthorizationCodeFlow flow, OauthStatusUpdater userInteractor) {
        try {
            if (userInteractor != null) {
                userInteractor.updateStatus("Reading local credentials");
            }
            Credential credential = flow.loadCredential(userId);
            if (credential != null
                && (credential.getRefreshToken() != null
                || credential.getExpiresInSeconds() == null
                || credential.getExpiresInSeconds() > 60)) {
                return credential;
            }
        } catch (Exception e) {
        }
        return null;
    }

    private Credential loadAuthorizationWithOobAuth(String userId, GoogleAuthorizationCodeFlow flow, InputStream readCodeFrom, OauthStatusUpdater userInteractor) throws IOException {
        // open in browser
        String redirectUri = "oob";
        AuthorizationCodeRequestUrl authorizationUrl
            = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
        if (userInteractor == null) {
            return null;
        }
        userInteractor.updateStatus("Waiting for PIN input");
        userInteractor.promptForAuthorizationPin();
        userInteractor.setAuthUrl(authorizationUrl.toString());
        Desktop.getDesktop().browse(authorizationUrl.toURI());
        // prompt for authorization code and exchange it for an access token
        String code = userInteractor.getAuthorizationPin();
        if (code == null || code.isEmpty()) {
            return null;
        }
        userInteractor.updateStatus("Authenticating with PIN");
        TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
        // store credential and return it
        return flow.createAndStoreCredential(response, userId);
    }

    private Credential loadAuthorizationWithCallbackUri(String userId, GoogleAuthorizationCodeFlow flow, OauthCallbackServer<GmailCredentials> receiver, OauthStatusUpdater userInteractor) throws IOException {
        try {
            if (userInteractor != null) {
                userInteractor.updateStatus("Opening callback listener");
            }
            if (receiver.openListener()) {
                // open in browser
                String redirectUri = receiver.getLocalCallbackUri();
                AuthorizationCodeRequestUrl authorizationUrl
                    = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
                Desktop.getDesktop().browse(authorizationUrl.toURI());
                userInteractor.setAuthUrl(authorizationUrl.toString());
                GmailCredentials saveTo = new GmailCredentials();
                if (userInteractor != null) {
                    userInteractor.updateStatus("Waiting for authentication");
                }
                // receive authorization code and exchange it for an access token
                if (receiver.listenForConnection(saveTo)) {
                    if (userInteractor != null) {
                        userInteractor.updateStatus("Parsing callback");
                    }
                    TokenResponse response = flow.newTokenRequest(saveTo.code).setRedirectUri(redirectUri).execute();
                    // store credential and return it
                    return flow.createAndStoreCredential(response, userId);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } finally {
            receiver.cancelListeningForConnection();
        }
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param subject The subject of the email
     * @param bodyText The body text of the email
     * @return The Message to be used to send email
     * @throws MessagingException
     * @throws IOException
     */
    private Message createEmail(String subject,
        String bodyText)
        throws MessagingException, IOException {
        List<EmailAddress> toList = getToAddressList();
        List<EmailAddress> ccList = getCcAddressList();
        List<EmailAddress> bccList = getBccAddressList();
        if (toList.isEmpty() && ccList.isEmpty() && bccList.isEmpty()) {
            throw new IllegalStateException("No emails are specified to send this message to");
        } else if (!hasTimeDelayPassed()) {
            throw new IllegalStateException("Emails being sent to frequently");
        }

        // === Initialize blanks for MimeMessage ===
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        // === Create MimeMessage ===
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(username));

        // === Set up MimeMessage send to fields ===
        for (EmailAddress address : getToAddressList()) {
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(address.getCompleteAddress()));
        }
        for (EmailAddress address : getCcAddressList()) {
            email.addRecipient(javax.mail.Message.RecipientType.BCC,
                new InternetAddress(address.getCompleteAddress()));
        }
        for (EmailAddress address : getBccAddressList()) {
            email.addRecipient(javax.mail.Message.RecipientType.BCC,
                new InternetAddress(address.getCompleteAddress()));
        }

        // === Configure MimeMessage ===
        email.setSubject(subject);
        email.setText(bodyText);

        // === Create Message (Gmail API) ===
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public class GmailCredentials {

        @OauthRequired
        public String code;
    }

}
