package org.nocrala.tools.debbie.parser;

public class QuoteClosing {

  private int pos;
  private String closing;

  public QuoteClosing(final int pos, final String closing) {
    super();
    this.pos = pos;
    this.closing = closing;
  }

  public int getPos() {
    return pos;
  }

  public String getClosing() {
    return closing;
  }

}
