package org.nocrala.tools.database.db.utils;

import java.math.BigInteger;

// Numbers sort before strings

public class Segment implements Comparable<Segment> {

  public enum Type {
    EMPTY, NUMERIC, STRING
  };

  private Type type;
  private BigInteger n;
  private String s;

  public Segment(final String segment) {
    if (segment == null) {
      throw new IllegalArgumentException("a segment cannot be null");
    }
    if (segment.indexOf('.') != -1) {
      throw new IllegalArgumentException("a segment cannot have a dot in it");
    }

    this.n = null;
    this.s = null;
    if (segment.equals("")) {
      this.type = Type.EMPTY;
    } else {
      try {
        this.n = new BigInteger(segment);
        if (this.n.signum() == -1) {
          this.s = segment;
          this.type = Type.STRING;
        } else {
          this.type = Type.NUMERIC;
        }
      } catch (NumberFormatException e) {
        this.s = segment;
        this.type = Type.STRING;
      }
    }
  }

  public boolean isEqualTo(final Segment s) {
    return this.compareTo(s) == 0;
  }

  public boolean isLessThan(final Segment s) {
    return this.compareTo(s) == -1;
  }

  public boolean isGreaterThan(final Segment s) {
    return this.compareTo(s) == 1;
  }

  // package level methods

  Type getType() {
    return this.type;
  }

  String render() {
    return this.type == Type.EMPTY ? "e:" : (this.type == Type.NUMERIC ? ("n:" + this.n) : ("s:" + this.s));
  }

  // Comparable<Segment>

  public int compareTo(final Segment s) {
    if (this.type == Type.EMPTY) {
      return s.type == Type.EMPTY ? 0 : -1;
    } else if (s.type == Type.EMPTY) {
      return 1;
    }
    if (this.type == Type.NUMERIC) { // this is number
      if (s.type == Type.NUMERIC) {
        return this.n.compareTo(s.n);
      } else {
        return -1; // number is before a string
      }
    } else { // this is string
      if (s.type == Type.NUMERIC) {
        return 1;
      } else {
        return this.s.compareTo(s.s);
      }
    }
  }

}
