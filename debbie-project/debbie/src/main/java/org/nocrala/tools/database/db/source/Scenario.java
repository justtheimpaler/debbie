package org.nocrala.tools.database.db.source;

import java.io.File;

import org.nocrala.tools.database.db.Configuration;
import org.nocrala.tools.database.db.executor.Feedback;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;

public class Scenario {

  private final static String BUILD_DATA_FILE = "build-data.sql";

  private File buildData;

  private Configuration config;
  private Feedback feedback;

  public Scenario(final File dir, final Configuration config, final Feedback feedback) {
    this.buildData = new File(dir, BUILD_DATA_FILE);
    this.config = config;
    this.feedback = feedback;
  }

  // Build

  public void build(final SQLExecutor sqlExecutor) throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (this.buildData.exists() && this.buildData.isFile()) {
      sqlExecutor.run(this.buildData, this.config.getBasedir(), this.config.getOnBuildError());
    } else {
      this.feedback.warn("-- " + this.buildData + " does not exist -- skipped");
    }
  }

  // Getters

  public File getBuildData() {
    return this.buildData;
  }

}
