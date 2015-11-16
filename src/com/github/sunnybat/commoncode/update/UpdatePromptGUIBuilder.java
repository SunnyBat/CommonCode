package com.github.sunnybat.commoncode.update;

/**
 *
 * @author SunnyBat
 */
public class UpdatePromptGUIBuilder {

  private boolean isBuilt;
  private int updateLevel;
  private double updateSize;
  private String programName;
  private String version;
  private String versionNotes;

  public UpdatePromptGUIBuilder setProgramName(String programName) {
    this.programName = programName;
    return this;
  }

  public UpdatePromptGUIBuilder setUpdateLevel(int updateLevel) {
    this.updateLevel = updateLevel;
    return this;
  }

  public UpdatePromptGUIBuilder setUpdateSize(double updateSize) {
    this.updateSize = updateSize;
    return this;
  }

  public UpdatePromptGUIBuilder setVersion(String version) {
    this.version = version;
    return this;
  }

  public UpdatePromptGUIBuilder setVersionNotes(String versionNotes) {
    this.versionNotes = versionNotes;
    return this;
  }

  public UpdatePrompt build() {
    if (isBuilt) {
      throw new IllegalStateException("GUI has already been built");
    } else {
      isBuilt = true;
      return new UpdatePrompt(programName, updateSize, updateLevel, version, versionNotes);
    }
  }

}
