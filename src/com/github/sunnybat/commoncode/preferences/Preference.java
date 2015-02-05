package com.github.sunnybat.commoncode.preferences;

/**
 *
 * @author Sunny
 */
public class Preference {

  protected Preference(String pref, PreferenceHandler handler) {
    name = pref;
    value = handler.loadPreferenceValue(pref);
    shouldSave = handler.isInPrefs(pref);
  }

  private final String name;
  private Object value;
  private boolean shouldSave;

  public String getPrefName() {
    return name;
  }

  /**
   * Checks whether or not the given Preference should be saved.
   *
   * @return True to save, false to not
   */
  public boolean shouldSave() {
    return shouldSave;
  }

  /**
   * Sets whether or not this Preference should be saved. Preferences that are not saved will be deleted (even if previously saved) when Preferences
   * are saved.
   *
   * @param save True to save Preference, false to not
   */
  public void setShouldSave(boolean save) {
    shouldSave = save;
  }

  /**
   * Returns the expected Object from the preference. This currently returns a boolean, integer, or String. Note that this value is not necessarily
   * the value saved in the Preferences node. To ensure that it is, call {@link PreferenceHandler#savePreferences()}.
   *
   * @return The Object associated with the Preference, or null if none has been set
   */
  public Object getValue() {
    return value;
  }

  /**
   * Sets the value for this Preference. Note that this value will be saved until {@link PreferenceHandler#savePreferences()} is called.
   *
   * @param val The value to set this Preference to
   */
  public void setValue(Object val) {
    if (val == null) {
      System.out.println("NOTE: Object val set to null!");
    }
    value = val;
    System.out.println(name + " -- Value = " + value);
  }

  @Override
  public String toString() {
    return name + ": value = " + value + " -- shouldSave = " + shouldSave;
  }

}
