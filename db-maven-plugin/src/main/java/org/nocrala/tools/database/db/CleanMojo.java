package org.nocrala.tools.database.db;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "clean"
public class CleanMojo extends AbstractDatabaseMojo {

  public CleanMojo() throws MojoExecutionException {
    super("Clean", "Database clean failed");
  }

  public void execute() throws MojoExecutionException {
    try {
      super.s.clean(super.currentVersion, super.sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(super.mojoErrorMesage);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(super.mojoErrorMesage);
    }
  }

}