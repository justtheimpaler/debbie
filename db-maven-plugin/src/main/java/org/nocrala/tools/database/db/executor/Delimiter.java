package org.nocrala.tools.database.db.executor;

public class Delimiter {

  private String token;
  private boolean caseSensitiveSolo;
  private boolean solo;

  public Delimiter(final String token, final boolean caseSensitiveSolo, final boolean solo) {
    this.token = token.trim();
    this.caseSensitiveSolo = caseSensitiveSolo;
    this.solo = solo;
  }

  public String getToken() {
    return this.token;
  }

  public boolean isCaseSensitiveSolo() {
    return this.caseSensitiveSolo;
  }

  public boolean isSolo() {
    return this.solo;
  }

}