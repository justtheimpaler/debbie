package org.nocrala.tools.database.db.version;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

class Part implements Comparable<Part> {

  private List<Atom> atoms;

  private enum AtomType {
    ALPHA, NUMBER
  };

  public Part(final String part) {
    if (part == null) {
      throw new IllegalArgumentException("Invalid part: cannot be null");
    }
    // System.out.println("### --- New Part: " + part + " ---");

    this.atoms = new ArrayList<Atom>();
    if (part.isEmpty()) { // Valid case, an empty part
      return;
    }

    int lastStart = 0;
    int pos = 0;
    AtomType type = decideType(part, pos);

    while (pos < part.length()) {
      char c = part.charAt(pos);
      // System.out.println("### pos=" + pos + " [" + type + "] c=" + c);
      switch (type) {
      case ALPHA:
        if (!Atom.isAlphabetic(c)) {
          // System.out.println("### A atom=" + part.substring(lastStart, pos));
          this.atoms.add(new Atom(part.substring(lastStart, pos)));
          lastStart = pos;
          type = decideType(part, pos);
        }
        break;
      case NUMBER:
        if (!Atom.isDigit(c)) {
          // System.out.println("### N atom=" + part.substring(lastStart, pos));
          this.atoms.add(new Atom(part.substring(lastStart, pos)));
          lastStart = pos;
          type = decideType(part, pos);
        }
        break;
      }
      pos++;
    }

    this.atoms.add(new Atom(part.substring(lastStart, pos)));

  }

  private AtomType decideType(final String part, final int pos) {
    char c = part.charAt(pos);
    if (Atom.isAlphabetic(c)) {
      return AtomType.ALPHA;
    } else if (Atom.isDigit(c)) {
      return AtomType.NUMBER;
    } else {
      throw new IllegalArgumentException(
          "Invalid part: must include alphabetic or digits, but a '" + c + "' (char " + ((int) c) + ") was found");
    }
  }

  // Comparable<Part>

  @Override
  public int compareTo(final Part other) {
    if (other == null) {
      return 1; // null is always before everything
    }
    Iterator<Atom> t = this.atoms.iterator();
    Iterator<Atom> o = other.atoms.iterator();

    while (t.hasNext() && o.hasNext()) {
      int c = t.next().compareTo(o.next());
      if (c != 0) {
        return c;
      }
    }

    if (t.hasNext()) {
      return 1;
    }
    if (o.hasNext()) {
      return -1;
    }
    return 0;

  }

  public String toString() {
    if (this.atoms.isEmpty()) {
      return "[empty]";
    } else {
      return this.atoms.stream().map(a -> a.render()).collect(Collectors.joining(" - "));
    }
  }

}
