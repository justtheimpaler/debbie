package org.nocrala.tools.database.db.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.nocrala.tools.database.db.ConfigurationProperties;
import org.nocrala.tools.database.db.ConfigurationProperties.ConfigurationException;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.source.Source;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class CleanMojo extends AbstractMojo {

  private static final String MOJO_ERROR_MESSAGE = "Database clean failed";

  // Parameters

  @Parameter()
  private String sourcedir = null;

  @Parameter()
  private String targetversion = null;

  @Parameter()
  private String datascenario = null;

  @Parameter()
  private String layeredbuild = null;

  @Parameter()
  private String layeredscenarios = null;

  @Parameter()
  private String onbuilderror = null;

  @Parameter()
  private String oncleanerror = null;

  @Parameter()
  private String localproperties = null;

  @Parameter()
  private String delimiter = null;

  @Parameter()
  private String solodelimiter = null;

  @Parameter()
  private String casesensitivedelimiter = null;

  @Parameter()
  private String treatwarningas = null;

  // Project information

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  public void execute() throws MojoExecutionException {

    MojoFeedback feedback = new MojoFeedback(this);

    feedback.info("Clean database from: " + this.sourcedir + " -- version: "
        + (this.targetversion != null ? this.targetversion : "n/a"));

    ConfigurationProperties config;
    try {
      config = new ConfigurationProperties(this.project.getBasedir(), this.sourcedir, this.targetversion,
          this.datascenario, this.layeredbuild, this.layeredscenarios, this.onbuilderror, this.oncleanerror,
          this.delimiter, this.solodelimiter, this.casesensitivedelimiter, this.treatwarningas, this.localproperties,
          feedback);
    } catch (ConfigurationException e1) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    Source source;
    try {
      source = new Source(config, feedback);
    } catch (InvalidDatabaseSourceException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    SQLExecutor sqlExecutor;
    try {
      sqlExecutor = new SQLExecutor(config, feedback);
    } catch (InvalidPropertiesFileException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    try {
      source.clean(config.getTargetVersion(), sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

  }

}