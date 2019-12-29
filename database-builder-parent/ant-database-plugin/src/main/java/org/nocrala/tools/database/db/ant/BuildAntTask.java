package org.nocrala.tools.database.db.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.nocrala.tools.database.db.ConfigurationProperties;
import org.nocrala.tools.database.db.ConfigurationProperties.ConfigurationException;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.source.Source;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;

public class BuildAntTask extends Task {

  private static final String ERROR_MESSAGE = "Database build failed";

  // Ant parameters

  private String sourcedir = null;
  private String targetversion = null;
  private String datascenario = null;
  private String layeredbuild = null;
  private String layeredscenarios = null;
  private String onbuilderror = null;
  private String oncleanerror = null;
  private String localproperties = null;
  private String delimiter = null;
  private String solodelimiter = null;
  private String casesensitivedelimiter = null;
  private String treatwarningas = null;

  public void execute() {

    AntFeedback feedback = new AntFeedback(this);

    feedback.info("Build database from: " + this.sourcedir + " -- version: "
        + (this.targetversion != null ? this.targetversion : "n/a") + " -- scenario: "
        + (this.datascenario != null ? this.datascenario : "no scenario"));

    File basedir = new File(".");

    ConfigurationProperties config;
    try {
      config = new ConfigurationProperties(basedir, this.sourcedir, this.targetversion, this.datascenario,
          this.layeredbuild, this.layeredscenarios, this.onbuilderror, this.oncleanerror, this.delimiter,
          this.solodelimiter, this.casesensitivedelimiter, this.treatwarningas, this.localproperties, feedback);
    } catch (ConfigurationException e1) {
      throw new BuildException(ERROR_MESSAGE);
    }

    Source source;
    try {
      source = new Source(config, feedback);
    } catch (InvalidDatabaseSourceException e) {
      throw new BuildException(ERROR_MESSAGE);
    }

    SQLExecutor sqlExecutor;
    try {
      sqlExecutor = new SQLExecutor(config, feedback);
    } catch (InvalidPropertiesFileException e) {
      throw new BuildException(ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new BuildException(ERROR_MESSAGE);
    }

    try {
      source.build(config.getTargetVersion(), sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new BuildException(ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new BuildException(ERROR_MESSAGE);
    }

  }

  // Setters

  public void setSourcedir(String sourcedir) {
    this.sourcedir = sourcedir;
  }

  public void setTargetversion(String targetversion) {
    this.targetversion = targetversion;
  }

  public void setDatascenario(String datascenario) {
    this.datascenario = datascenario;
  }

  public void setLayeredbuild(String layeredbuild) {
    this.layeredbuild = layeredbuild;
  }

  public void setLayeredscenarios(String layeredscenarios) {
    this.layeredscenarios = layeredscenarios;
  }

  public void setOnbuilderror(String onbuilderror) {
    this.onbuilderror = onbuilderror;
  }

  public void setOncleanerror(String oncleanerror) {
    this.oncleanerror = oncleanerror;
  }

  public void setLocalproperties(String localproperties) {
    this.localproperties = localproperties;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public void setSolodelimiter(String solodelimiter) {
    this.solodelimiter = solodelimiter;
  }

  public void setCasesensitivedelimiter(String casesensitivedelimiter) {
    this.casesensitivedelimiter = casesensitivedelimiter;
  }

  public void setTreatwarningas(String treatwarningas) {
    this.treatwarningas = treatwarningas;
  }

}
