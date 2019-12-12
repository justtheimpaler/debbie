package org.nocrala.tools.database.db.executor;

import org.nocrala.tools.database.db.parser.ScriptSQLStatement;

public class SQLStats {

  private int successful = 0;
  private int failed = 0;

  private ScriptSQLStatement failedSQLStatement = null;

  public void addSuccessful() {
    this.successful++;
  }

  public void addFailed() {
    this.failed++;
  }

  public int getSuccessful() {
    return this.successful;
  }

  public int getFailed() {
    return this.failed;
  }

  public ScriptSQLStatement getFailedSQLStatement() {
    return failedSQLStatement;
  }

  public void setFailedSQLStatement(ScriptSQLStatement failedSQLStatement) {
    this.failedSQLStatement = failedSQLStatement;
  }

  public String render() {
    return "" + this.successful + " statement(s) successful" + (this.failed == 0 ? "" : ", " + this.failed + " failed");
  }

}
