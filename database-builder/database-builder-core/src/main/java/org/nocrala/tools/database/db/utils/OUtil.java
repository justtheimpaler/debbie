package org.nocrala.tools.database.db.utils;

public class OUtil {

  public static Object coalesce(final Object... objects) {
    for (Object o : objects) {
      if (o != null) {
        return o;
      }
    }
    return null;
  }

}
