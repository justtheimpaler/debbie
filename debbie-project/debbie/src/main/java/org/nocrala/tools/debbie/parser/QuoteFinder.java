package org.nocrala.tools.debbie.parser;

public class QuoteFinder {

  // Standard openings

  private static final QuoteParser SINGLE_QUOTE = new QuoteParser("'", "'", "''", "\\\\", "\\'");

  private static final QuoteParser DOUBLE_QUOTE = new QuoteParser("\"", "\"");

  private static final QuoteParser ORACLE_QUOTE_NL = new QuoteParser("n'", "'", "''");
  private static final QuoteParser ORACLE_QUOTE_NU = new QuoteParser("N'", "'", "''");

  private static final QuoteParser POSTGRESQL_QUOTE_EL = new QuoteParser("e'", "'", "\\'");
  private static final QuoteParser POSTGRESQL_QUOTE_EU = new QuoteParser("E'", "'", "\\'");
  private static final QuoteParser POSTGRESQL_QUOTE_DD = new QuoteParser("$$", "$$");

  private static final QuoteParser[] STANDARD_QUOTES = new QuoteParser[] { SINGLE_QUOTE, DOUBLE_QUOTE, ORACLE_QUOTE_NL,
      ORACLE_QUOTE_NU, POSTGRESQL_QUOTE_EL, POSTGRESQL_QUOTE_EU, POSTGRESQL_QUOTE_DD };

  // Custom headings

  private static final String[] ORACLE_CUSTOM_HEADINGS = new String[] { "q'", "Q'", "nq'", "nQ'", "Nq'", "NQ'" };

  // Methods

  public static QuoteOpening find(final String line, final int pos) {

    // find standard quotes

    QuoteOpening o = null;
    for (QuoteParser p : STANDARD_QUOTES) {
      QuoteOpening s = p.findOpening(line, pos);
      // System.out.println("### {" + p.getOpening() + "} line=" + line + " pos=" +
      // pos + " s=" + s);
      // System.out.println("### o1 before=" + o);
      o = QuoteOpening.first(o, s);
      // System.out.println("### o2 after=" + o);
    }

    // find custom quotes

    QuoteOpening co = findCustomOracleOpening(line, pos);
    // System.out.println("### co=" + co);
    o = QuoteOpening.first(o, co);
    // System.out.println("### o3 after=" + o);

    QuoteOpening cp = findCustomPostgreSQLOpening(line, pos);
    // System.out.println("line=" + line + " pos=" + pos + " cp=" + cp);
    o = QuoteOpening.first(o, cp);
    // System.out.println("### o4 after=" + o);

    return o;
  }

  private static QuoteOpening findCustomOracleOpening(final String line, final int pos) {
    for (String h : ORACLE_CUSTOM_HEADINGS) {
      int idx = line.indexOf(h, pos);
      if (idx != -1 && idx + h.length() < line.length()) {
        char o = line.charAt(idx + h.length());
        char c;
        switch (o) {
        case '<':
          c = '>';
          break;
        case '[':
          c = ']';
          break;
        case '{':
          c = '}';
          break;
        case '(':
          c = ')';
          break;
        default:
          c = o;
        }
        return new QuoteOpening(idx, new QuoteParser("a" + o, "" + c + "'"));
      }
    }
    return null;
  }

  private static QuoteOpening findCustomPostgreSQLOpening(final String line, final int pos) {
    int d = line.indexOf("$", pos);
    if (d == -1) {
      return null;
    }
    int p = d + 1;
    while (p < line.length()) {
      char c = line.charAt(p);
      if (c == '$') {
        // System.out.println("line=" + line + " -- d + 1=" + (d + 1) + " p=" + p);
        String tag = line.substring(d + 1, p);
        return new QuoteOpening(d, new QuoteParser("$" + tag + "$", "$" + tag + "$"));
      } else if (c > 'a' && c < 'z' || c > 'A' && c < 'Z' || c > '0' && c < '9' || c == '_') {
        // OK, part of alphanumeric tag
      } else {
        return null;
      }
      p++;
    }
    return null;
  }

}
