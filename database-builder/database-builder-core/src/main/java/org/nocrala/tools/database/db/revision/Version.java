package org.nocrala.tools.database.db.revision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Version implements Comparable<Version> {

  private static final String UPPER_SNAPSHOT = "-SNAPSHOT";
  private static final String LOWER_SNAPSHOT = "-snapshot";

  private List<Part> parts;
  private String baseVersion;
  private boolean isSnapshot;
  private boolean upperCaseSnapshot;

  public Version(final String version) {
    if (version == null) {
      throw new IllegalArgumentException("Invalid version: cannot be null");
    }
    if (version.isEmpty()) {
      throw new IllegalArgumentException("Invalid version: cannot be empty");
    }

    if (version.endsWith(UPPER_SNAPSHOT)) {
      this.baseVersion = version.substring(0, version.length() - UPPER_SNAPSHOT.length());
      this.isSnapshot = true;
      this.upperCaseSnapshot = true;
    } else if (version.endsWith(LOWER_SNAPSHOT)) {
      this.baseVersion = version.substring(0, version.length() - LOWER_SNAPSHOT.length());
      this.isSnapshot = true;
      this.upperCaseSnapshot = false;
    } else if (version.indexOf('-') != -1) {
      throw new IllegalArgumentException(
          "Invalid version: cannot include the dash symbol (-) except when using the ending " + UPPER_SNAPSHOT);
    } else if (version.startsWith(".")) {
      throw new IllegalArgumentException("Invalid version: cannot start with a dot");
    } else {
      this.baseVersion = version;
      this.isSnapshot = false;
      this.upperCaseSnapshot = false;
    }

    this.parts = new ArrayList<>();
    int pos = 0;
    while (pos < this.baseVersion.length()) {
      int dot = this.baseVersion.indexOf('.', pos);
      try {
        if (dot == -1) {
          this.parts.add(new Part(this.baseVersion.substring(pos)));
          pos = this.baseVersion.length();
        } else {
          this.parts.add(new Part(this.baseVersion.substring(pos, dot)));
          pos = dot + 1;
        }
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid version '" + version + "': " + e.getMessage());
      }
    }

    if (this.baseVersion.endsWith(".")) {
      this.parts.add(new Part(""));
    }

  }

  // Getters

  public String getBaseVersion() {
    return this.baseVersion;
  }

  public boolean isUpperCaseSnapshot() {
    return this.upperCaseSnapshot;
  }

  public boolean isSnapshot() {
    return this.isSnapshot;
  }

  int getNumberOfParts() {
    return this.parts.size();
  }

  // Comparable<Version>

  private static final int THIS_IS_LESS_THAN_OTHER = -1;
  private static final int THIS_IS_EQUAL_TO_OTHER = 0;
  private static final int THIS_IS_GREATER_THAN_OTHER = 1;

  @Override
  public int compareTo(final Version other) {
    if (other == null) {
      return THIS_IS_GREATER_THAN_OTHER; // null is always before everything
    }
    Iterator<Part> t = this.parts.iterator();
    Iterator<Part> o = other.parts.iterator();

    while (t.hasNext() && o.hasNext()) {
      int c = t.next().compareTo(o.next());
      if (c != 0) {
        return c;
      }
    }

    if (t.hasNext()) {
      return THIS_IS_GREATER_THAN_OTHER;
    }
    if (o.hasNext()) {
      return THIS_IS_LESS_THAN_OTHER;
    }

    if (this.isSnapshot) {
      if (other.isSnapshot) {
        return THIS_IS_EQUAL_TO_OTHER;
      } else {
        return THIS_IS_LESS_THAN_OTHER;
      }
    } else {
      if (other.isSnapshot) {
        return THIS_IS_GREATER_THAN_OTHER;
      } else {
        return THIS_IS_EQUAL_TO_OTHER;
      }
    }

  }

  // Indexable

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((baseVersion == null) ? 0 : baseVersion.hashCode());
    result = prime * result + (isSnapshot ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Version other = (Version) obj;
    if (baseVersion == null) {
      if (other.baseVersion != null)
        return false;
    } else if (!baseVersion.equals(other.baseVersion))
      return false;
    if (isSnapshot != other.isSnapshot)
      return false;
    return true;
  }

}
