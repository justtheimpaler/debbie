package org.nocrala.tools.database.db.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Version implements Comparable<Version> {

  private List<Segment> segments;

  public Version(final String version) {
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

  public boolean same(final Version other) {
    return this.compareTo(other) == 0;
  }

  public boolean before(final Version other) {
    return this.compareTo(other) < 0;
  }

  public boolean after(final Version other) {
    return this.compareTo(other) > 0;
  }

  // Comparable<Version>

  public int compareTo(final Version other) {
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

  private void log(final String txt) {
    // System.out.println("[log] " + txt);
  }

}
