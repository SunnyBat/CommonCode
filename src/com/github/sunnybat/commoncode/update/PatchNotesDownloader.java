package com.github.sunnybat.commoncode.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author SunnyBat
 */
public class PatchNotesDownloader {

  private String versionNotes;
  private int updateLevel = -2;
  private int minimumUpdateLevel = 2;
  private final String PATCH_NOTES_LINK; // No need to give user this link, I don't think...
  /**
   * {@link #getUpdateLevel()} returns this if the Patch Notes have not been downloaded yet.
   */
  public static final int UPDATE_NOT_RUN = -3;
  /**
   * {@link #getUpdateLevel()} returns this if the version supplied in {@link #downloadVersionNotes(java.lang.String)} was not found.
   */
  public static final int UPDATE_NOT_FOUND = -2;
  /**
   * {@link #getUpdateLevel()} returns this if the program was unable to download the Patch Notes, but an attempt to was made.
   */
  public static final int UPDATE_ERROR = -1;
  /**
   * {@link #getUpdateLevel()} returns this if no update is available.
   */
  public static final int UPDATE_NONE = 0;
  /**
   * {@link #getUpdateLevel()} returns this if a BETA version is available.
   */
  public static final int UPDATE_BETA = 1;
  /**
   * {@link #getUpdateLevel()} returns this if a minor update is available.
   */
  public static final int UPDATE_MINOR = 2;
  /**
   * {@link #getUpdateLevel()} returns this if a major update is available.
   */
  public static final int UPDATE_MAJOR = 3;

  /**
   * Creates a new PatchNotes instance with the given path to the Patch Notes file.
   *
   * @param patchNotesLink The URL to the Patch Notes file
   */
  public PatchNotesDownloader(String patchNotesLink) {
    // Check to see if patchNotesLink starts with C:\ or a similar path and then append file:\\ if true?
    PATCH_NOTES_LINK = patchNotesLink;
  }

  /**
   * Returns the current Version Notes found. This returns all of the notes after the supplied version (useful for things like patch notes when
   * updating). Note that the version must be the same as in the update notes, otherwise this will return
   *
   * @param version The Version (raw String of version number)
   * @return The version notes after the given version, or null if notes have not been retrieved yet
   */
  public String getVersionNotes(String version) {
    if (versionNotes == null) {
      return null;
    }
    String versNotes = versionNotes;
    try {
      versNotes = versNotes.substring(0, versNotes.indexOf("~~~" + version + "~~~")).trim();
    } catch (IndexOutOfBoundsException e) {
      System.out.println("ERROR: Unable to find update notes for version " + version);
    }
    return versNotes;
  }

  /**
   * Gets the currently loaded version notes. This returns all of the notes in one String. Note that this returns null if the version notes have not
   * been loaded yet.
   *
   * @return The currently loaded version notes, or null if notes are not loaded yet.
   * @see #downloadVersionNotes(java.lang.String)
   */
  public String getVersionNotes() {
    return versionNotes;
  }

  /**
   * Loads the current version notes from online. This retreives all of the version notes possible and stores them in one String, with each line
   * separated by a line break (\n). Note that this method blocks until finished, which depends on the user's internet speed. This also parses tokens
   * from the version notes (and does not add them into the version notes String).<br>
   * Note that if this method throws an IOException, a new PatchNotesDownloader object should be created to try again.
   *
   * @param currentVersion The current version of the program
   * @throws java.io.IOException If an error occurs while downloading the Patch Notes
   * @see #getVersionNotes()
   */
  public void downloadVersionNotes(String currentVersion) throws IOException {
    setUpdateLevel(UPDATE_ERROR); // So if something goes wrong, it's at this error level
    URLConnection inputConnection;
    InputStream textInputStream;
    BufferedReader myReader;
    URL patchNotesURL = new URL(PATCH_NOTES_LINK);
    inputConnection = patchNotesURL.openConnection();
    textInputStream = inputConnection.getInputStream();
    myReader = new BufferedReader(new InputStreamReader(textInputStream));
    String line;
    String lineSeparator = System.getProperty("line.separator", "\n");
    StringBuilder allText = new StringBuilder();
    boolean versionFound = false;
    while ((line = myReader.readLine()) != null) {
      line = line.trim();
      if (line.contains("~~~" + currentVersion + "~~~")) {
        setUpdateLevel(UPDATE_NONE);
        versionFound = true;
      }
      if (line.startsWith("TOKEN:")) {
        String d = line.substring(6).toUpperCase();
        if (d.startsWith("UPDATETYPE:")) {
          if (!versionFound) {
            String load = d.substring(11);
            switch (load) {
              case "BETA":
                setUpdateLevel(UPDATE_BETA);
                break;
              case "UPDATE":
                setUpdateLevel(UPDATE_MINOR);
                break;
              case "MAJORUPDATE":
                setUpdateLevel(UPDATE_MAJOR);
                break;
              case "INVALIDUPDATE": // Token should be at EOF for all PatchNotes
                setUpdateLevel(UPDATE_NOT_FOUND);
                break;
              default:
                System.out.println("Unknown updateType token: " + load);
                break;
            }
          }
        } else {
          System.out.println("Unknown token: " + d);
        }
      } else {
        allText.append(line);
        allText.append(lineSeparator);
      }
    }
    versionNotes = allText.toString().trim();
    System.out.println("Finished loading version notes.");
    myReader.close();
  }

  /**
   * Sets the level of the update. Note that this can only increase the level -- attempting to set the update level lower will have no effect.<br>
   * Level -1 = Error downloading Patch Notes<br>
   * Level 0 = None<br>
   * Level 1 = BETA<br>
   * Level 2 = Update<br>
   * Level 3 = Major Update<br>
   * Level 4 = Error finding version in Patch Notes
   *
   * @param level The level to set the update to
   */
  private void setUpdateLevel(int level) {
    if (updateLevel < level) {
      updateLevel = level;
    }
  }

  /**
   * Gets the current level of update available.<br>
   * See static final UPDATE variables within this class for more information.
   *
   * @return The current update level
   */
  public int getUpdateLevel() {
    return updateLevel;
  }

  public void enableBetaDownload() {
    minimumUpdateLevel = UPDATE_BETA;
  }

  /**
   * Checks whether or not an update to the program is available. Note that you'll have to run {@link #getVersionNotes(java.lang.String)} first.
   *
   * @return True if an update is available, false if not.
   * @throws IllegalStateException if Patch Notes have not been loaded yet
   */
  public boolean updateAvailable() {
    if (updateLevel == UPDATE_NOT_RUN) {
      throw new IllegalStateException("Patch Notes have not been loaded!");
    }
    return updateLevel >= minimumUpdateLevel;
  }

  /**
   * Checks whether or not the Version Notes have been loaded.
   *
   * @return True if they are loaded, false if not
   */
  public boolean areNotesLoaded() {
    return versionNotes != null;
  }

}
