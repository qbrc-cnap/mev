package edu.dfci.cccb.mev.t_test.domain.impl;

import static edu.dfci.cccb.mev.t_test.domain.prototype.AbstractTTestBuilder.FULL_FILENAME;
import static java.lang.Double.parseDouble;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;

import org.apache.commons.io.IOUtils;

import edu.dfci.cccb.mev.io.implementation.TemporaryFolder;
import edu.dfci.cccb.mev.t_test.domain.prototype.AbstractTTest;

public class FileBackedTTest extends AbstractTTest implements AutoCloseable{

  
  private @Getter final File full;
  private @Getter final TemporaryFolder tempFolder;

  public FileBackedTTest (TemporaryFolder tempFolder) {
    this.tempFolder = tempFolder;
    this.full = new File (this.tempFolder, FULL_FILENAME);
  }
  
  @SneakyThrows
  public static FileBackedTTest from (InputStream results) {
    FileBackedTTest result = new FileBackedTTest (new TemporaryFolder ());
    try (OutputStream full = new BufferedOutputStream (new FileOutputStream (result.full));
         BufferedInputStream in = new BufferedInputStream (results)) {
      IOUtils.copy (in, full);
    }
    return result;
  }

  @SneakyThrows
  public void to (OutputStream out) {
    try (InputStream full = new BufferedInputStream (new FileInputStream (this.full));
         OutputStream o = new BufferedOutputStream (out)) {
      IOUtils.copy (full, o);
    }
  }
  
  @Override
  public Iterable<Entry> fullResults () {
    return iterateEntries (full);
  }

  private Iterable<Entry> iterateEntries (final File file) {
    return new Iterable<Entry> () {

      /* (non-Javadoc)
       * @see java.lang.Iterable#iterator() */
      @Override
      @SneakyThrows (IOException.class)
      public Iterator<Entry> iterator () {
        return new Iterator<Entry> () {

          private final BufferedReader reader = new BufferedReader (new FileReader (file));
          private String current = null;

          @Override
          @Synchronized
          @SneakyThrows (IOException.class)
          public boolean hasNext () {
            return current == null ? (current = reader.readLine ()) != null : true;
          }

          @Override
          @Synchronized
          public Entry next () {
            hasNext ();
            Entry result = parse (current);
            current = null;
            return result;
          }

          @Override
          public void remove () {
            throw new UnsupportedOperationException ();
          }
        };
      }
    };
  }
  
  private String string (int index, String[] split) {
    return split[index];
  }

  private Double number (int index, String[] split) {
    String value = string (index, split);
    if ("Inf".equals (value))
      return Double.POSITIVE_INFINITY;
    else if ("-Inf".equals (value))
      return Double.NEGATIVE_INFINITY;
    else if ("NA".equals (value))
      return Double.NaN;
    else
      return parseDouble (value);
  }
  
  private Entry parse (String line) {
    final String[] split = line.split ("\t");
    return new SimpleEntry (number(1, split), string(0, split), number(2, split));
  }

  
  /* (non-Javadoc)
   * @see java.lang.Object#finalize() */
  @Override
  protected void finalize () throws Throwable {
    close ();
  }
  @Override
  public void close () throws Exception {
    tempFolder.close ();
  }

}
