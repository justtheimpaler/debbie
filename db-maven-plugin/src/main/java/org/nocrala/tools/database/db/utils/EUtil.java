package org.nocrala.tools.database.db.utils;

public class EUtil {

  public static String renderException(final Throwable e) {
    StringBuilder sb = new StringBuilder();
    Throwable t = e;
    boolean first = true;
    while (t != null) {
      String m = t.getMessage();
      if (m != null && !m.trim().isEmpty()) {
        if (first) {
          first = false;
        } else {
          sb.append(": ");
        }
        sb.append(m.trim());
      }
      t = t.getCause();
    }
    return sb.toString();
  }

}
