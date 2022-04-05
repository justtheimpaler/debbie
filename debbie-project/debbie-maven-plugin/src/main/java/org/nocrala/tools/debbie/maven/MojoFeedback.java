package org.nocrala.tools.debbie.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.nocrala.tools.debbie.Constants;
import org.nocrala.tools.debbie.executor.Feedback;

public class MojoFeedback implements Feedback {

  public static final String MOJO_ERROR_MESSAGE = Constants.DEBBIE_NAME + " operation failed";

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
