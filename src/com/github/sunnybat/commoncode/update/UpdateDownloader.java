package com.github.sunnybat.commoncode.update;

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
   *
   * @throws IllegalStateException If BETA link has not been specified
   */
  public void setUseBeta() {
    if (BETA_UPDATE_LINK == null) {
      throw new IllegalStateException("Unable to use BETA version -- BETA version link not set!");
    }
    useBetaVersion = true;
  }

  /**
   * Returns the size of the update file found online.
   *
   * @return The size of the update file found online
   * @throws IOException If an error occurs while getting the update size
   * @throws IllegalStateException If BETA version is enabled but BETA update link has not been specified
   */
  public long getUpdateSize() throws IOException {
    URL updateURL;
    if (useBetaVersion) {
      if (BETA_UPDATE_LINK == null) {
        throw new IllegalStateException("BETA version is enabled, but BETA update link is null!");
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
   * Downloads the latest JAR file from the given link. Note that if this overwrites the program's current jar file, you will have to restart the
   * program (in a new JVM instance) to load any new classes.
   *
   * @param writeFile The File to write the update to
   * @throws IOException If an error occurs while downloading or writing the file
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
    if (tempFile.exists()) {
      tempFile.delete();
    }
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
    // Requires custom client code to overwrite currently open JAR file :(
//    if (writeFile.exists()) {
//      if (!writeFile.delete()) {
//        System.out.println("Error deleting file.");
//      }
//    }
//    if (!tempFile.renameTo(writeFile)) {
//      System.out.println("Error renaming file.");
//    }
    // Instead we have to force an overwrite
    InputStream fIn = new BufferedInputStream(new FileInputStream(tempFile));
    File outputFile = new File(path);
    buffOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
    buffer = new byte[1024];
    while ((bytesRead = fIn.read(buffer)) != -1) {
      buffOutputStream.write(buffer, 0, bytesRead);
    }
    buffOutputStream.flush();
    buffOutputStream.close();
    fIn.close();
    tempFile.delete();
  }
}
