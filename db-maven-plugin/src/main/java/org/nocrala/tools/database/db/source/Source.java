package org.nocrala.tools.database.db.source;

import java.io.File;
import java.sql.SQLException;
import java.util.TreeMap;

import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.utils.VersionNumber;

public class Source {

  private File sourceDir;
  private boolean layeredBuild;
  private boolean layeredScenarios;
  private boolean buildOnErrorContinue;
  private boolean cleanOnErrorContinue;

  private TreeMap<VersionNumber, Layer> layers;

  public Source(final File sourceDir, final boolean layeredBuild, final boolean layeredScenarios,
      final boolean buildOnErrorContinue, final boolean cleanOnErrorContinue) {

    if (sourceDir == null) {
      throw new IllegalArgumentException("sourcedir cannot be null");
    }
    if (!sourceDir.exists()) {
      throw new IllegalArgumentException("sourcedir does not exist: " + sourceDir);
    }
    if (!sourceDir.isDirectory()) {
      throw new IllegalArgumentException("sourcedir is not a directory: " + sourceDir);
    }

    if (!layeredBuild && layeredScenarios) {
      throw new IllegalArgumentException("layered scenarios cannot be enabled when builds are not layered");
    }

    this.sourceDir = sourceDir;
    this.layeredBuild = layeredBuild;
    this.layeredScenarios = layeredScenarios;
    this.buildOnErrorContinue = buildOnErrorContinue;
    this.cleanOnErrorContinue = cleanOnErrorContinue;

    File[] dirs = sourceDir.listFiles(f -> f.isDirectory());

    this.layers = new TreeMap<>();
    for (File f : dirs) {
      VersionNumber v = new VersionNumber(f.getName());
      Layer l = new Layer(v, f);
      this.layers.put(v, l);
    }

  }

  public void build(final VersionNumber currentVersion, final String scenarioName, final SQLExecutor sqlExecutor)
      throws SQLException {
    Layer l = this.layers.get(currentVersion);
    if (l == null) {
      throw new IllegalArgumentException(
          "Could build database; version " + currentVersion + " does not exist in: " + this.sourceDir);
    }

    if (this.layeredBuild) {
      for (VersionNumber v : this.layers.keySet()) {
        if (!v.after(currentVersion)) {
          buildLayer(this.layers.get(v), sqlExecutor);
          if (this.layeredScenarios || v.same(currentVersion)) {
            buildScenario(this.layers.get(v), scenarioName, sqlExecutor);
          }
        }
      }
    } else {
      buildLayer(l, sqlExecutor);
      buildScenario(l, scenarioName, sqlExecutor);
    }

  }

  public void clean(final VersionNumber currentVersion, final SQLExecutor sqlExecutor) throws SQLException {
    Layer l = this.layers.get(currentVersion);
    if (l == null) {
      throw new IllegalArgumentException(
          "Could build database; version " + currentVersion + " does not exist in: " + this.sourceDir);
    }

    if (this.layeredBuild) {
      for (VersionNumber v : this.layers.descendingKeySet()) {
        if (!v.after(currentVersion)) {
          cleanLayer(this.layers.get(v), sqlExecutor);
        }
      }
    } else {
      cleanLayer(l, sqlExecutor);
    }

  }

  public void rebuild(final VersionNumber currentVersion, final String scenarioName, final SQLExecutor sqlExecutor)
      throws SQLException {
    clean(currentVersion, sqlExecutor);
    build(currentVersion, scenarioName, sqlExecutor);
  }

  // Delegate methods

  private void buildLayer(final Layer l, final SQLExecutor sqlExecutor) throws SQLException {
    l.build(sqlExecutor, this.buildOnErrorContinue);
  }

  private void buildScenario(final Layer l, final String scenarioName, final SQLExecutor sqlExecutor)
      throws SQLException {
      l.buildScenario(scenarioName, sqlExecutor, this.buildOnErrorContinue);
  }

  private void cleanLayer(final Layer l, final SQLExecutor sqlExecutor) throws SQLException {
    l.clean(sqlExecutor, this.cleanOnErrorContinue);
  }

  // toString()

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("layers=" + this.layers.size() + "\n");
    for (Layer l : this.layers.values()) {
      sb.append(" * layer '" + l.getVersionNumber() + "' - " + l + "\n");
    }
    return sb.toString();
  }

}
