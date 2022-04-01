package org.nocrala.tools.debbie.parser;

public class ScriptSQLStatement {

  private int line;
  private int col;
  private String sql;

  public ScriptSQLStatement(final int line, final int col, final String sql) {
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
