package org.nocrala.tools.debbie.source;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.nocrala.tools.debbie.Configuration;
import org.nocrala.tools.debbie.executor.Feedback;
import org.nocrala.tools.debbie.executor.SQLExecutor;
import org.nocrala.tools.debbie.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.debbie.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.debbie.version.Version;

public class Layer {

  private final static String BUILD_SCRIPT_FILE = "build.sql";
  private final static String CLEAN_SCRIPT_FILE = "clean.sql";
  private final static String SCENARIOS_DIR = "scenarios";

  private Version versionNumber;
  private File buildScript;
  private File cleanScript;
  private File scenariosDir;
  private Map<String, Scenario> scenarios;

  private Configuration config;
  private Feedback feedback;

  public Layer(final Version versionNumber, final File dir, final Configuration config, final Feedback feedback) {
    this.versionNumber = versionNumber;
    this.config = config;
    this.feedback = feedback;
    this.buildScript = new File(dir, BUILD_SCRIPT_FILE);
    this.cleanScript = new File(dir, CLEAN_SCRIPT_FILE);

    this.scenarios = new HashMap<>();
    this.scenariosDir = new File(dir, SCENARIOS_DIR);
    if (this.scenariosDir.exists() && this.scenariosDir.isDirectory()) {
      File[] dirs = this.scenariosDir.listFiles(f -> f.isDirectory());
      this.scenarios = new HashMap<>();
      for (File d : dirs) {
        this.scenarios.put(d.getName(), new Scenario(d, this.config, this.feedback));
      }
    }
  }

  // Build

  public void build(final SQLExecutor sqlExecutor) throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (this.buildScript.exists() && this.buildScript.isFile()) {
      sqlExecutor.run(this.buildScript, this.config.getBasedir(), this.config.getOnBuildError());
    } else {
      this.feedback.warn("" + this.buildScript + ": not found -- skipped");
    }
  }

  // Clean

  public void clean(final SQLExecutor sqlExecutor) throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (this.cleanScript.exists() && this.cleanScript.isFile()) {
      sqlExecutor.run(this.cleanScript, this.config.getBasedir(), this.config.getOnCleanError());
    } else {
      this.feedback.warn("" + this.cleanScript + ": not found -- skipped");
    }
  }

  // Scenario

  public void buildScenario(final String name, final SQLExecutor sqlExecutor)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    if (name != null) {
      Scenario s = this.scenarios.get(name);
      if (s != null) {
        s.build(sqlExecutor);
      } else {
        this.feedback.warn("" + new File(this.scenariosDir, name) + ": not found -- skipped");
      }
    }
  }

  // Getters

  public Version getVersionNumber() {
    return versionNumber;
  }

}
