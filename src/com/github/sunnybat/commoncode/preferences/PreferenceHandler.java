package com.github.sunnybat.commoncode.preferences;

import java.util.List;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Sunny
 */
public class PreferenceHandler {

  private final Preferences myPrefs;
  private final List<Preference> prefArray = new ArrayList<>();

  public PreferenceHandler(String programName) {
    myPrefs = Preferences.userRoot().node("com/github/sunnybat/" + programName);
  }

  protected synchronized Object loadPreferenceValue(Preference pref) {
    return loadPreferenceValue(pref.getPrefName());
  }

  protected synchronized Object loadPreferenceValue(String prefName) {
    String value = myPrefs.get(prefName, null);
    if (value == null) {
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

  /**
   * Gets the boolean value of the given Preference. Note that this always returns false unless the Preference value is "True" (case insensitive).
   *
   * @param prefName The Preference to get the value of
   * @return True if Preference value is "true", false otherwise
   */
  public synchronized boolean getBooleanPreference(String prefName) {
    return Boolean.parseBoolean(String.valueOf(getPreferenceObject(prefName).getValue()));
  }

  /**
   * Gets the integer value for the given preference.
   *
   * @param prefName The Preference to get the value of
   * @return The integer value, or -1 if preference is not an integer OR does not exist
   */
  public synchronized int getIntegerPreference(String prefName) {
    try {
      return Integer.parseInt(String.valueOf(getPreferenceObject(prefName).getValue()));
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
    String str = String.valueOf(getPreferenceObject(prefName).getValue());
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
  public synchronized boolean isInPrefs(String prefName) {
    try {
      String prefT = prefName;
      String[] keys = myPrefs.keys();
      for (String k : keys) {
        if (k.equalsIgnoreCase(prefT)) {
          return true;
        }
      }
    } catch (BackingStoreException bse) {
      bse.printStackTrace();
    }
    return false;
  }

  /**
   * Gets the given Preference object associated with the given TYPES object.
   *
   * @param prefName The TYPES object to load the Preference for
   * @return The desired Preference object
   */
  public synchronized Preference getPreferenceObject(String prefName) {
    for (Preference p : prefArray) {
      if (p.getPrefName().equals(prefName)) {
        return p;
      }
    }
    Preference p = new Preference(prefName, this);
    prefArray.add(p);
    return p;
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
