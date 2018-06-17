package com.github.sunnybat.commoncode.javax.swing;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/**
 * A wrapper class for javax.swing.JFrame. Includes methods that are common to almost all of my GUIs.
 *
 * @author SunnyBat
 */
public abstract class JFrame extends javax.swing.JFrame {

  private SystemTray tray;
  private PopupMenu myMenu;
  private TrayIcon myIcon;

  public JFrame() {
    if (SystemTray.isSupported()) {
      tray = SystemTray.getSystemTray();
    }
    myMenu = new PopupMenu();
  }

  /**
   * Invokes the given Runnable on the Event Dispatch Thread and waits for it to finish.
   *
   * @param r The Runnable to execute on the EDT
   */
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

  /**
   * Sets the icon to display when the window is minimized to the tray. If programName or trayIcon is set to null, minimizing to the tray is disabled.
   *
   * @param programName The name to display when hovering over the icon in the System Tray
   * @param trayIcon The icon to display when minimized to tray
   */
  public final void setTrayIcon(final String programName, final Image trayIcon) { // CHECK: Synchronization? Just assign these on EDT?
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        tray = SystemTray.getSystemTray();
        if (programName != null && trayIcon != null) {
          myIcon = new TrayIcon(trayIcon, programName, myMenu);
          myIcon.setImageAutoSize(true);
          myIcon.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              restoreFromTray();
            }
          });
        } else {
          myIcon = null;
        }
      }
    });
  }

  /**
   * Gets the PopupMenu to display when right clicking on the icon in the System Tray. This will never be null.
   *
   * @return The PopupMenu for the System Tray icon
   */
  protected final PopupMenu getPopupMenu() {
    return myMenu;
  }

  /**
   * Minimizes the program to the System Tray if possible. If the System Tray is not supported or a tray icon has not been set, this method does
   * nothing.
   */
  public void minimizeToTray() {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        if (myIcon != null && SystemTray.isSupported()) {
          try {
            if (tray != null) {
              setExtendedState(javax.swing.JFrame.ICONIFIED);
              setVisible(false);
              tray.add(myIcon);
            }
          } catch (AWTException e) {
            e.printStackTrace();
          }
        }
      }
    });
  }

  /**
   * Restores the JFrame from the system tray.
   */
  public void restoreFromTray() {
    invokeAndWaitOnEDT(new Runnable() {
      @Override
      public void run() {
        if (tray != null) {
          tray.remove(myIcon);
        }
        setExtendedState(javax.swing.JFrame.NORMAL);
        setVisible(true); // Required =/
        setLocationRelativeTo(null);
        toFront();
      }
    });
  }

  /**
   * Disposes the window as normal. Also removes the window's icon from the System Tray if present.
   */
  @Override
  public void dispose() {
    if (tray != null) {
      tray.remove(myIcon);
    }
    super.dispose();
  }
}
