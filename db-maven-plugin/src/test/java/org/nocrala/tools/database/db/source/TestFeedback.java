package org.nocrala.tools.database.db.source;

import org.nocrala.tools.database.db.executor.Feedback;

public class TestFeedback implements Feedback {

  @Override
  public void info(final String line) {
    for (String l : line.split("\n")) {
      System.out.println("<<INFO>> " + l);
    }
  }

  @Override
  public void error(String line) {
    for (String l : line.split("\n")) {
      System.out.println("<<ERROR>> " + l);
    }
  }

}
