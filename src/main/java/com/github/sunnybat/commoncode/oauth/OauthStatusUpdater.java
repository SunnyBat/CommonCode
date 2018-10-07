package com.github.sunnybat.commoncode.oauth;

/**
 * A way to update the current status of OAuth authentication. All of these
 * should be implemented in such a way that they are non-blocking.
 *
 * @author SunnyBat
 */
public interface OauthStatusUpdater {

    public void setAuthUrl(String url);

    public void promptForAuthorizationPin();

    public void cancelAuthorizationPinPrompt();

    public String getAuthorizationPin();

    public void updateStatus(String status);

    public void authSuccess();

    public void authFailure();

}
