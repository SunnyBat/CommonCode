package com.github.sunnybat.commoncode.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

/**
 *
 * @author Sunny
 */
public class SavePrintStream extends java.io.PrintStream {

  private final FileOutputStream fOut;
  private final Object LOCK = new Object();
  private boolean newLine;

  /**
   * Creates a new SavePrintStream. Wraps the given OutputStream. Saves all output to Output YEAR-MONTH-DAY.txt in the classpath root. If the file
   * already exists, it creates a new one with (#) appended to the name, where # is the lowest number that does not exist.
   *
   * @param out
   * @throws FileNotFoundException
   */
  public SavePrintStream(OutputStream out) throws FileNotFoundException {
    this(out, "Output " + Calendar.getInstance().get(Calendar.YEAR) + "-" // Slightly hacked-together
        + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ".txt");
  }

  /**
   * Creates a new SavePrintStream. Wraps the given OutputStream. Saves all output to the given file. If the file already exists, it creates a new one
   * with (#) appended to the name, where # is the lowest number that does not exist.
   *
   * @param out The OutputStream to save
   * @param filePath The file to save to, either relative or absolute.
   * @throws java.io.FileNotFoundException If the given File does not exist
   */
  public SavePrintStream(OutputStream out, String filePath) throws FileNotFoundException {
    super(out);
    File myFile = new File(filePath);
    int i = 2;
    while (myFile.exists()) {
      myFile = new File(filePath.substring(0, filePath.lastIndexOf(".")) + " (" + i++ + ")" + filePath.substring(filePath.lastIndexOf(".")));
    }
    fOut = new FileOutputStream(myFile);
  }

  @Override
  public void println() {
    markNewLine();
    super.println();
  }

  @Override
  public void println(Object x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(String x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(boolean x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(char x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(char[] x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(double x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(float x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(int x) {
    markNewLine();
    super.println(x);
  }

  @Override
  public void println(long x) {
    markNewLine();
    super.println(x);
  }

  /**
   * Tells the PrintStream to print out a new line the next time print() is called.
   */
  private void markNewLine() {
    synchronized (LOCK) {
      newLine = true;
    }
  }

  @Override
  public void print(String s) {
    super.print(s);
    if (fOut != null) {
      if (s == null) {
        s = "null"; // Still save "null" to stream
      }
      try {
        fOut.write(s.getBytes());
        synchronized (LOCK) {
          if (newLine) {
            fOut.write(System.getProperty("line.separator").getBytes());
            newLine = false;
          }
        }
      } catch (IOException iOException) {
      }
    }
  }
}
