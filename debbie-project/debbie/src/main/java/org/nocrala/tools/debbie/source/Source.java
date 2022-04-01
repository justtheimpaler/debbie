package org.nocrala.tools.debbie.source;

import java.io.File;
import java.util.TreeMap;

import org.nocrala.tools.debbie.Configuration;
import org.nocrala.tools.debbie.executor.Feedback;
import org.nocrala.tools.debbie.executor.SQLExecutor;
import org.nocrala.tools.debbie.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.debbie.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.aversion.Version;

public class Source {

  private Configuration config;
  private Feedback feedback;

  private TreeMap<Version, Layer> layers;

  public Source(final Configuration config, final Feedback feedback) throws InvalidDatabaseSourceException {

    this.config = config;
    this.feedback = feedback;

    File[] dirs = config.getDatabaseSourceDir().listFiles(f -> f.isDirectory());

    this.layers = new TreeMap<>();
    for (File f : dirs) {
      Version v = new Version(f.getName());
      Layer l = new Layer(v, f, this.config, this.feedback);
      this.layers.put(v, l);
    }

  }

  public void build(final Version version, final SQLExecutor sqlExecutor)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    Layer l = this.layers.get(version);
    if (l == null) {
      throw new IllegalArgumentException(
          "Could build database; version " + version + " does not exist in: " + this.config.getDatabaseSourceDir());
    }

    if (this.config.isLayeredBuild()) {
      for (Version v : this.layers.keySet()) {
        if (!v.after(version)) {
          Layer vl = this.layers.get(v);
          vl.build(sqlExecutor);
          if (this.config.isLayeredScenario() || v.same(version)) {
            vl.buildScenario(this.config.getDataScenario(), sqlExecutor);
          }
        }
      }
    } else {
      l.build(sqlExecutor);
      l.buildScenario(this.config.getDataScenario(), sqlExecutor);
    }

  }

  public void clean(final Version version, final SQLExecutor sqlExecutor)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    Layer l = this.layers.get(version);
    if (l == null) {
      throw new IllegalArgumentException(
          "Could build database; version " + version + " does not exist in: " + this.config.getDatabaseSourceDir());
    }

    if (this.config.isLayeredBuild()) {
      for (Version v : this.layers.descendingKeySet()) {
        if (!v.after(version)) {
          Layer vl = this.layers.get(v);
          vl.clean(sqlExecutor);
        }
      }
    } else {
      l.clean(sqlExecutor);
    }

  }

  public void rebuild(final Version version, final SQLExecutor sqlExecutor)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    clean(version, sqlExecutor);
    build(version, sqlExecutor);
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

  // Classes

  public static class InvalidDatabaseSourceException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidDatabaseSourceException(final String message) {
      super(message);
    }

  }

}
