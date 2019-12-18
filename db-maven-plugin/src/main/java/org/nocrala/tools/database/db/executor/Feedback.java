package org.nocrala.tools.database.db.executor;

public interface Feedback {

  void info(String line);

  void warn(String line);

  void error(String line);

  default void logSQLWarning(final String m) {
    
  }
  
}
