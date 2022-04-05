package org.nocrala.tools.debbie.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
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

public class RebuildAntTask extends Task implements RawParametersProvider {

  private static final String OPERATION = "Rebuild";

  // Ant parameters

  private String sourcedir = null;
  private String targetversion = null;
  private String datascenario = null;

  private String layeredbuild = null;
  private String layeredscenario = null;

  private String onbuilderror = null;
  private String oncleanerror = null;

  private String localproperties = null;

  private String delimiter = null;
  private String solodelimiter = null;
  private String casesensitivedelimiter = null;

  private String treatwarningas = null;

  private String jdbcdriverclass;
  private String jdbcurl;
  private String jdbcusername;
  private String jdbcpassword;

  public void execute() {

    AntFeedback feedback = new AntFeedback(this);

    feedback.info(Constants.DEBBIE_NAME + " " + BuildInformation.PROJECT_VERSION + " (build "
        + BuildInformation.PROJECT_BUILD_ID + ")" + " - " + OPERATION);

    File basedir = new File(".");

    Configuration config;
    try {
      config = new Configuration(basedir, feedback, this);
    } catch (ConfigurationException e1) {
      throw new BuildException(AntFeedback.ERROR_MESSAGE);
    }

    File sourceDir = FUtil.relativize(basedir, config.getDatabaseSourceDir());
    feedback.info(OPERATION + " database from: " + sourceDir //
        + " -- target version: " + OUtil.coalesce(config.getTargetVersion(), "n/a") //
        + " -- data scenario: " + OUtil.coalesce(config.getDataScenario(), "no scenario"));

    Source source;
    try {
      source = new Source(config, feedback);
    } catch (InvalidDatabaseSourceException e) {
      throw new BuildException(AntFeedback.ERROR_MESSAGE);
    }

    SQLExecutor sqlExecutor;
    try {
      sqlExecutor = new SQLExecutor(config, feedback);
    } catch (InvalidPropertiesFileException e) {
      throw new BuildException(AntFeedback.ERROR_MESSAGE);
    } catch (CouldNotConnectToDatabaseException e) {
      throw new BuildException(AntFeedback.ERROR_MESSAGE);
    }

    try {
      source.rebuild(config.getTargetVersion(), sqlExecutor);
    } catch (CouldNotReadSQLScriptException e) {
      throw new BuildException(AntFeedback.ERROR_MESSAGE);
    } catch (SQLScriptAbortedException e) {
      throw new BuildException(AntFeedback.ERROR_MESSAGE);
    } finally {
      try {
        sqlExecutor.close();
      } catch (Exception e) {
        feedback.warn("Could not close database connection.");
      }
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

  public void setLayeredscenario(String layeredscenario) {
    this.layeredscenario = layeredscenario;
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

  public void setJdbcdriverclass(String jdbcdriverclass) {
    this.jdbcdriverclass = jdbcdriverclass;
  }

  public void setJdbcurl(String jdbcurl) {
    this.jdbcurl = jdbcurl;
  }

  public void setJdbcusername(String jdbcusername) {
    this.jdbcusername = jdbcusername;
  }

  public void setJdbcpassword(String jdbcpassword) {
    this.jdbcpassword = jdbcpassword;
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
