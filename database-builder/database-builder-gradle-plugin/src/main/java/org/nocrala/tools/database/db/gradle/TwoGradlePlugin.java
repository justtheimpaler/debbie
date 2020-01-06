package org.nocrala.tools.database.db.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class TwoGradlePlugin implements Plugin<Project> {

  @Override
  public void apply(Project project) {
    project.getTasks().create("two", Two.class, (task) -> {
      task.setMessage("Hello");
      task.setRecipient("World");
    });
  }

  // Gradle Task definition

  public static class Two extends DefaultTask {

    private String message;
    private String recipient;

    // Behavior

    @TaskAction
    void sayGreeting() {
      System.out.printf("%s, %s!\n", getMessage(), getRecipient());
    }

    // Accessors

    @Input
    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    @Input
    public String getRecipient() {
      return recipient;
    }

    public void setRecipient(String recipient) {
      this.recipient = recipient;
    }

  }

}
