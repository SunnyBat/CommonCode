package com.github.sunnybat.commoncode.audio;

import java.io.BufferedInputStream;
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
public class Sound {

  private final LListener listener = new LListener();
  private Clip clip;
  private InputStream soundInput;

  public Sound(InputStream input) {
    if (input == null) {
      throw new IllegalArgumentException("input cannot be null");
    }
    soundInput = input;
  }

  /**
   * Plays the sound. This only allows one sound to play at a time, and resets the sound currently playing to the beginning.
   *
   * @return True if the sound was successfully started, false if not
   */
  public boolean play() {
    try {
      if (clip != null) {
        clip.stop();
        clip.setFramePosition(0);
      }
      clip = AudioSystem.getClip();
      clip.addLineListener(listener);
      InputStream bufferedIn = new BufferedInputStream(soundInput);
      AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedIn);
      clip.open(inputStream);
      clip.start();
      return true;
    } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
      e.printStackTrace();
      return false;
    }
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
