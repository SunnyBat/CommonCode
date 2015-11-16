package com.github.sunnybat.commoncode.utilities;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author SunnyBat
 */
public class SoundClip {

  private final LListener listener = new LListener();
  private File inputFile;
  private Clip clip;

  /**
   * Creates a new SoundClip object. This currently only supports WAV files.
   *
   * @param audioFile The audio file to use
   * @throws IllegalArgumentException If audioFile is null, does not exist, or is not a WAV file
   */
  public SoundClip(File audioFile) {
    if (audioFile == null || !audioFile.exists() || !audioFile.getName().toLowerCase().endsWith(".wav")) {
      throw new IllegalArgumentException("Invalid audio file");
    }
    inputFile = audioFile;
  }

  /**
   * Plays the alarm. This method only allows one sound to play at a time, and resets the sound currently playing to the beginning.
   *
   * @return True if the alarm was successfully started, false if not
   */
  public boolean playSound() {
    try {
      if (clip != null) {
        clip.stop();
        clip.setFramePosition(0);
      }
      clip = AudioSystem.getClip();
      clip.addLineListener(listener);
      InputStream audioSrc = new BufferedInputStream(new FileInputStream(inputFile));
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(audioSrc);
      clip.open(inputStream);
      clip.start();
      return true;
    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * A custom LineListener implementation used for stopping the default clip when it's told to stop
   */
  private class LListener implements LineListener {

    @Override
    public void update(LineEvent le) {
      if (le.getType() == LineEvent.Type.STOP) {
        clip.removeLineListener(listener);
        clip.close();
      }
    }

  }

}
