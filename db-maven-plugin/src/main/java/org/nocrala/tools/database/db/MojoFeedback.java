package org.nocrala.tools.database.db;

import org.apache.maven.plugin.AbstractMojo;
import org.nocrala.tools.database.db.executor.Feedback;

public class MojoFeedback implements Feedback {

  private AbstractMojo mojo;

  public MojoFeedback(final AbstractMojo mojo) {
    this.mojo = mojo;
  }

  @Override
  public void info(final String line) {
    this.mojo.getLog().info(line);
  }

  @Override
  public void warn(final String line) {
    this.mojo.getLog().warn(line);
  }

  @Override
  public void error(final String line) {
    this.mojo.getLog().error(line);
  }

}
