package org.nocrala.tools.database.db.revision;

import java.math.BigInteger;

public class Atom implements Comparable<Atom> {

  public enum Type {
    EMPTY, NUMERIC, ALPHABETIC
  };

  private Type type;
  private BigInteger n;
  private String s;

  public Atom(final String atom) {
    if (atom == null) {
      throw new IllegalArgumentException("Invalid atom: cannot be null");
    }
    this.n = null;
    this.s = null;
    if (atom.isEmpty()) {
      this.type = Type.EMPTY;
    } else if (isNumeric(atom)) {
      this.type = Type.NUMERIC;
      this.n = new BigInteger(atom);
    } else if (isAlphabetic(atom)) {
      this.type = Type.ALPHABETIC;
      this.s = atom;
    } else {
      throw new IllegalArgumentException("Invalid atom '" + atom + "': must be either numeric or alphabetic.");
    }
  }

  public boolean isNumeric(final String s) {
    if (s == null || s.isEmpty()) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!Atom.isDigit(c)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isDigit(final char c) {
    return c >= '0' && c <= '9';
  }

  public boolean isAlphabetic(final String s) {
    if (s == null || s.isEmpty()) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!Atom.isAlphabetic(c)) {
        return false;
      }
    }
    return true;
  }

  public static boolean isAlphabetic(final char c) {
    return Character.isAlphabetic(c);
  }

  public boolean isEqualTo(final Atom a) {
    return this.compareTo(a) == 0;
  }

  public boolean isLessThan(final Atom a) {
    return this.compareTo(a) == -1;
  }

  public boolean isGreaterThan(final Atom a) {
    return this.compareTo(a) == 1;
  }

  // package level methods

  Type getType() {
    return this.type;
  }

  String render() {
    return this.type == Type.EMPTY ? "e:" : (this.type == Type.NUMERIC ? ("n:" + this.n) : ("a:" + this.s));
  }

  String renderPlain() {
    return this.type == Type.EMPTY ? "" : (this.type == Type.NUMERIC ? ("" + this.n) : ("" + this.s));
  }

  // Comparable<Segment>

  public int compareTo(final Atom other) {
    if (this.type == Type.EMPTY) {
      return other.type == Type.EMPTY ? 0 : -1;
    } else if (other.type == Type.EMPTY) {
      return 1;
    }
    if (this.type == Type.NUMERIC) { // this is number
      if (other.type == Type.NUMERIC) {
        return this.n.compareTo(other.n);
      } else {
        return -1; // number is before a alphabetic
      }
    } else { // this is string
      if (other.type == Type.NUMERIC) {
        return 1;
      } else {
        return this.s.compareTo(other.s);
      }
    }
  }

}
