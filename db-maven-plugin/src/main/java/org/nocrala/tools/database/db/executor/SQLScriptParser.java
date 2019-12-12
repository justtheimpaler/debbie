package org.nocrala.tools.database.db.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class SQLScriptParser {

  private BufferedReader r;
  private String delimiter;

  private Integer startLineNumber;
  private int startPos;

  private String encloser;
  private String line;
  private int lineNumber;
  private int pos;
  private StringBuilder sb;

  public SQLScriptParser(final Reader r, final String delimiter) throws IOException {
    // System.out.println("-------------------- SQL SCRIPT");
    this.r = new BufferedReader(r);
    this.sb = new StringBuilder();
    this.encloser = null;
    this.delimiter = delimiter;
    this.lineNumber = 0;
    nextLine();
  }

  private void nextLine() throws IOException {
    this.lineNumber++;
    // System.out.println("-------------------- lineNumber=" + this.lineNumber);
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
    return new ScriptStatement(this.startLineNumber, this.startPos + 1, sql);
  }

  private static final String SQ = "'";
  private static final String DQ = "\"";
  private static final String COMMENT = "--";

  private void readUntilNextDelimiter() throws IOException {

    System.out.println(">>> this.pos="+this.pos);

    this.startLineNumber = null;
    this.startPos = this.pos;

    boolean delimiterFound = false;
    while (!delimiterFound && this.line != null) {

      if (this.encloser == null) {
        // System.out.println(">>> pos=" + this.pos + " '" + this.line + "'");
        int sq = this.line.indexOf(SQ, this.pos);
        int dq = this.line.indexOf(DQ, this.pos);
        int cm = this.line.indexOf(COMMENT, this.pos);
        int del = this.line.indexOf(this.delimiter, this.pos);
        int min = Utl.min(Utl.min(sq, dq), Utl.min(del, cm));
        // System.out.println(">>> sq=" + sq + " dq=" + dq + " del=" + del + " cm=" + cm
        // + " min=" + min);
        if (min == -1) { // no special symbol until end of the line
          appendRestOfLine();
          nextLine();
        } else if (min == sq) { // single quote found
          appendSegment(min + SQ.length());
          this.encloser = SQ;
        } else if (min == dq) { // double quote found
          appendSegment(min + DQ.length());
          this.encloser = DQ;
        } else if (min == cm) { // comment found
          appendSegment(min);
          this.sb.append("\n");
          nextLine();
        } else { // delimiter found at: del
          delimiterFound = true;
          appendSegment(min);
          this.pos = min + this.delimiter.length();
          return;
        }
      } else {
        int min = this.line.indexOf(this.encloser, this.pos);
        if (min == -1) { // no end of quote until end of the line
          appendRestOfLine();
          nextLine();
        } else { // end of quote found
          appendSegment(min + this.encloser.length());
          this.pos = min + this.encloser.length();
          this.encloser = null;
        }
      }
    }

  }

  private void appendRestOfLine() {
    // System.out.println("### [y] pos=" + this.pos);
    if (this.startLineNumber == null) {
      int nb = findFirstNonBlankChar(this.line, this.pos, this.line.length());
      // System.out.println("### [1] nb=" + nb + " '" + this.line.substring(this.pos)
      // + "'");
      if (nb != -1) {
        this.startLineNumber = this.lineNumber;
        // System.out.println("### this.startLineNumber=" + this.startLineNumber);
        this.startPos = nb;
      }
    }
    this.sb.append(this.line.substring(this.pos));
    this.sb.append("\n");
  }

  private void appendSegment(final int end) {
    // System.out.println("### [x] pos=" + this.pos + " end=" + end);
    if (this.startLineNumber == null) {
      int nb = findFirstNonBlankChar(this.line, this.pos, end);
      // System.out.println("### [2] nb=" + nb + " '" + this.line.substring(this.pos)
      // + "'");
      if (nb != -1) {
        this.startLineNumber = this.lineNumber;
        // System.out.println("### this.startLineNumber=" + this.startLineNumber);
        this.startPos = this.pos;
      }
    }
    this.sb.append(this.line.substring(this.pos, end));
    this.pos = end;
  }

  private int findFirstNonBlankChar(final String s, final int start, final int end) {
    if (s == null || start > s.length() || start < 0 || end > s.length() || end < 0 || end < start) {
      return -1;
    }
    int p = start;
    while (p < end) {
      if (!Character.isSpaceChar(s.charAt(p))) {
        return p;
      }
      p++;
    }
    return -1;
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

    public String toString() {
      return "line " + this.line + ", col " + this.col + ":" + this.sql;
    }

  }

}
