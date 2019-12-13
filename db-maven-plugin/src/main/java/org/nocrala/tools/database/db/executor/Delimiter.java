package org.nocrala.tools.database.db.executor;

public class Delimiter {

  private String token;
  private boolean solo;

  public Delimiter(final String token, final boolean solo) {
    this.token = token;
    this.solo = solo;
  }

  public String getToken() {
    return token;
  }

  public boolean isSolo() {
    return solo;
  }

}
