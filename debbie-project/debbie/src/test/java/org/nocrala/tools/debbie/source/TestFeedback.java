package org.nocrala.tools.debbie.source;

import org.nocrala.tools.debbie.executor.Feedback;

public class TestFeedback implements Feedback {

  @Override
  public void info(final String line) {
    for (String l : line.split("\n")) {
      System.out.println("<<INFO>> " + l);
    }
  }

  @Override
  public void warn(final String line) {
    for (String l : line.split("\n")) {
      System.out.println("<<WARN>> " + l);
    }
  }

  @Override
  public void error(final String line) {
    for (String l : line.split("\n")) {
      System.out.println("<<ERROR>> " + l);
    }
  }

}
