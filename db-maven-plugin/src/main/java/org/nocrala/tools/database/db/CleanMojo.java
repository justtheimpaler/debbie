package org.nocrala.tools.database.db;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.source.Source;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;
import org.nocrala.tools.database.db.utils.VersionNumber;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "clean"
public class CleanMojo extends AbstractMojo {

  private static final String MOJO_ERROR_MESSAGE = "Database clean failed";

  @Parameter()
  private String sourcedir;

  @Parameter()
  private String version;

  @Parameter()
  private String scenario;

  @Parameter()
  private boolean layeredbuild = true;

  @Parameter()
  private boolean layeredscenarios = false;

  @Parameter()
  private boolean buildonerrorcontinue = false;

  @Parameter()
  private boolean cleanonerrorcontinue = true;

  @Parameter()
  private String localproperties = null;

  public void execute() throws MojoExecutionException {

    MojoFeedback feedback = new MojoFeedback(this);

    feedback
        .info("Clean database from: " + this.sourcedir + " -- version: " + (this.version != null ? this.version : "n/a")
            + " -- scenario: " + (this.scenario != null ? this.scenario : "no scenario"));

    if (this.version == null || this.version.trim().isEmpty()) {
      feedback.error("Mandatory property (version) is not specified.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    VersionNumber currentVersion = new VersionNumber(this.version);

    Source s;
    try {
      File dbdir = new File(this.sourcedir);
      s = new Source(dbdir, this.layeredbuild, this.layeredscenarios, this.buildonerrorcontinue,
          this.cleanonerrorcontinue, feedback);
    } catch (InvalidDatabaseSourceException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    SQLExecutor sqlExecutor;
    try {
      File propsFile = new File(this.localproperties);
      sqlExecutor = new SQLExecutor(propsFile, feedback);
    } catch (InvalidPropertiesFileException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    try {

      s.clean(currentVersion, sqlExecutor);

    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

  }

}