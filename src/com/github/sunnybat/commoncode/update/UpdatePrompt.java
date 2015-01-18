package com.github.sunnybat.commoncode.update;

import java.util.concurrent.CountDownLatch;

/**
 * Basic UpdatePrompt GUI.
 *
 * @author SunnyBat
 */
public class UpdatePrompt extends javax.swing.JFrame {

  private final CountDownLatch countdown = new CountDownLatch(1);
  private boolean updateProgram;
  private final PatchNotes patchNotesWindow;

  /**
   * Creates a new Update form. Note that the size and update level are set to unknown, and the Patch Notes button will be unavailable.
   */
  public UpdatePrompt() {
    this(-1, -2, null, null);
  }

  /**
   * Creates a new Update form. Note that the Patch Notes button will be unavailable.
   *
   * @param updateSize The update size to display (in bytes)
   * @param updateLevel The update level to display (see PatchNotesDownloader constants)
   */
  public UpdatePrompt(double updateSize, int updateLevel) {
    this(updateSize, updateLevel, null, null);
  }

  /**
   * Creates a new Update form.
   *
   * @param version The current version of the program
   * @param versionNotes The Version Notes to display in the Patch Notes window
   */
  public UpdatePrompt(String version, String versionNotes) {
    this(-1, -2, version, versionNotes);
  }

  public UpdatePrompt(final double updateSize, final int updateLevel, String version, String versionNotes) {
    if (versionNotes == null) {
      patchNotesWindow = null;
    } else {
      patchNotesWindow = new PatchNotes(this, version, versionNotes);
    }
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        initComponents();
        customComponents(updateSize, updateLevel);
      }
    });
  }

  /**
   * Sets up GUI
   */
  private void customComponents(double updateSize, int updateLevel) {
    if (patchNotesWindow == null) {
      JBPatchNotes.setVisible(false);
    }
    JPBProgressBar.setVisible(false);
    pack();
    setLocationRelativeTo(null);
    if (updateSize == -1) {
      setStatusLabelText("[Unknown Update Size]");
    } else {
      setStatusLabelText("Update Size: " + ((double) ((int) ((double) updateSize / 1024 / 1024 * 100)) / 100) + "MB");
    }
    setYesButtonText(updateLevel);
  }

  /**
   * Shows the JProgressBar for download progress.
   */
  private void updateInit() {
    //JLStatus.setVisible(true);
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JPBProgressBar.setVisible(true);
        pack();
      }
    });
  }

  /**
   * Call to display the window.
   */
  public void showWindow() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setVisible(true);
      }
    });
  }

  /**
   * Waits for the user to select whether or not to update the program. Note this method blocks until a choice has been made.
   *
   * @throws InterruptedException If the CountDownLatch throws it
   */
  public void waitForClose() throws InterruptedException {
    countdown.await();
  }

  /**
   * Returns whether or not the user has requested to update the program.
   *
   * @return True if user has requested an update, false if not
   * @throws IllegalStateException If the user has not selected an option
   * @see #waitForClose() To wait for user to select an option
   */
  public boolean shouldUpdateProgram() {
    if (countdown.getCount() != 0) {
      throw new IllegalStateException("GUI has not been closed yet.");
    }
    synchronized (this) {
      return updateProgram;
    }
  }

  /**
   * Sets whether or not the Show Patch Notes button is enabled or disabled.
   *
   * @param enabled True to enable, false to disable
   */
  public void setPatchNotesButtonEnabled(final boolean enabled) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JBPatchNotes.setEnabled(enabled);
      }
    });
  }

  /**
   * Sets the status label test. Recommended to use for status, download percentage, and error information
   *
   * @param text The text to set
   */
  public void setStatusLabelText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JLStatus.setText(text);
      }
    });
  }

  /**
   * Updates the JProgressBar. Note that the user must select an option before calling this option.
   *
   * @param percent The percent (0-100) to update
   * @throws IllegalStateException If the JProgressBar is not visible
   */
  public void updateProgress(final int percent) {
    if (!JPBProgressBar.isVisible()) {
      throw new IllegalStateException("JProgressBar is not visible. Ensure update has been selected by user.");
    }
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JPBProgressBar.setValue(percent);
        setStatusLabelText("Progress: " + percent + "%");
      }
    });
  }

  @Override
  public final void dispose() {
    countdown.countDown();
    if (patchNotesWindow != null) {
      patchNotesWindow.dispose();
    }
    super.dispose();
  }

  /**
   * Sets the yes button text.
   *
   * @param text The text to set
   */
  public void setYesButtonText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JBYes.setText(text);
      }
    });
  }

  /**
   * Sets the yes button text.
   *
   * @param updateLevel The update level to use
   * @see PatchNotesDownloader
   */
  public void setYesButtonText(final int updateLevel) {
    if (updateLevel == PatchNotesDownloader.UPDATE_BETA) {
      setYesButtonText("Yes (BETA Version)");
    } else if (updateLevel == PatchNotesDownloader.UPDATE_MINOR) {
      setYesButtonText("Yes (Minor Update)");
    } else if (updateLevel == PatchNotesDownloader.UPDATE_MAJOR) {
      setYesButtonText("Yes (MAJOR Update)");
    } else {
      setYesButtonText("Yes (Unknown)");
    }
  }

  /**
   * Called when the user requests the Patch Notes to be shown.<br>
   * Note that this runs on the EDT.
   */
  public void showPatchNotes() {
    JBPatchNotes.setEnabled(false);
    patchNotesWindow.setVisible(true);
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    JBYes = new javax.swing.JButton();
    JBNo = new javax.swing.JButton();
    JLStatus = new javax.swing.JLabel();
    JPBProgressBar = new javax.swing.JProgressBar();
    JBPatchNotes = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setTitle("Program Update");
    setAlwaysOnTop(true);
    setResizable(false);

    jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel1.setText("Program Update Found!");

    jLabel2.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    jLabel2.setText("Would you like to update to the most recent version?");

    JBYes.setText("Yes! (Recommended)");
    JBYes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBYesActionPerformed(evt);
      }
    });

    JBNo.setText("No!");
    JBNo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBNoActionPerformed(evt);
      }
    });

    JLStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    JLStatus.setText("Downloading Update:");

    JPBProgressBar.setToolTipText("Please wait, downloading the latest version...");

    JBPatchNotes.setText("View Patch Notes");
    JBPatchNotes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBPatchNotesActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(JBPatchNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(JLStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
          .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
          .addGroup(layout.createSequentialGroup()
            .addComponent(JBYes, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(JBNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
          .addComponent(JPBProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(jLabel1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(JBYes)
          .addComponent(JBNo))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JBPatchNotes)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JLStatus)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JPBProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void JBYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBYesActionPerformed
    // TODO add your handling code here:
    //UpdateHandler.startUpdatingProgram();
    synchronized (this) {
      updateProgram = true;
    }
    updateInit();
    JBYes.setVisible(false);
    JBNo.setVisible(false);
    JBPatchNotes.setVisible(false);
    countdown.countDown();
  }//GEN-LAST:event_JBYesActionPerformed

  private void JBNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBNoActionPerformed
    dispose();
  }//GEN-LAST:event_JBNoActionPerformed

  private void JBPatchNotesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBPatchNotesActionPerformed
    showPatchNotes();
  }//GEN-LAST:event_JBPatchNotesActionPerformed
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton JBNo;
  private javax.swing.JButton JBPatchNotes;
  private javax.swing.JButton JBYes;
  private javax.swing.JLabel JLStatus;
  private javax.swing.JProgressBar JPBProgressBar;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  // End of variables declaration//GEN-END:variables
}
