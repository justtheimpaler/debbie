package org.nocrala.tools.database.db.ant;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.nocrala.tools.database.db.executor.Feedback;

public class AntFeedback implements Feedback {

  private Task task;

  public AntFeedback(final Task task) {
    this.task = task;
  }

  @Override
  public void info(final String line) {
    this.task.log(line, Project.MSG_INFO);
  }

  @Override
  public void warn(final String line) {
    this.task.log("Warning: " + line, Project.MSG_WARN);
  }

  @Override
  public void error(final String line) {
    this.task.log("Error: " + line, Project.MSG_ERR);
  }

}
