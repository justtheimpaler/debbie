package org.nocrala.tools.database.db;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.nocrala.tools.database.db.executor.Delimiter;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.source.Source;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;
import org.nocrala.tools.database.db.utils.VersionNumber;

@Mojo(name = "rebuild", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class RebuildMojo extends AbstractMojo {

  private static final String MOJO_ERROR_MESSAGE = "Database rebuild failed";

  private static final String DEFAULT_DELIMITER = ";";
  private static final boolean DEFAULT_SOLO_DELIMITER = false;

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

  public void execute() throws MojoExecutionException {

    MojoFeedback feedback = new MojoFeedback(this);

    feedback.info(
        "Rebuild database from: " + this.sourcedir + " -- version: " + (this.version != null ? this.version : "n/a")
            + " -- scenario: " + (this.scenario != null ? this.scenario : "no scenario"));

    if (this.version == null || this.version.trim().isEmpty()) {
      feedback.error("Mandatory property (version) is not specified.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    if (this.delimiter == null) {
      this.delimiter = DEFAULT_DELIMITER;
    } else if (this.delimiter.trim().isEmpty()) {
      feedback.error("Specified property (delimiter) is empty.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    boolean solo;
    if (this.solodelimiter == null) {
      solo = DEFAULT_SOLO_DELIMITER;
    } else if ("true".equals(this.solodelimiter)) {
      solo = true;
    } else if ("false".equals(this.solodelimiter)) {
      solo = false;
    } else {
      feedback.error("Specified property (solodelimiter) must have either the value true or false.");
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    Delimiter delimiter = new Delimiter(this.delimiter, solo);

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
      sqlExecutor = new SQLExecutor(propsFile, feedback, delimiter);
    } catch (InvalidPropertiesFileException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }

    try {
      s.rebuild(currentVersion, this.scenario, sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new MojoExecutionException(MOJO_ERROR_MESSAGE);
    }
  }

}