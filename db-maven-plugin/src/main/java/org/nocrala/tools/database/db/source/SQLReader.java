package org.nocrala.tools.database.db.source;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SQLReader implements Closeable {

  private BufferedReader r;
  private String delimiter;

  private String encloser;
  private String line;
  private int pos;
  private StringBuilder sb;

  public SQLReader(final File f, final String delimiter) throws IOException {
    this.r = new BufferedReader(new FileReader(f));
    this.sb = new StringBuilder();
    this.encloser = null;
    nextLine();
  }

  private void nextLine() throws IOException {
    this.line = this.r.readLine();
    this.pos = 0;
  }

  public String read() throws IOException {
    if (this.line == null) {
      return null;
    }

    this.sb = new StringBuilder();

    int sep = 0;
    while (this.line != null && (sep = findDelimiter()) == -1) {
      this.sb.append(this.line.substring(this.pos));
      nextLine();
    }

    if (this.line != null) {
      this.sb.append(this.line.substring(this.pos, sep));
      this.pos = sep + this.delimiter.length();
    }
    return this.sb.toString();
  }

  private static final String SQ = "'";
  private static final String DQ = "\"";
  private static final String COMMENT = "--";

  private int findDelimiter() {

    int d = -1;
    while (d == -1 && this.pos < this.line.length()) {

      if (this.encloser == null) {
        int sq = this.line.indexOf(SQ, this.pos);
        int dq = this.line.indexOf(DQ, this.pos);
        int cm = this.line.indexOf(COMMENT, this.pos);
        int del = this.line.indexOf(this.delimiter, this.pos);
        int min = min(min(sq, dq), del);
        if (min == -1) {
          return -1;
        } else if (min == sq) {
          this.encloser = SQ;
          this.pos = min + SQ.length();
        } else if (min == dq) {
          this.encloser = DQ;
          this.pos = min + DQ.length();
        } else if (min == dq) {
          
        } else { // del
          d = del;
          this.pos = min + this.delimiter.length();
        }
      } else {
        int enc = this.line.indexOf(this.encloser, this.pos);
        int del = this.line.indexOf(this.delimiter, this.pos);
        int min = min(enc, del);
        if (min == -1) {
          return -1;
        } else if (min == enc) {
          this.pos = min + this.encloser.length();
          this.encloser = null;
        } else { // del
          d = del;
          this.pos = min + this.delimiter.length();
        }
      }

    }

    int enc = this.line.indexOf(this.encloser, this.pos);
    int del = this.line.indexOf(this.delimiter, this.pos);
    return del;
  }

  private int min(final int a, final int b) {
    return a == -1 ? b : (b == -1 ? a : Math.min(a, b));
  }

  @Override
  public void close() throws IOException {
    this.r.close();
  }

}
