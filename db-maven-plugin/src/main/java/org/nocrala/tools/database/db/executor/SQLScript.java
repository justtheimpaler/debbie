package org.nocrala.tools.database.db.executor;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SQLScript implements Closeable {

  private BufferedReader r;
  private String delimiter;

  private int startLineNumber;
  private int startPos;

  private String encloser;
  private String line;
  private int lineNumber;
  private int pos;
  private StringBuilder sb;

  public SQLScript(final File f, final String delimiter) throws IOException {
    System.out.println("-------------------- SQL SCRIPT");
    this.r = new BufferedReader(new FileReader(f));
    this.sb = new StringBuilder();
    this.encloser = null;
    this.delimiter = delimiter;
    this.lineNumber = 0;
    nextLine();
  }

  private void nextLine() throws IOException {
    this.lineNumber++;
    System.out.println("-------------------- lineNumber="+this.lineNumber);
    this.line = this.r.readLine();
    this.pos = 0;
  }

  public ScriptStatement readStatement() throws IOException {
    String sql;
    do {
      if (this.line == null) {
        return null;
      }
      this.sb = new StringBuilder();
      readUntilNextDelimiter();
      sql = this.sb.toString().trim();
    } while (sql.isEmpty());
    return new ScriptStatement(this.startLineNumber, this.startPos, sql);
  }

  private static final String SQ = "'";
  private static final String DQ = "\"";
  private static final String COMMENT = "--";

  private void readUntilNextDelimiter() throws IOException {

    this.startLineNumber = this.lineNumber;
    this.startPos = this.pos;

    boolean delimiterFound = false;
    while (!delimiterFound && this.line != null) {

      if (this.encloser == null) {
        int sq = this.line.indexOf(SQ, this.pos);
        int dq = this.line.indexOf(DQ, this.pos);
        int cm = this.line.indexOf(COMMENT, this.pos);
        int del = this.line.indexOf(this.delimiter, this.pos);
        int min = min(min(sq, dq), del);
        if (min == -1) { // no special symbol until end of the line
          this.sb.append(this.line.substring(this.pos));
          this.sb.append("\n");
          nextLine();
        } else if (min == sq) { // single quote found
          this.sb.append(this.line.substring(this.pos, min + SQ.length()));
          this.encloser = SQ;
          this.pos = min + SQ.length();
        } else if (min == dq) { // double quote found
          this.sb.append(this.line.substring(this.pos, min + DQ.length()));
          this.encloser = DQ;
          this.pos = min + DQ.length();
        } else if (min == cm) { // comment found
          this.sb.append(this.line.substring(this.pos, min));
          this.sb.append("\n");
          nextLine();
        } else { // delimiter found at: del
          delimiterFound = true;
          this.sb.append(this.line.substring(this.pos, min));
          this.pos = min + this.delimiter.length();
          return;
        }
      } else {
        int min = this.line.indexOf(this.encloser, this.pos);
        if (min == -1) { // no end of quote until end of the line
          this.sb.append(this.line.substring(this.pos));
          this.sb.append("\n");
          nextLine();
        } else { // end of quote found
          this.sb.append(this.line.substring(this.pos, min + this.encloser.length()));
          this.pos = min + this.encloser.length();
          this.encloser = null;
        }
      }
    }

  }

  private int min(final int a, final int b) {
    return a == -1 ? b : (b == -1 ? a : Math.min(a, b));
  }

  @Override
  public void close() throws IOException {
    this.r.close();
  }

  // Classes

  public static class ScriptStatement {

    private int line;
    private int col;
    private String sql;

    public ScriptStatement(final int line, final int col, final String sql) {
      this.line = line;
      this.col = col;
      this.sql = sql;
    }

    public int getLine() {
      return line;
    }

    public int getCol() {
      return col;
    }

    public String getSql() {
      return sql;
    }

  }

}
