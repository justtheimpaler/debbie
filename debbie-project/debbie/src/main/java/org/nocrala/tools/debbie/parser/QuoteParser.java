package org.nocrala.tools.debbie.parser;

public class QuoteParser {

  private String opening;
  private String closing;
  private String[] internalEscapeSequences;

  public QuoteParser(final String opening, final String closing, final String... internalEscapeSequences) {
    this.opening = opening;
    this.internalEscapeSequences = internalEscapeSequences;
    this.closing = closing;
  }

  public QuoteOpening findOpening(final String line, final int pos) {
    int idx = line.indexOf(this.opening, pos);
    if (idx != -1) {
      return new QuoteOpening(idx, this);
    }
    return null;
  }

  public QuoteClosing findClosing(final String line, final int pos) {
    int p = pos;
    while (p < line.length()) {

      // Find closer escape sequence

      int mine = -1;
      String mines = null;
      for (String es : this.internalEscapeSequences) {
        int idx = line.indexOf(es, p);
        if (idx != -1) {
          if (mine == -1 || idx < mine) {
            mine = idx;
            mines = es;
          }
        }
        mine = Utl.min(mine, idx);
      }

      // Find closer closing

      int minc = line.indexOf(this.closing, p);

      // Check if closing found

      int min = Utl.min(mine, minc);
      if (min == -1) {
        return null;
      } else if (min == minc) {
        return new QuoteClosing(minc, this.closing);
      } else {
        p = mine + mines.length();
      }

    }

    return null;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String es : this.internalEscapeSequences) {
      sb.append(es + " ");
    }
    return "opening=" + this.opening + " closing=" + this.closing + " es=" + sb.toString();
  }

  // Getters

  public String getOpening() {
    return this.opening;
  }

  public String getClosing() {
    return this.closing;
  }

}
