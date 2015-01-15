package com.github.sunnybat.update;

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
public class PatchNotes {

  private String versionNotes;
  private int updateLevel = -1;
  private final String PATCH_NOTES_LINK;

  /**
   * Creates a new PatchNotes instance with the given path to the Patch Notes file.
   *
   * @param patchNotesLink The URL to the Patch Notes file
   */
  public PatchNotes(String patchNotesLink) {
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
    String versNotes = getVersionNotes();
    if (versNotes == null) {
      return null;
    }
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
   * @see #loadVersionNotes(java.lang.String)
   */
  public String getVersionNotes() {
    return versionNotes;
  }

  /**
   * Loads the current version notes from online. This retreives all of the version notes possible and stores them in one String, with each line
   * separated by a line break (\n). Note that this method blocks until finished, which depends on the user's internet speed. This also parses tokens
   * from the version notes (and does not add them into the version notes String).
   *
   * @param currentVersion The current version of the program
   * @throws java.io.IOException If an error occurs while downloading the Patch Notes
   * @see #getVersionNotes()
   */
  public void loadVersionNotes(String currentVersion) throws IOException {
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
        setUpdateLevel(0);
        versionFound = true;
      }
      if (line.startsWith("TOKEN:")) {
        String d = line.substring(6).toUpperCase();
        if (d.startsWith("UPDATETYPE:")) {
          if (!versionFound) {
            String load = d.substring(11);
            switch (load) {
              case "BETA":
                setUpdateLevel(1);
                break;
              case "UPDATE":
                setUpdateLevel(2);
                break;
              case "MAJORUPDATE":
                setUpdateLevel(3);
                break;
              case "INVALIDUPDATE":
                setUpdateLevel(4);
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
   * Level 0 = Unknown (should be treated the same as Level 2 in program)<br>
   * Level 1 = BETA<br>
   * Level 2 = Update<br>
   * Level 3 = Major Update
   *
   * @param level The level to set the update to
   */
  public void setUpdateLevel(int level) {
    if (updateLevel < level) {
      updateLevel = level;
    }
  }

  /**
   * Gets the current level of update available.<br>
   * 0 = Unknown update level<br>
   * 1 = BETA version<br>
   * 2 = Minor version<br>
   * 3 = Major version
   *
   * @return 0-3 depending on the update level available
   */
  public int getUpdateLevel() {
    return updateLevel;
  }

  /**
   * Checks whether or not an update to the program is available. Note that you'll have to run {@link #getVersionNotes(java.lang.String)} first.
   *
   * @return True if an update is available, false if not.
   */
  public boolean updateAvailable() {
    if (getUpdateLevel() == -1) {
      throw new IllegalStateException("Patch Notes have not been loaded!");
    }
    return !(getUpdateLevel() <= 0 || getUpdateLevel() >= 4);
  }

}
