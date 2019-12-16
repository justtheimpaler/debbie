package org.nocrala.tools.database.db;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.nocrala.tools.database.db.executor.Delimiter;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.executor.SQLExecutor.TreatWarningAs;
import org.nocrala.tools.database.db.source.Source;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;
import org.nocrala.tools.database.db.utils.VersionNumber;

@Mojo(name = "build", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class BuildMojo extends AbstractMojo {

  private static final String MOJO_ERROR_MESSAGE = "Database build failed";

  // Parameters

  @Parameter()
  private String sourcedir;

  @Parameter()
  private String version;

  @Parameter()
  protected String scenario;

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

    feedback
        .info("Build database from: " + this.sourcedir + " -- version: " + (this.version != null ? this.version : "n/a")
            + " -- scenario: " + (this.scenario != null ? this.scenario : "no scenario"));

    // Version

    if (this.version == null || this.version.trim().isEmpty()) {
      feedback.error("Mandatory property (version) is not specified.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    VersionNumber currentVersion = new VersionNumber(this.version);

    // delimiter

    if (this.delimiter == null) {
      this.delimiter = Constants.DEFAULT_DELIMITER;
    } else if (this.delimiter.trim().isEmpty()) {
      feedback.error("Specified property (delimiter) is empty.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    boolean solo;
    if (this.solodelimiter == null) {
      solo = Constants.DEFAULT_SOLO_DELIMITER;
    } else if ("true".equals(this.solodelimiter)) {
      solo = true;
    } else if ("false".equals(this.solodelimiter)) {
      solo = false;
    } else {
      feedback.error("Specified property (solodelimiter) must have either the value true or false.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    boolean caseSensitive;
    if (this.casesensitivedelimiter == null) {
      caseSensitive = Constants.DEFAULT_CASE_SENSITIVE_SOLO_DELIMITER;
    } else if ("true".equals(this.casesensitivedelimiter)) {
      caseSensitive = true;
    } else if ("false".equals(this.casesensitivedelimiter)) {
      caseSensitive = false;
    } else {
      feedback.error("Specified property (casesensitivedelimiter) must have either the value true or false.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    TreatWarningAs treatWarningAs;
    if (this.treatwarningas == null) {
      treatWarningAs = Constants.DEFAULT_TREAT_WARNING_AS;
    } else if ("success".equalsIgnoreCase(this.treatwarningas)) {
      treatWarningAs = TreatWarningAs.SUCCESS;
    } else if ("warn".equalsIgnoreCase(this.treatwarningas)) {
      treatWarningAs = TreatWarningAs.WARNING;
    } else if ("error".equalsIgnoreCase(this.treatwarningas)) {
      treatWarningAs = TreatWarningAs.ERROR;
    } else {
      feedback.error("Specified property (treatwarningas) must have any value from the list: success, warn, error");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    Delimiter delimiter = new Delimiter(this.delimiter, caseSensitive, solo);

    Source s;
    try {
      File dbdir = new File(this.project.getBasedir(), this.sourcedir);
      s = new Source(dbdir, this.layeredbuild, this.layeredscenarios, this.buildonerrorcontinue,
          this.cleanonerrorcontinue, feedback);
    } catch (InvalidDatabaseSourceException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    SQLExecutor sqlExecutor;
    try {
      File propsFile = new File(this.project.getBasedir(), this.localproperties);
      sqlExecutor = new SQLExecutor(propsFile, feedback, delimiter, treatWarningAs);
    } catch (InvalidPropertiesFileException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    try {
      s.build(currentVersion, this.scenario, sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }
  }

}