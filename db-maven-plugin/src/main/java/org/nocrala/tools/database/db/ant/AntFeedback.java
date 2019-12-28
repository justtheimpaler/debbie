package org.nocrala.tools.database.db.ant;

import org.nocrala.tools.database.db.executor.Feedback;

public class AntFeedback implements Feedback {

  public AntFeedback() {
  }

  @Override
  public void info(final String line) {
    System.out.println("[INFO] " + line);
  }

  @Override
  public void warn(final String line) {
    System.out.println("[WARN] " + line);
  }

  @Override
  public void error(final String line) {
    System.out.println("[ERROR] " + line);
  }

}
