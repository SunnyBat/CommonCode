package com.github.sunnybat.commoncode.update;

/**
 *
 * @author SunnyBat
 */
public interface UpdatePrompter {

  public void showPrompt();

  public void waitForResponse();

  public boolean getResponse();

}
