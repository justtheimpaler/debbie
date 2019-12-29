package org.nocrala.tools.database.db.version;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VersionNumber implements Comparable<VersionNumber> {

  private List<Segment> segments;

  public VersionNumber(final String version) {
    log("* version='" + version + "'");

    this.segments = new ArrayList<Segment>();
    if (version == null) {
      Segment s = new Segment("");
      this.segments.add(s);
    } else {
      String[] p = version.split("\\.", 1000);
      for (String s : p) {
        this.segments.add(new Segment(s));
      }
    }

    log("=== segments [" + this.segments.size() + "] ===");
    for (Segment s : this.segments) {
      log(s.render());
    }
    log("======");
  }

  public boolean same(final VersionNumber other) {
    return this.compareTo(other) == 0;
  }

  public boolean before(final VersionNumber other) {
    return this.compareTo(other) < 0;
  }

  public boolean after(final VersionNumber other) {
    return this.compareTo(other) > 0;
  }

  // Indexable

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((segments == null) ? 0 : segments.hashCode());
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
    VersionNumber other = (VersionNumber) obj;
    return this.same(other);
  }

  // Comparable<Version>

  public int compareTo(final VersionNumber other) {
    if (other == null) {
      return 1; // null is always before everything
    }
    Iterator<Segment> t = this.segments.iterator();
    Iterator<Segment> o = other.segments.iterator();
    log("t[" + this.segments.size() + "]=" + t + " o[" + other.segments.size() + "]=" + o);

    while (t.hasNext() && o.hasNext()) {
      int c = t.next().compareTo(o.next());
      log(" - c=" + c);
      if (c != 0) {
        log(" - return " + c);
        return c;
      }
    }

    if (t.hasNext()) {
      log(" - return 1");
      return 1;
    }
    if (o.hasNext()) {
      log(" - return -1");
      return -1;
    }
    log(" - return 0");
    return 0;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (Segment s : this.segments) {
      if (first) {
        first = false;
      } else {
        sb.append(".");
      }
      sb.append(s.renderPlain());
    }
    return sb.toString();
  }

  private void log(final String txt) {
    // System.out.println("[log] " + txt);
  }

}
