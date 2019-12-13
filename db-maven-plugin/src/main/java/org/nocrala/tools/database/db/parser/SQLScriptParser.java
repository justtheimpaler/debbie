package org.nocrala.tools.database.db.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;

import org.nocrala.tools.database.db.executor.Delimiter;
import org.nocrala.tools.database.db.executor.Feedback;

public class SQLScriptParser {

  private static final String COMMENT = "--";
  private static final String DELIMITER_DIRECTIVE = "@delimiter";
  private static final String DELIMITER_SOLO = "solo";
  private static final String DELIMITER_INSENSITIVE = "insensitive";

  private BufferedReader r;
  private Delimiter delimiter;
  private Feedback feedback;

  private Integer startLineNumber;
  private int startPos;

  private QuoteOpening quote;
  private String line;
  private int lineNumber;
  private int pos;
  private StringBuilder sb;

  // -- @delimiter ;
  // -- @delimiter go solo insensitive
  // -- @delimiter // solo

  public SQLScriptParser(final Reader r, final Delimiter delimiter, final Feedback feedback) throws IOException {
    this.r = new BufferedReader(r);
    this.sb = new StringBuilder();
    this.quote = null;
    this.delimiter = delimiter;
    this.feedback = feedback;
    this.lineNumber = 0;
    nextLine();
  }

  private void nextLine() throws IOException {
    boolean directiveProcessed;
    do {
      directiveProcessed = false;
      this.line = this.r.readLine();
      this.lineNumber++;
      if (this.line != null) {
        Iterator<String> it = Arrays.stream(this.line.split("\\s+")).filter(s -> !s.trim().isEmpty()).iterator();
        if (it.hasNext() && COMMENT.equals(it.next())) {
          if (it.hasNext() && DELIMITER_DIRECTIVE.equals(it.next())) {
            if (it.hasNext()) {
              String token = it.next();
              boolean solo = false;
              boolean insensitive = false;
              if (it.hasNext()) {
                String p2 = it.next();
                if (DELIMITER_SOLO.equals(p2)) {
                  solo = true;
                  if (it.hasNext()) {
                    String p3 = it.next();
                    if (DELIMITER_INSENSITIVE.equals(p3)) {
                      insensitive = true;
                    } else {
                      directiveError("expected '" + DELIMITER_INSENSITIVE + "' but found '" + p3 + "'");
                    }
                  }
                } else {
                  directiveError("expected '" + DELIMITER_SOLO + "' but found '" + p2 + "'");
                }
              }
              this.delimiter = new Delimiter(token, !insensitive, solo);
              directiveProcessed = true;
            } else {
              directiveError("token not found");
            }
          }
        }
      }
    } while (directiveProcessed);
    this.pos = 0;
  }

  private void directiveError(final String message) {
    this.feedback
        .error("Invalid delimiter directive at line " + this.lineNumber + ": " + message + " -- directive skipped.");
    this.feedback.info("Delimiter directive line must take the form: -- @delimiter token [[solo] insensitive]");
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

  private void readUntilNextDelimiter() throws IOException {

    if (this.delimiter.isSolo()) {

      // Solo delimiter

      while (this.line != null
          && !(this.delimiter.isCaseSensitiveSolo() && this.line.trim().equals(this.delimiter.getToken())
              || this.line.trim().equalsIgnoreCase(this.delimiter.getToken()))) {
        appendRestOfLine();
        nextLine();
      }
      if (this.line != null) {
        nextLine();
      }

    } else {

      // Inline delimiter

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
