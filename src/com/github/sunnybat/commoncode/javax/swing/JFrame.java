package com.github.sunnybat.commoncode.javax.swing;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * A wrapper class for javax.swing.JFrame. Includes methods that are common to almost all of my GUIs.
 *
 * @author SunnyBat
 */
public abstract class JFrame extends javax.swing.JFrame {

  /**
   * Shows the current window in the center of the screen. Executes on the EDT and blocks until the GUI is visible.
   */
  public void showWindow() {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        setLocationRelativeTo(null);
        setVisible(true);
      }
    });
  }

  protected void invokeAndWaitOnEDT(Runnable r) {
    if (SwingUtilities.isEventDispatchThread()) {
      r.run(); // Just run it, we're on the EDT already
    } else {
      try {
        SwingUtilities.invokeAndWait(r);
      } catch (InterruptedException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }
}
