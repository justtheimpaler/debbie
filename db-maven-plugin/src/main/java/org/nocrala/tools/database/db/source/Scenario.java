package org.nocrala.tools.database.db.source;

import java.io.File;

public class Scenario {

  private final static String BUILD_DATA_FILE = "build-data.sql";

  private File buildData;

  public Scenario(final File dir) {
    this.buildData = new File(dir, BUILD_DATA_FILE);
  }

  // Build

  public void build() {
    if (this.buildData.exists() && this.buildData.isFile()) {
      System.out.println("-- running " + this.buildData);
    } else {
      System.out.println("-- " + this.buildData + " does not exist -- skipped");
    }
  }

  // Getters

  public File getBuildData() {
    return buildData;
  }

}
