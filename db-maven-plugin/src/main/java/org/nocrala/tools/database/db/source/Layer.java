package org.nocrala.tools.database.db.source;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.utils.VersionNumber;

public class Layer {

  private final static String BUILD_FILE = "build.sql";
  private final static String CLEAN_FILE = "clean.sql";
  private final static String SCENARIOS_DIR = "scenarios";

  private VersionNumber versionNumber;
  private File build;
  private File clean;
  private File scenariosDir;
  private Map<String, Scenario> scenarios;

  public Layer(final VersionNumber versionNumber, final File dir) {
    this.versionNumber = versionNumber;
    this.build = new File(dir, BUILD_FILE);
    this.clean = new File(dir, CLEAN_FILE);

    this.scenarios = new HashMap<>();
    this.scenariosDir = new File(dir, SCENARIOS_DIR);
    if (this.scenariosDir.exists() && this.scenariosDir.isDirectory()) {
      File[] dirs = this.scenariosDir.listFiles(f -> f.isDirectory());
      this.scenarios = new HashMap<>();
      for (File d : dirs) {
        this.scenarios.put(d.getName(), new Scenario(d));
      }
    }
  }

  // Build

  public void build(final SQLExecutor sqlExecutor, final boolean onErrorContinue) throws SQLException {
    if (this.build.exists() && this.build.isFile()) {
      System.out.println("-- running " + this.build);
      sqlExecutor.run(this.build, onErrorContinue);
    } else {
      System.out.println("-- " + this.build + " does not exist -- skipped");
    }
  }

  public void clean(final SQLExecutor sqlExecutor, final boolean onErrorContinue) throws SQLException {
    if (this.clean.exists() && this.clean.isFile()) {
      System.out.println("-- running " + this.clean);
      sqlExecutor.run(this.clean, onErrorContinue);
    } else {
      System.out.println("-- " + this.clean + " does not exist -- skipped");
    }
  }

  public void buildScenario(final String name, final SQLExecutor sqlExecutor, final boolean onErrorContinue)
      throws SQLException {
    Scenario s = this.scenarios.get(name);
    if (s != null) {
      s.build(sqlExecutor, onErrorContinue);
    } else {
      System.out.println("-- " + new File(this.scenariosDir, name) + " scenario does not exist -- skipped");
    }
  }

  // Getters

  public VersionNumber getVersionNumber() {
    return versionNumber;
  }

}
