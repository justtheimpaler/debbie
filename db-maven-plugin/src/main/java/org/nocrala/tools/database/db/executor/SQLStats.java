package org.nocrala.tools.database.db.executor;

import org.nocrala.tools.database.db.parser.SQLScriptParser.ScriptStatement;

public class SQLStats {

  private int successful = 0;
  private int failed = 0;

  private ScriptStatement failedSQLStatement = null;

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

  public ScriptStatement getFailedSQLStatement() {
    return failedSQLStatement;
  }

  public void setFailedSQLStatement(ScriptStatement failedSQLStatement) {
    this.failedSQLStatement = failedSQLStatement;
  }

  public String render() {
    return "" + this.successful + " statement(s) successful" + (this.failed == 0 ? "" : ", " + this.failed + " failed");
  }

}
