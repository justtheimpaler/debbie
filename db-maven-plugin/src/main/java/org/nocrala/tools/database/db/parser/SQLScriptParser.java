package org.nocrala.tools.database.db.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.nocrala.tools.database.db.executor.Delimiter;

public class SQLScriptParser {

  private BufferedReader r;
  private Delimiter delimiter;

  private Integer startLineNumber;
  private int startPos;

  private QuoteOpening quote;
  private String line;
  private int lineNumber;
  private int pos;
  private StringBuilder sb;

  public SQLScriptParser(final Reader r, final Delimiter delimiter) throws IOException {
    this.r = new BufferedReader(r);
    this.sb = new StringBuilder();
    this.quote = null;
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

  public ScriptSQLStatement readStatement() throws IOException {
    String sql;
    do {
      if (this.line == null) {
        return null;
      }
      this.sb = new StringBuilder();
      readUntilNextDelimiter();
      sql = this.sb.toString().trim();
    } while (sql.isEmpty());
    return new ScriptSQLStatement(this.startLineNumber, this.startPos + 1, sql);
  }

  private static final String COMMENT = "--";

  private void readUntilNextDelimiter() throws IOException {

    // System.out.println(">>> --- readUntilNextDelimiter --- this.pos=" +
    // this.pos);

    this.startLineNumber = null;
    this.startPos = this.pos;
    this.quote = null;

    boolean delimiterFound = false;
    while (!delimiterFound && this.line != null) {

      if (this.quote == null) {
        QuoteOpening qo = QuoteFinder.find(this.line, this.pos);
        // System.out.println(">>> qo=" + qo);
        int cm = this.line.indexOf(COMMENT, this.pos);
        int del = this.line.indexOf(this.delimiter.getToken(), this.pos);
        int min = Utl.min(qo == null ? -1 : qo.getPos(), Utl.min(del, cm));
        if (min == -1) { // no special symbol until end of the line
          appendRestOfLine();
          nextLine();
        } else if (qo != null && min == qo.getPos()) { // quote found
          appendSegment(min + qo.getParser().getOpening().length());
          this.quote = qo;
        } else if (min == cm) { // comment found
          appendSegment(min);
          this.sb.append("\n");
          nextLine();
        } else { // delimiter found at: del
          // System.out.println(">>> delimiter found at " + min);
          delimiterFound = true;
          appendSegment(min);
          this.pos = min + this.delimiter.getToken().length();
          return;
        }
      } else {
        QuoteClosing c = this.quote.getParser().findClosing(this.line, this.pos);
        if (c == null) { // no end of quote until end of the line
          appendRestOfLine();
          nextLine();
        } else { // end of quote found
          appendSegment(c.getPos() + c.getClosing().length());
          this.pos = c.getPos() + c.getClosing().length();
          this.quote = null;
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

}
