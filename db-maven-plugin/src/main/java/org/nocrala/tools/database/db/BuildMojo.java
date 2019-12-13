package org.nocrala.tools.database.db;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;

@Mojo(name = "build", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class BuildMojo extends AbstractDatabaseMojo {

  public BuildMojo() throws MojoExecutionException {
    super("Build", "Database build failed");
  }

  public void execute() throws MojoExecutionException {
    try {
      super.s.build(super.currentVersion, super.scenario, super.sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(super.mojoErrorMesage);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(super.mojoErrorMesage);
    }
  }

}