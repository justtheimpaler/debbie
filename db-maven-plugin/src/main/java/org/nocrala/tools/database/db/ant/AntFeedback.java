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
//    t.System.out.println(line);
  }

  @Override
  public void warn(final String line) {
    this.task.log(line, Project.MSG_WARN);
//    System.out.println("[WARN] " + line);
  }

  @Override
  public void error(final String line) {
    this.task.log(line, Project.MSG_ERR);
//    System.out.println("[ERROR] " + line);
  }

}
