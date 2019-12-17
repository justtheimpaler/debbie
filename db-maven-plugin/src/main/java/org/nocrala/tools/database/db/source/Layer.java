package org.nocrala.tools.database.db.source;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nocrala.tools.database.db.executor.Feedback;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
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

  private Feedback feedback;

  public Layer(final VersionNumber versionNumber, final File dir, final Feedback feedback) {
    this.versionNumber = versionNumber;
    this.feedback = feedback;
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

  public void build(final SQLExecutor sqlExecutor, final boolean onErrorContinue)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (this.build.exists() && this.build.isFile()) {
      sqlExecutor.run(this.build, onErrorContinue);
    } else {
      this.feedback.warn("" + this.build + ": not found -- skipped");
    }
  }

  // Clean

  public void clean(final SQLExecutor sqlExecutor, final boolean onErrorContinue)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (this.clean.exists() && this.clean.isFile()) {
      sqlExecutor.run(this.clean, onErrorContinue);
    } else {
      this.feedback.warn("" + this.clean + ": not found -- skipped");
    }
  }

  // Scenario

  public void buildScenario(final String name, final SQLExecutor sqlExecutor, final boolean onErrorContinue)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (name != null) {
      Scenario s = this.scenarios.get(name);
      if (s != null) {
        s.build(sqlExecutor, onErrorContinue, feedback);
      } else {
        this.feedback.warn("" + new File(this.scenariosDir, name) + ": not found -- skipped");
      }
    }
  }

  // Getters

  public VersionNumber getVersionNumber() {
    return versionNumber;
  }

}
