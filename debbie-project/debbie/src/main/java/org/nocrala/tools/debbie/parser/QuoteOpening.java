package org.nocrala.tools.debbie.parser;

public class QuoteOpening {

  private int pos;
  private QuoteParser parser;

  public QuoteOpening(final int pos, final QuoteParser parser) {
    this.pos = pos;
    this.parser = parser;
  }

  public int getPos() {
    return pos;
  }

  public QuoteParser getParser() {
    return parser;
  }

  public static QuoteOpening first(final QuoteOpening a, final QuoteOpening b) {
    return a == null ? b : (b == null ? a : a.getPos() <= b.getPos() ? a : b);
  }

  public String toString() {
    return "pos=" + this.pos + " parser=[" + this.parser + "]";
  }

}
