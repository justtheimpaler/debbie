package org.nocrala.tools.database.db.source;

import java.io.File;
import java.util.TreeMap;

import org.nocrala.tools.database.db.utils.VersionNumber;

public class Source {

  private File sourceDir;
  private TreeMap<VersionNumber, Layer> layers;

  public Source(final File sourceDir) {

    if (sourceDir == null) {
      throw new IllegalArgumentException("sourcedir cannot be null");
    }
    if (!sourceDir.exists()) {
      throw new IllegalArgumentException("sourcedir does not exist: " + sourceDir);
    }
    if (!sourceDir.isDirectory()) {
      throw new IllegalArgumentException("sourcedir is not a directory: " + sourceDir);
    }

    this.sourceDir = sourceDir;
    File[] dirs = sourceDir.listFiles(f -> f.isDirectory());

    this.layers = new TreeMap<>();
    for (File f : dirs) {
      VersionNumber v = new VersionNumber(f.getName());
      Layer l = new Layer(v, f);
      this.layers.put(v, l);
    }

  }

  public void build(final VersionNumber currentVersion, final String scenarioName) {
    if (!this.layers.containsKey(currentVersion)) {
      throw new IllegalArgumentException(
          "Could build database; version " + currentVersion + " does not exist in: " + this.sourceDir);
    }
    for (VersionNumber v : this.layers.keySet()) {
      if (!v.after(currentVersion)) {
        this.layers.get(v).build();
      }
      if (v.same(currentVersion)) {
        this.layers.get(v).buildScenario(scenarioName);
      }
    }
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
