package org.nocrala.tools.database.db.source;

import java.io.File;
import java.sql.SQLException;

import org.nocrala.tools.database.db.executor.SQLExecutor;

public class Scenario {

  private final static String BUILD_DATA_FILE = "build-data.sql";

  private File buildData;

  public Scenario(final File dir) {
    this.buildData = new File(dir, BUILD_DATA_FILE);
  }

  // Build

  public void build(final SQLExecutor sqlExecutor, final boolean onErrorContinue) throws SQLException {
    if (this.buildData.exists() && this.buildData.isFile()) {
      System.out.println("-- running " + this.buildData);
      sqlExecutor.run(this.buildData, onErrorContinue);
    } else {
      System.out.println("-- " + this.buildData + " does not exist -- skipped");
    }
  }

  // Getters

  public File getBuildData() {
    return buildData;
  }

}
