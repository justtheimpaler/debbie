package org.nocrala.tools.debbie.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Iterator;

import org.nocrala.tools.debbie.executor.Delimiter;
import org.nocrala.tools.debbie.executor.Feedback;

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
  private boolean directiveProcessed;

  // -- @delimiter ;
  // -- @delimiter go solo insensitive
  // -- @delimiter // solo

  public SQLScriptParser(final Reader r, final Delimiter delimiter, final Feedback feedback)
      throws IOException, InvalidSQLScriptException {
    this.r = new BufferedReader(r);
    this.sb = new StringBuilder();
    this.quote = null;
    this.delimiter = delimiter;
    this.feedback = feedback;
    this.lineNumber = 0;
    nextLine();
  }

  private void nextLine() throws IOException, InvalidSQLScriptException {
    this.directiveProcessed = false;
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
            this.directiveProcessed = true;
          } else {
            directiveError("token not found");
          }
        }
      }
    }
    this.pos = 0;
  }

  private void directiveError(final String message) throws InvalidSQLScriptException {
    throw new InvalidSQLScriptException(this.lineNumber, "Invalid delimiter directive: " + message
        + "\nA directive line must take the form: -- @delimiter <token> [[solo] insensitive]");
  }

  public ScriptSQLStatement readStatement() throws IOException, InvalidSQLScriptException {
    String sql;
    do {
      if (this.line == null) {
        return null;
      }
      this.sb = new StringBuilder();
      readUntilNextDelimiterOrDirective();
      sql = this.sb.toString().trim();
    } while (sql.isEmpty());
    return new ScriptSQLStatement(this.startLineNumber, this.startPos + 1, sql);
  }

  private void readUntilNextDelimiterOrDirective() throws IOException, InvalidSQLScriptException {

    this.startLineNumber = null;
    this.startPos = this.pos;
    this.quote = null;

    if (this.delimiter.isSolo()) {

      // Solo delimiter

      while (this.line != null
          && !(this.delimiter.isCaseSensitiveSolo() && this.line.trim().equals(this.delimiter.getToken())
              || this.line.trim().equalsIgnoreCase(this.delimiter.getToken()))) {
        appendRestOfLine();
        nextLine();
        if (this.directiveProcessed) {
          return;
        }
      }
      if (this.line != null) {
        nextLine();
        if (this.directiveProcessed) {
          return;
        }
      }

    } else {

      // Inline delimiter

      boolean delimiterFound = false;
      while (!delimiterFound && this.line != null) {

        if (this.quote == null) {
          QuoteOpening qo = QuoteFinder.find(this.line, this.pos);
          int cm = this.line.indexOf(COMMENT, this.pos);
          int del = this.line.indexOf(this.delimiter.getToken(), this.pos);
          int min = Utl.min(qo == null ? -1 : qo.getPos(), Utl.min(del, cm));
          if (min == -1) { // no special symbol until end of the line
            appendRestOfLine();
            nextLine();
            if (this.directiveProcessed) {
              return;
            }
          } else if (qo != null && min == qo.getPos()) { // quote found
            appendSegment(min + qo.getParser().getOpening().length());
            this.quote = qo;
          } else if (min == cm) { // comment found
            appendSegment(min);
            this.sb.append("\n");
            nextLine();
            if (this.directiveProcessed) {
              return;
            }
          } else { // delimiter found at: del
            delimiterFound = true;
            appendSegment(min);
            this.pos = min + this.delimiter.getToken().length();
            // verify there's no trailing content after the statement delimiter
            String content = this.line.substring(this.pos);
            if (!content.trim().isEmpty()) {
              throw new InvalidSQLScriptException(this.lineNumber, "Extra content '" + content
                  + "' was found at the end of the line after the statement delimiter '" + this.delimiter.getToken()
                  + "'. Old school SQL interpreters and DBA tools (e.g. DB2 DBA production tools) "
                  + "are not able to handle this type of comment correctly and will crash during the execution of the SQL script.\n"
                  + "Please move this comment to a different line of the script.");
            }
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
    if (this.startLineNumber == null) {
      int nb = findFirstNonBlankChar(this.line, this.pos, this.line.length());
      if (nb != -1) {
        this.startLineNumber = this.lineNumber;
        this.startPos = nb;
      }
    }
    this.sb.append(this.line.substring(this.pos));
    this.sb.append("\n");
  }

  private void appendSegment(final int end) {
    if (this.startLineNumber == null) {
      int nb = findFirstNonBlankChar(this.line, this.pos, end);
      if (nb != -1) {
        this.startLineNumber = this.lineNumber;
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

  public static class InvalidSQLScriptException extends Exception {

    private static final long serialVersionUID = 1L;

    private int lineNumber;

    public InvalidSQLScriptException(final int lineNumber, final String message) {
      super(message);
      this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
      return lineNumber;
    }

  }

}
