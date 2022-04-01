package org.nocrala.tools.debbie;

import java.io.File;

import org.nocrala.tools.debbie.executor.Delimiter;
import org.nocrala.tools.debbie.executor.Feedback;
import org.nocrala.tools.debbie.executor.SQLExecutor.TreatWarningAs;
import org.nocrala.tools.debbie.utils.SUtil;
import org.nocrala.tools.debbie.version.Version;

public class Configuration {

  // Default values

  private static final boolean DEFAULT_LAYERED_BUILD = true;
  private static final boolean DEFAULT_LAYERED_SCENARIO = false;

  private static final OnError DEFAULT_ON_BUILD_ERROR = OnError.STOP;
  private static final OnError DEFAULT_ON_CLEAN_ERROR = OnError.CONTINUE;

  public enum OnError {
    STOP, CONTINUE
  };

  // Computed properties

  private File basedir;

  private File databaseSourceDir;
  private Version targetVersion;
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

  public Configuration(final File basedir, final Feedback feedback, final RawParametersProvider rawParameters)
      throws ConfigurationException {

    this.basedir = basedir;

    FlatParameters flat = new FlatParameters(basedir, rawParameters, feedback);

    // Database source dir

    if (flat.getSourcedir() == null) {
      feedback.error("Mandatory property (" + FlatParameters.SOURCE_DIR + ") is not specified.");
      throw new ConfigurationException();
    }
    if (flat.getSourcedir().trim().isEmpty()) {
      feedback.error("Mandatory property (" + FlatParameters.SOURCE_DIR + ") is empty.");
      throw new ConfigurationException();
    }

    this.databaseSourceDir = new File(basedir, flat.getSourcedir());

    if (!this.databaseSourceDir.exists()) {
      feedback.error("Source directory does not exist: " + this.databaseSourceDir);
      throw new ConfigurationException();
    }
    if (!this.databaseSourceDir.isDirectory()) {
      feedback.error("Source directory file entry exists but is not a directory: " + this.databaseSourceDir);
      throw new ConfigurationException();
    }

    // Target version

    if (SUtil.isEmpty(flat.getTargetversion())) {
      feedback.error("Mandatory property (" + FlatParameters.TARGET_VERSION + ") is not specified.");
      throw new ConfigurationException();
    }

    this.targetVersion = new Version(flat.getTargetversion());

    // Scenario

    this.datascenario = flat.getDatascenario();

    // Layering

    if (SUtil.isEmpty(flat.getLayeredbuild())) {
      this.layeredBuild = DEFAULT_LAYERED_BUILD;
    } else {
      switch (flat.getLayeredbuild()) {
      case "true":
        this.layeredBuild = true;
        break;
      case "false":
        this.layeredBuild = false;
        break;
      default:
        feedback.error("Invalid value '" + flat.getLayeredbuild() + "' for property (" + FlatParameters.LAYERED_BUILD
            + "): " + "must have either the value true or false.");
        throw new ConfigurationException();
      }
    }

    if (SUtil.isEmpty(flat.getLayeredscenario())) {
      this.layeredScenario = DEFAULT_LAYERED_SCENARIO;
    } else {
      switch (flat.getLayeredscenario()) {
      case "true":
        this.layeredScenario = true;
        break;
      case "false":
        this.layeredScenario = false;
        break;
      default:
        feedback.error("Invalid value '" + flat.getLayeredscenario() + "' for property ("
            + FlatParameters.LAYERED_SCENARIO + "): " + "must have either the value true or false.");
        throw new ConfigurationException();
      }
    }

    if (!this.layeredBuild && this.layeredScenario) {
      feedback.error("Layered scenarios cannot be enabled when builds are not layered");
      throw new ConfigurationException();
    }

    // Error handling

    if (SUtil.isEmpty(flat.getOnbuilderror())) {
      this.oncleanerror = DEFAULT_ON_BUILD_ERROR;
    } else {
      switch (flat.getOnbuilderror()) {
      case "stop":
        this.onbuilderror = OnError.STOP;
        break;
      case "continue":
        this.onbuilderror = OnError.CONTINUE;
        break;
      default:
        feedback.error("Invalid value '" + flat.getOnbuilderror() + "' for property (" + FlatParameters.ON_BUILD_ERROR
            + "): " + "must have the value stop or continue.");
        throw new ConfigurationException();
      }
    }

    if (SUtil.isEmpty(flat.getOncleanerror())) {
      this.oncleanerror = DEFAULT_ON_CLEAN_ERROR;
    } else {
      switch (flat.getOncleanerror()) {
      case "stop":
        this.oncleanerror = OnError.STOP;
        break;
      case "continue":
        this.oncleanerror = OnError.CONTINUE;
        break;
      default:
        feedback.error("Invalid value '" + flat.getOncleanerror() + "' for property (" + FlatParameters.ON_CLEAN_ERROR
            + "): " + "must have the value stop or continue.");
        throw new ConfigurationException();
      }
    }

    // Delimiter

    String delimiterSequence;
    if (flat.getDelimiter() == null) {
      delimiterSequence = Constants.DEFAULT_DELIMITER;
    } else if (flat.getDelimiter().trim().isEmpty()) {
      feedback.error(
          "Invalid value for property (" + FlatParameters.DELIMITER_SEQUENCE + "): must specify a non empty value.");
      throw new ConfigurationException();
    } else {
      delimiterSequence = flat.getDelimiter();
    }

    boolean solo;
    if (flat.getSolodelimiter() == null) {
      solo = Constants.DEFAULT_SOLO_DELIMITER;
    } else if ("true".equals(flat.getSolodelimiter())) {
      solo = true;
    } else if ("false".equals(flat.getSolodelimiter())) {
      solo = false;
    } else {
      feedback.error("Invalid value '" + flat.getSolodelimiter() + "' for property (" + FlatParameters.SOLO_DELIMITER
          + "): must have either the value true or false.");
      throw new ConfigurationException();
    }

    boolean caseSensitive;
    if (flat.getCasesensitivedelimiter() == null) {
      caseSensitive = Constants.DEFAULT_CASE_SENSITIVE_SOLO_DELIMITER;
    } else if ("true".equals(flat.getCasesensitivedelimiter())) {
      caseSensitive = true;
    } else if ("false".equals(flat.getCasesensitivedelimiter())) {
      caseSensitive = false;
    } else {
      feedback.error("Invalid value '" + flat.getCasesensitivedelimiter() + "' for property ("
          + FlatParameters.CASE_SENSITIVE_DELIMITER + "): must have either the value true or false.");
      throw new ConfigurationException();
    }

    this.delimiter = new Delimiter(delimiterSequence, caseSensitive, solo);

    // Warning handling

    if (flat.getTreatwarningas() == null) {
      this.treatWarningAs = Constants.DEFAULT_TREAT_WARNING_AS;
    } else {
      switch (flat.getTreatwarningas()) {
      case "ignore":
        this.treatWarningAs = TreatWarningAs.IGNORE;
        break;
      case "info":
        this.treatWarningAs = TreatWarningAs.INFO;
        break;
      case "warn":
        this.treatWarningAs = TreatWarningAs.WARN;
        break;
      case "error":
        this.treatWarningAs = TreatWarningAs.ERROR;
        break;
      default:
        feedback.error("Invalid value '" + flat.getTreatwarningas() + "' for property ("
            + FlatParameters.TREAT_WARNING_AS + "): must have one of the following values: ignore, info, warn, error");
        throw new ConfigurationException();
      }
    }

    // JDBC Properties

    this.jdbcDriverClass = flat.getJdbcdriverClass();
    this.jdbcURL = flat.getJdbcurl();
    this.jdbcUsername = flat.getJdbcusername();
    this.jdbcPassword = flat.getJdbcpassword();

    if (empty(this.jdbcDriverClass)) {
      feedback.error("Property (" + FlatParameters.JDBC_DRIVER_CLASS_PROP + ") is required but was not specified");
      throw new ConfigurationException();
    }

    if (empty(this.jdbcURL)) {
      feedback.error("Property (" + FlatParameters.JDBC_URL_PROP + ") is required but was not specified");
      throw new ConfigurationException();
    }

  }

  private boolean empty(final String s) {
    return s == null || s.trim().isEmpty();
  }

  // Getters

  public File getBasedir() {
    return basedir;
  }

  public File getDatabaseSourceDir() {
    return databaseSourceDir;
  }

  public Version getTargetVersion() {
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
