package com.sunnybat.update;

import java.io.*;
import java.net.*;

/**
 * A class used for downloading program updates.
 *
 * @author SunnyBat
 */
public class UpdateDownloader {

  private boolean useBetaVersion;
  private final String UPDATE_LINK;
  private final String BETA_UPDATE_LINK;

  /**
   * Creates a new UpdateHandler object. Note that you cannot use BETA versions with this.
   *
   * @param updateLink The link to the program's most recent release file
   */
  public UpdateDownloader(String updateLink) {
    this(updateLink, null);
  }

  /**
   * Creates a new UpdateHandler object.
   *
   * @param updateLink The link to the program's most recent release file
   * @param betaUpdateLink The link to the program's most recent BETA file
   */
  public UpdateDownloader(String updateLink, String betaUpdateLink) {
    if (updateLink == null) {
      throw new IllegalArgumentException("updateLink argument cannot be null!");
    }
    UPDATE_LINK = updateLink;
    BETA_UPDATE_LINK = betaUpdateLink;
  }

  /**
   * Makes UpdateDownloader check for BETA versions.
   */
  public void setUseBeta() {
    if (BETA_UPDATE_LINK == null) {
      throw new IllegalStateException("Unable to use BETA version -- BETA version link not set!");
    }
    useBetaVersion = true;
  }

  /**
   * Checks whether or not the program should use BETA versions.
   *
   * @return True for use BETA, false for not
   */
  public boolean getUseBeta() {
    return useBetaVersion;
  }

  /**
   * Returns the size of the update file found online. This will return 0 if the size has not been loaded yet.
   *
   * @return The size of the update file found online, or 0 if the size has not been loaded yet
   * @throws java.io.IOException If an error occurs while getting the update size
   */
  public long getUpdateSize() throws IOException {
    URL updateURL;
    if (useBetaVersion) {
      if (BETA_UPDATE_LINK == null) {
        throw new IllegalArgumentException("BETA update link is null, but BETA versions are enabled!");
      }
      updateURL = new URL(BETA_UPDATE_LINK);
    } else {
      updateURL = new URL(UPDATE_LINK);
    }
    URLConnection conn = updateURL.openConnection();
    long updateSize = conn.getContentLengthLong();
    System.out.println("Update size = " + updateSize);
    if (updateSize == -1) {
      System.out.println("ERROR checking for updates: Update size listed as -1, program most likely unable to connect!");
    }
    return updateSize;
  }

  /**
   * Downloads the latest JAR file from the given link. Note that this automatically closes the program once finished. Also note that if this
   * overwrites the program's current jar file, you will have to restart the program (in a new JVM instance) to load any new classes.
   *
   * @param writeFile The File to write the update to
   * @throws java.io.IOException If an error occurs while downloading or writing the file
   */
  public void updateProgram(File writeFile) throws IOException {
    URL updateURL;
    if (useBetaVersion) {
      updateURL = new URL(BETA_UPDATE_LINK);
    } else {
      updateURL = new URL(UPDATE_LINK);
    }
    URLConnection conn = updateURL.openConnection();
    InputStream inputStream = conn.getInputStream();
    long remoteFileSize = conn.getContentLength();
    System.out.println("Update Size(compressed): " + remoteFileSize + " Bytes");
    String path = writeFile.getAbsolutePath();
    File tempFile = new File(path.substring(0, path.lastIndexOf(".")) + ".temp");
    BufferedOutputStream buffOutputStream = new BufferedOutputStream(new FileOutputStream(tempFile));
    byte[] buffer = new byte[1024];
    int bytesRead;
    System.out.println("Downloading update...");
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      buffOutputStream.write(buffer, 0, bytesRead);
    }
    buffOutputStream.flush();
    buffOutputStream.close();
    inputStream.close();
    if (writeFile.exists()) {
      writeFile.delete();
    }
    tempFile.renameTo(writeFile);
  }
}
