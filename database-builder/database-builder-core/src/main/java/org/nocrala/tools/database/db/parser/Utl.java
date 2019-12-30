package org.nocrala.tools.database.db.parser;

class Utl {

  static int min(final int a, final int b) {
    return a == -1 ? b : (b == -1 ? a : Math.min(a, b));
  }

}
