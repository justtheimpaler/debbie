package org.nocrala.tools.debbie.executor;

public interface Feedback {

  void info(String line);

  void warn(String line);

  void error(String line);

}
