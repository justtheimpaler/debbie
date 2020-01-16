package org.nocrala.tools.database.db.revision;

import java.util.List;

public class Version implements Comparable<Version> {

  private static final String UPPER_SNAPSHOT = "-SNAPSHOT";
  private static final String LOWER_SNAPSHOT = "-snapshot";

  private List<Part> parts;
  private boolean isSnapshot;
  private boolean upperCaseSnapshot;

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
