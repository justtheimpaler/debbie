package org.nocrala.tools.database.db.source;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

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

  public void build() {
    if (this.build.exists() && this.build.isFile()) {
      System.out.println("-- running " + this.build);
    } else {
      System.out.println("-- " + this.build + " does not exist -- skipped");
    }
  }

  public void clean() {
    if (this.clean.exists() && this.clean.isFile()) {
      System.out.println("-- running " + this.clean);
    } else {
      System.out.println("-- " + this.clean + " does not exist -- skipped");
    }
  }

  public void buildScenario(final String name) {
    Scenario s = this.scenarios.get(name);
    if (s != null) {
      s.build();
    } else {
      System.out.println("-- " + new File(this.scenariosDir, name) + " scenario does not exist -- skipped");
    }
  }

  // Getters

  public VersionNumber getVersionNumber() {
    return versionNumber;
  }

  public File getBuild() {
    return build;
  }

  public File getClean() {
    return clean;
  }

  public Map<String, Scenario> getScenarios() {
    return scenarios;
  }

}
