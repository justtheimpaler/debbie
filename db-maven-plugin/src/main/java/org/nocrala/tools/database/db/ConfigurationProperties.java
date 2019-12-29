package org.nocrala.tools.database.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.nocrala.tools.database.db.executor.Delimiter;
import org.nocrala.tools.database.db.executor.Feedback;
import org.nocrala.tools.database.db.executor.SQLExecutor.TreatWarningAs;
import org.nocrala.tools.database.db.version.VersionNumber;

public class ConfigurationProperties {

  private static final String JDBC_DRIVER_CLASS_PROP = "jdbc.driverclass";
  private static final String JDBC_URL_PROP = "jdbc.url";
  private static final String JDBC_USERNAME_PROP = "jdbc.username";
  private static final String JDBC_PASSWORD_PROP = "jdbc.password";

  private static final boolean DEFAULT_LAYERED_BUILD = true;
  private static final boolean DEFAULT_LAYERED_SCENARIO = false;

  private static final OnError DEFAULT_ON_BUILD_ERROR = OnError.STOP;
  private static final OnError DEFAULT_ON_CLEAN_ERROR = OnError.CONTINUE;

  public enum OnError {
    STOP, CONTINUE
  };

  // Computed properties

  private File databaseSourceDir;
  private VersionNumber targetVersion;
  private String datascenario;

  private boolean layeredBuild;
  private boolean layeredScenario;

  private OnError onbuilderror;
  private OnError oncleanerror;

  private Delimiter delimiter;
  private TreatWarningAs treatWarningAs;

  private String jdbcDriverClass;
  private String jdbcURL;
  private String jdbcUsername;
  private String jdbcPassword;

  public ConfigurationProperties(final File basedir, final String sourcedir, final String targetversion,
      final String datascenario, final String layeredbuild, final String layeredscenario, final String onbuilderror,
      final String oncleanerror, final String delimitersequence, final String solodelimiter,
      final String casesensitivedelimiter, final String treatwarningas, final String localproperties,
      final Feedback feedback) throws ConfigurationException {

    // Database source dir

    if (sourcedir == null) {
      feedback.error("Mandatory property (sourcedir) is not specified.");
      throw new ConfigurationException();
    }
    if (sourcedir.trim().isEmpty()) {
      feedback.error("Mandatory property (sourcedir) is empty.");
      throw new ConfigurationException();
    }

    this.databaseSourceDir = new File(basedir, sourcedir);

    if (!this.databaseSourceDir.exists()) {
      feedback.error("sourcedir does not exist: " + this.databaseSourceDir);
      throw new ConfigurationException();
    }
    if (!this.databaseSourceDir.isDirectory()) {
      feedback.error("sourcedir is not a directory: " + this.databaseSourceDir);
      throw new ConfigurationException();
    }

    // Target version

    if (targetversion == null || targetversion.trim().isEmpty()) {
      feedback.error("Mandatory property (version) is not specified.");
      throw new ConfigurationException();
    }

    this.targetVersion = new VersionNumber(targetversion);

    // Scenario

    this.datascenario = datascenario;

    // Layering

    if (layeredbuild == null || layeredbuild.isEmpty()) {
      this.layeredBuild = DEFAULT_LAYERED_BUILD;
    } else {
      switch (layeredbuild) {
      case "true":
        this.layeredBuild = true;
        break;
      case "false":
        this.layeredBuild = false;
        break;
      default:
        feedback.error("Invalid value for property (layeredbuild): " + "must have either the value true or false.");
        throw new ConfigurationException();
      }
    }

    if (layeredscenario == null || layeredscenario.isEmpty()) {
      this.layeredScenario = DEFAULT_LAYERED_SCENARIO;
    } else {
      switch (layeredscenario) {
      case "true":
        this.layeredScenario = true;
        break;
      case "false":
        this.layeredScenario = false;
        break;
      default:
        feedback.error("Invalid value for property (layeredscenario): " + "must have either the value true or false.");
        throw new ConfigurationException();
      }
    }

    if (!this.layeredBuild && this.layeredScenario) {
      feedback.error("layered scenarios cannot be enabled when builds are not layered");
      throw new ConfigurationException();
    }

    // Error handling

    if (onbuilderror == null || onbuilderror.trim().isEmpty()) {
      this.oncleanerror = DEFAULT_ON_BUILD_ERROR;
    } else {
      switch (onbuilderror) {
      case "stop":
        this.onbuilderror = OnError.STOP;
        break;
      case "continue":
        this.onbuilderror = OnError.CONTINUE;
        break;
      default:
        feedback.error("Invalid value for property (onbuilderror): " + "must have the value stop or continue.");
        throw new ConfigurationException();
      }
    }

    if (oncleanerror == null || oncleanerror.trim().isEmpty()) {
      this.oncleanerror = DEFAULT_ON_CLEAN_ERROR;
    } else {
      switch (oncleanerror) {
      case "stop":
        this.oncleanerror = OnError.STOP;
        break;
      case "continue":
        this.oncleanerror = OnError.CONTINUE;
        break;
      default:
        feedback.error("Invalid value for property (oncleanerror): " + "must have the value stop or continue.");
        throw new ConfigurationException();
      }
    }

    // Delimiter

    String dsequence;
    if (delimitersequence == null) {
      dsequence = Constants.DEFAULT_DELIMITER;
    } else if (delimitersequence.trim().isEmpty()) {
      feedback.error("Invalid value for property (delimiter): must specify a non empty value.");
      throw new ConfigurationException();
    } else {
      dsequence = delimitersequence;
    }

    boolean solo;
    if (solodelimiter == null) {
      solo = Constants.DEFAULT_SOLO_DELIMITER;
    } else if ("true".equals(solodelimiter)) {
      solo = true;
    } else if ("false".equals(solodelimiter)) {
      solo = false;
    } else {
      feedback.error("Invalid value '" + solodelimiter
          + "' for property (solodelimiter): must have either the value true or false.");
      throw new ConfigurationException();
    }

    boolean caseSensitive;
    if (casesensitivedelimiter == null) {
      caseSensitive = Constants.DEFAULT_CASE_SENSITIVE_SOLO_DELIMITER;
    } else if ("true".equals(casesensitivedelimiter)) {
      caseSensitive = true;
    } else if ("false".equals(casesensitivedelimiter)) {
      caseSensitive = false;
    } else {
      feedback.error("Invalid value '" + casesensitivedelimiter
          + "' for property (casesensitivedelimiter): must have either the value true or false.");
      throw new ConfigurationException();
    }

    this.delimiter = new Delimiter(dsequence, caseSensitive, solo);

    // Warning handling

    if (treatwarningas == null) {
      this.treatWarningAs = Constants.DEFAULT_TREAT_WARNING_AS;
    } else if ("ignore".equalsIgnoreCase(treatwarningas)) {
      this.treatWarningAs = TreatWarningAs.IGNORE;
    } else if ("info".equalsIgnoreCase(treatwarningas)) {
      this.treatWarningAs = TreatWarningAs.INFO;
    } else if ("warn".equalsIgnoreCase(treatwarningas)) {
      this.treatWarningAs = TreatWarningAs.WARN;
    } else if ("error".equalsIgnoreCase(treatwarningas)) {
      this.treatWarningAs = TreatWarningAs.ERROR;
    } else {
      feedback.error("Invalid value '" + treatwarningas
          + "' for property (treatwarningas): must have one of the following values: ignore, info, warn, error");
      throw new ConfigurationException();
    }

    // JDBC Properties

    Properties props = new Properties();
    try {
      props.load(new FileReader(localproperties));
    } catch (FileNotFoundException e) {
      feedback.error("Local properties file not found (" + localproperties + "): " + e.getMessage());
      throw new ConfigurationException();
    } catch (IOException e) {
      feedback.error("Could not read local properties file (" + localproperties + "): " + e.getMessage());
      throw new ConfigurationException();
    }

    this.jdbcDriverClass = props.getProperty(JDBC_DRIVER_CLASS_PROP);
    this.jdbcURL = props.getProperty(JDBC_URL_PROP);
    this.jdbcUsername = props.getProperty(JDBC_USERNAME_PROP);
    this.jdbcPassword = props.getProperty(JDBC_PASSWORD_PROP);

    if (empty(this.jdbcDriverClass)) {
      feedback.error("Mandatory property (" + JDBC_DRIVER_CLASS_PROP + ") is not specified in the properties file: "
          + localproperties);
      throw new ConfigurationException();
    }

    if (empty(this.jdbcURL)) {
      feedback.error(
          "Mandatory property (" + JDBC_URL_PROP + ") is not specified in the properties file: " + localproperties);
      throw new ConfigurationException();
    }

  }

  private boolean empty(final String s) {
    return s == null || s.trim().isEmpty();
  }

  // Getters

  public File getDatabaseSourceDir() {
    return databaseSourceDir;
  }

  public VersionNumber getTargetVersion() {
    return targetVersion;
  }

  public String getDataScenario() {
    return datascenario;
  }

  public boolean isLayeredBuild() {
    return layeredBuild;
  }

  public boolean isLayeredScenario() {
    return layeredScenario;
  }

  public OnError getOnBuildError() {
    return onbuilderror;
  }

  public OnError getOnCleanError() {
    return oncleanerror;
  }

  public Delimiter getDelimiter() {
    return delimiter;
  }

  public TreatWarningAs getTreatWarningAs() {
    return treatWarningAs;
  }

  public String getJdbcDriverClass() {
    return jdbcDriverClass;
  }

  public String getJdbcURL() {
    return jdbcURL;
  }

  public String getJdbcUsername() {
    return jdbcUsername;
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }

  // Exception handling

  public static class ConfigurationException extends Exception {

    private static final long serialVersionUID = 1L;

  }

}
