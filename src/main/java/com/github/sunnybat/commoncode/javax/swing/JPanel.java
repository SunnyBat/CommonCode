/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sunnybat.commoncode.javax.swing;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 *
 * @author SunnyBat
 */
public class JPanel extends javax.swing.JPanel {


    /**
     * Gets the text from the given component. This uses the EDT to ensure that
     * the text is updated properly, therefore this will block until the EDT is
     * available.
     *
     * @param comp The component to get text from
     * @return The text of the component
     */
    public String getTextFromComponent(JTextComponent comp) {
        if (SwingUtilities.isEventDispatchThread()) {
            return comp.getText();
        } else {
            try {
                TextPassback myPassback = new TextPassback();
                SwingUtilities.invokeAndWait(new TextSaver(comp, myPassback));
                return myPassback.myText;
            } catch (InterruptedException | InvocationTargetException e) {
                return null;
            }
        }
    }

    private static class TextSaver implements Runnable {

        private final JTextComponent textArea;
        private final TextPassback saveHere;

        public TextSaver(JTextComponent componentToRead, TextPassback saveHere) {
            this.textArea = componentToRead;
            this.saveHere = saveHere;
        }

        @Override
        public void run() {
            saveHere.save(textArea.getText());
        }
    }

    private static class TextPassback {

        private String myText;

        public void save(String text) {
            myText = text;
        }
    }
}
