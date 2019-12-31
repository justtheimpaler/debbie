package org.nocrala.tools.database.db;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.nocrala.tools.database.db.Configuration.ConfigurationException;
import org.nocrala.tools.database.db.executor.Feedback;
import org.nocrala.tools.database.db.utils.SUtil;

public class FlatParameters {

  public static final String SOURCE_DIR = "sourcedir";
  public static final String TARGET_VERSION = "targetversion";
  public static final String DATA_SCENARIO = "datascenario";

  public static final String LAYERED_BUILD = "layeredbuild";
  public static final String LAYERED_SCENARIO = "layeredscenario";

  public static final String ON_BUILD_ERROR = "onbuilderror";
  public static final String ON_CLEAN_ERROR = "oncleanerror";

  public static final String DELIMITER_SEQUENCE = "delimiter";
  public static final String SOLO_DELIMITER = "solodelimiter";
  public static final String CASE_SENSITIVE_DELIMITER = "casesensitivedelimiter";

  public static final String TREAT_WARNING_AS = "treatwarningas";

  public static final String JDBC_DRIVER_CLASS_PROP = "jdbcdriverclass";
  public static final String JDBC_URL_PROP = "jdbcurl";
  public static final String JDBC_USERNAME_PROP = "jdbcusername";
  public static final String JDBC_PASSWORD_PROP = "jdbcpassword";

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

  private String jdbcdriverClass;
  private String jdbcurl;
  private String jdbcusername;
  private String jdbcpassword;

  public FlatParameters(final RawParametersProvider provider, final Feedback feedback) throws ConfigurationException {

    Properties props = new Properties();
    if (!SUtil.isEmpty(provider.getLocalproperties())) {
      try {
        props.load(new FileReader(provider.getLocalproperties()));
      } catch (FileNotFoundException e) {
        feedback.error("Local properties file not found (" + provider.getLocalproperties() + "): " + e.getMessage());
        throw new ConfigurationException();
      } catch (IOException e) {
        feedback
            .error("Could not read local properties file (" + provider.getLocalproperties() + "): " + e.getMessage());
        throw new ConfigurationException();
      }
    }

    // Retrieve parameters

    this.sourcedir = resolve(provider.getSourcedir(), props.getProperty(SOURCE_DIR));
    this.targetversion = resolve(provider.getTargetversion(), props.getProperty(TARGET_VERSION));
    this.datascenario = resolve(provider.getDatascenario(), props.getProperty(DATA_SCENARIO));

    this.layeredbuild = resolve(provider.getLayeredbuild(), props.getProperty(LAYERED_BUILD));
    this.layeredscenario = resolve(provider.getLayeredscenario(), props.getProperty(LAYERED_SCENARIO));

    this.onbuilderror = resolve(provider.getOnbuilderror(), props.getProperty(ON_BUILD_ERROR));
    this.oncleanerror = resolve(provider.getOncleanerror(), props.getProperty(ON_CLEAN_ERROR));

    this.delimiter = resolve(provider.getDelimiter(), props.getProperty(DELIMITER_SEQUENCE));
    this.solodelimiter = resolve(provider.getSolodelimiter(), props.getProperty(SOLO_DELIMITER));
    this.casesensitivedelimiter = resolve(provider.getCasesensitivedelimiter(),
        props.getProperty(CASE_SENSITIVE_DELIMITER));

    this.treatwarningas = resolve(provider.getTreatwarningas(), props.getProperty(TREAT_WARNING_AS));

    this.jdbcdriverClass = resolve(provider.getJdbcdriverclass(), props.getProperty(JDBC_DRIVER_CLASS_PROP));
    this.jdbcurl = resolve(provider.getJdbcurl(), props.getProperty(JDBC_URL_PROP));
    this.jdbcusername = resolve(provider.getJdbcusername(), props.getProperty(JDBC_USERNAME_PROP));
    this.jdbcpassword = resolve(provider.getJdbcpassword(), props.getProperty(JDBC_PASSWORD_PROP));

  }

  private String resolve(final String base, final String override) {
    String value = null;
    if (base != null) {
      value = base;
    }
    if (override != null) {
      value = unquote(override);
    }
    return value;
  }

  private String unquote(final String v) {
    if (v == null) {
      return v;
    }
    if (v.length() >= 2 && v.startsWith("\"") && v.endsWith("\"")) {
      return v.substring(1, v.length() - 1);
    }
    return v;
  }

  // Getters

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

  public String getJdbcdriverClass() {
    return jdbcdriverClass;
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
