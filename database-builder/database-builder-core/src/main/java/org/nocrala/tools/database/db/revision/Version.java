package org.nocrala.tools.database.db.revision;

import java.util.List;

public class Version implements Comparable<Version> {

  private List<Part> parts;
  private boolean isSnapshot;

  public Version(final String version) {

  }

  public boolean isSnapshot() {
    return this.isSnapshot;
  }

  @Override
  public int compareTo(final Version other) {
    return 0;
  }

}
