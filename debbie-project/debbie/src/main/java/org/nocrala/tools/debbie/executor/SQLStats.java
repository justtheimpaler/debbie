package org.nocrala.tools.debbie.executor;

import org.nocrala.tools.debbie.parser.ScriptSQLStatement;

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
    return "" + this.successful + " statement" + (this.successful == 1 ? "" : "s") + " executed successfully"
        + (this.failed == 0 ? "" : ", " + this.failed + " failed");
  }

}
