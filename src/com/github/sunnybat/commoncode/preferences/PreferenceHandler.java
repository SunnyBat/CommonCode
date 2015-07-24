package com.github.sunnybat.commoncode.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Sunny
 */
public class PreferenceHandler {

  private final Preferences myPrefs;
  private final List<Preference> prefArray = new ArrayList<>();

  /**
   * Creates a new PreferenceHandler with the default root.
   *
   * @param programName The unique name of the program
   */
  public PreferenceHandler(String programName) {
    this("com/github/sunnybat/", programName.toLowerCase());
  }

  /**
   * Creates a new PreferenceHandler with the given root and node
   *
   * @param root The root to use for the Preferences, should end with /
   * @param node The unique name of the program, should not start with /
   */
  public PreferenceHandler(String root, String node) {
    if (!root.endsWith("/")) {
      root += "/";
    }
    if (node.startsWith("/")) {
      node = node.substring(1);
    }
    myPrefs = Preferences.userRoot().node(root.toLowerCase() + node.toLowerCase());
    try {
      for (String pref : myPrefs.keys()) { // Add all Preferences in node to Preference list
        Preference p = new Preference(pref, loadPreferenceValue(pref));
        prefArray.add(p);
      }
    } catch (BackingStoreException bse) {
      bse.printStackTrace();
    }
  }

  protected synchronized Object loadPreferenceValue(Preference pref) {
    return loadPreferenceValue(pref.getPrefName());
  }

  protected synchronized Object loadPreferenceValue(String prefName) {
    String value = myPrefs.get(prefName, null);
    if (value == null || value.equals("null")) {
      return null;
    }
    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
      return Boolean.parseBoolean(value);
    }
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException nfe) {
      return value;
    }
  }

  private synchronized Preference getPreferenceObjectIfExists(String prefName) {
    for (Preference p : prefArray) {
      if (p.getPrefName().equals(prefName)) {
        return p;
      }
    }
    return null;
  }

  /**
   * Gets the given Preference object associated with the given TYPES object. If it does not exist, it is created. Preference objects will always be
   * saved unless they are set to not save.
   *
   * @param prefName The TYPES object to load the Preference for
   * @return The desired Preference object
   */
  public synchronized Preference getPreferenceObject(String prefName) {
    Preference p = getPreferenceObjectIfExists(prefName);
    if (p == null) {
      p = new Preference(prefName, this);
      prefArray.add(p);
    }
    return p;
  }

  /**
   * Gets the boolean value of the given Preference. Note that this always returns false unless the Preference value is "True" (case insensitive).
   *
   * @param prefName The Preference to get the value of
   * @return True if Preference value is "true", false otherwise
   */
  public synchronized boolean getBooleanPreference(String prefName) {
    if (!isInPrefs(prefName)) {
      return false;
    }
    return Boolean.parseBoolean(String.valueOf(getPreferenceObjectIfExists(prefName).getValue()));
  }

  /**
   * Gets the integer value for the given preference.
   *
   * @param prefName The Preference to get the value of
   * @return The integer value, or -1 if preference is not an integer OR does not exist
   */
  public synchronized int getIntegerPreference(String prefName) {
    if (!isInPrefs(prefName)) {
      return -1;
    }
    try {
      return Integer.parseInt(String.valueOf(getPreferenceObjectIfExists(prefName).getValue()));
    } catch (NumberFormatException | NullPointerException e) {
      return -1;
    }
  }

  /**
   * Gets the value of the given preference. Note that if the preference does not exist OR is equal to "null", this returns null.
   *
   * @param prefName The Preference to get the value of
   * @return The Preference value, or null if it does not exist
   */
  public synchronized String getStringPreference(String prefName) {
    if (!isInPrefs(prefName)) {
      return null;
    }
    String str = String.valueOf(getPreferenceObjectIfExists(prefName).getValue());
    if (str.equalsIgnoreCase("null")) {
      str = null;
    }
    return str;
  }

  /**
   * Checks whether or not the given Preference is currently saved in the Preferences.
   *
   * @param prefName The Preference name to check for
   * @return True if found, false if not
   */
  private synchronized boolean isInPrefs(String prefName) {
    return getPreferenceObjectIfExists(prefName) != null;
  }

  /**
   * Saves all Preferences currently created. Note that this must be called for changes to the Preference to take effect.
   */
  public synchronized void savePreferences() {
    try {
      for (Preference p : prefArray) {
        if (p.getValue() == null || !p.shouldSave()) {
          myPrefs.remove(p.getPrefName());
        } else {
          myPrefs.put(p.getPrefName(), String.valueOf(p.getValue()));
        }
      }
      myPrefs.sync();
    } catch (BackingStoreException bse) {
      new com.github.sunnybat.commoncode.error.ErrorBuilder()
          .setErrorMessage("Error Saving Preferences")
          .setErrorMessage("An error has occurred while saving program Preferences. Some or all of your preferences may not be saved, or may even be corrupted.")
          .setError(bse)
          .buildWindow();
    }
  }

}
