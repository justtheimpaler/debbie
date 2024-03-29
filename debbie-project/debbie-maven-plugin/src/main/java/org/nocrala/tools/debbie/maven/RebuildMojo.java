package org.nocrala.tools.debbie.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.nocrala.tools.debbie.BuildInformation;
import org.nocrala.tools.debbie.Configuration;
import org.nocrala.tools.debbie.Configuration.ConfigurationException;
import org.nocrala.tools.debbie.Constants;
import org.nocrala.tools.debbie.RawParametersProvider;
import org.nocrala.tools.debbie.executor.SQLExecutor;
import org.nocrala.tools.debbie.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.debbie.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.debbie.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.debbie.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.debbie.source.Source;
import org.nocrala.tools.debbie.source.Source.InvalidDatabaseSourceException;
import org.nocrala.tools.debbie.utils.FUtil;
import org.nocrala.tools.debbie.utils.OUtil;

@Mojo(name = "rebuild", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class RebuildMojo extends AbstractMojo implements RawParametersProvider {

  private static final String OPERATION = "Rebuild";

  // Parameters

  @Parameter()
  private String skip = null;

  @Parameter()
  private String sourcedir = null;

  @Parameter()
  private String targetversion = null;

  @Parameter()
  private String datascenario = null;

  @Parameter()
  private String layeredbuild = null;

  @Parameter()
  private String layeredscenario = null;

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

  @Parameter()
  private String jdbcdriverclass;

  @Parameter()
  private String jdbcurl;

  @Parameter()
  private String jdbcusername;

  @Parameter()
  private String jdbcpassword;

  // Project information

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  public void execute() throws MojoExecutionException {

    MojoFeedback feedback = new MojoFeedback(this);

    if (this.skip(feedback)) {
      return;
    }

    feedback.info(Constants.DEBBIE_NAME + " " + BuildInformation.PROJECT_VERSION + " (build "
        + BuildInformation.PROJECT_BUILD_ID + ")" + " - " + OPERATION);

    Configuration config;
    try {
      config = new Configuration(this.project.getBasedir(), feedback, this);
    } catch (ConfigurationException e1) {
      throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
    }

    File sourceDir = FUtil.relativize(this.project.getBasedir(), config.getDatabaseSourceDir());
    feedback.info("Rebuilding database from source at: " + sourceDir //
        + " -- target version: " + OUtil.coalesce(config.getTargetVersion(), "n/a") //
        + " -- data scenario: " + OUtil.coalesce(config.getDataScenario(), "no scenario"));

    Source source;
    try {
      source = new Source(config, feedback);
    } catch (InvalidDatabaseSourceException e) {
      throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
    }

    SQLExecutor sqlExecutor;
    try {
      sqlExecutor = new SQLExecutor(config, feedback);
    } catch (InvalidPropertiesFileException e) {
      throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
    }

    try {
      source.rebuild(config.getTargetVersion(), sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
    } finally {
      try {
        sqlExecutor.close();
      } catch (Exception e) {
        feedback.warn("Could not close database connection.");
      }
    }

  }

  private boolean skip(final MojoFeedback feedback) throws MojoExecutionException {
    if ("true".equals(this.skip)) {
      return true;
    }
    if (this.skip == null || skip.trim().isEmpty() || "false".equals(this.skip)) {
      return false;
    }
    feedback.error(
        "Invalid value for configuration property 'skip'. Must be 'true' or 'false' but found '" + this.skip + "'.");
    throw new MojoExecutionException(MojoFeedback.MOJO_ERROR_MESSAGE);
  }

  // RawParametersProvider

  public String getSourcedir() {
    return sourcedir;
  }

  public String getTargetversion() {
    return targetversion;
  }

  public String getDatascenario() {
    return datascenario;
  }

  public String getLayeredbuild() {
    return layeredbuild;
  }

  public String getLayeredscenario() {
    return layeredscenario;
  }

  public String getOnbuilderror() {
    return onbuilderror;
  }

  public String getOncleanerror() {
    return oncleanerror;
  }

  public String getLocalproperties() {
    return localproperties;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public String getSolodelimiter() {
    return solodelimiter;
  }

  public String getCasesensitivedelimiter() {
    return casesensitivedelimiter;
  }

  public String getTreatwarningas() {
    return treatwarningas;
  }

  public String getJdbcdriverclass() {
    return jdbcdriverclass;
  }

  public String getJdbcurl() {
    return jdbcurl;
  }

  public String getJdbcusername() {
    return jdbcusername;
  }

  public String getJdbcpassword() {
    return jdbcpassword;
  }

}