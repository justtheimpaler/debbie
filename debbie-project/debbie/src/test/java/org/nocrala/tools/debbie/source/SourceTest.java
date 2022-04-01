package org.nocrala.tools.debbie.source;

import java.io.File;

import org.nocrala.tools.debbie.Configuration;
import org.nocrala.tools.debbie.RawParametersProvider;
import org.nocrala.tools.debbie.Configuration.ConfigurationException;
import org.nocrala.tools.debbie.executor.SQLExecutor;
import org.nocrala.tools.debbie.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.debbie.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.debbie.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.debbie.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.debbie.source.Source;
import org.nocrala.tools.debbie.source.Source.InvalidDatabaseSourceException;

public class SourceTest implements RawParametersProvider {

  // @Test
  public void testMain() {

    performTest();

  }

  private void performTest() {

    // Parameters

    File basedir = new File(".");

    // Execution

    TestFeedback feedback = new TestFeedback();

    Source s;
    try {
      Configuration config = null;
      s = new Source(config, feedback);
    } catch (InvalidDatabaseSourceException e1) {
      e1.printStackTrace();
      return;
    }

    SQLExecutor sqlExecutor = null;

    Configuration config;
    try {
      config = new Configuration(basedir, feedback, this);

      sqlExecutor = new SQLExecutor(config, feedback);
    } catch (InvalidPropertiesFileException e) {
      System.out.println("Invalid properties file: " + renderException(e));
      // e.printStackTrace();
      return;
    } catch (CouldNotConnectToDatabaseException e) {
      System.out.println("Could not connect to database: " + renderException(e));
      // e.printStackTrace();
      return;
    } catch (ConfigurationException e) {
      System.out.println("Invalid configuration: " + renderException(e));
      e.printStackTrace();
      return;
    }

    try {

      s.build(config.getTargetVersion(), sqlExecutor);
      // s.clean(currentVersion, sqlExecutor, feedback );
      // s.rebuild(currentVersion, scenario, sqlExecutor, feedback );

    } catch (CouldNotReadSQLScriptException e) {
      // System.out.println("Could not read SQL script file.");
      // System.out.println(" -> " + renderException(e));
      // e.printStackTrace();
      return;
    } catch (SQLScriptAbortedException e) {
      // System.out.println("SQL statement failed.");
      // System.out.println(" -> " + renderException(e));
      // e.printStackTrace();
      return;
    }
  }

  private String renderException(final Throwable e) {
    StringBuilder sb = new StringBuilder();
    Throwable t = e;
    boolean first = true;
    while (t != null) {
      String m = t.getMessage();
      if (m != null && !m.trim().isEmpty()) {
        if (first) {
          first = false;
        } else {
          sb.append(": ");
        }
        sb.append(m.trim());
      }
      t = t.getCause();
    }
    return sb.toString();

  }

  //

  private String sourcedir = "testdata/dbsrc";
  private String targetversion = "1.1.0";
  private String datascenario = "dev1";

  private String layeredbuild = "true";
  private String layeredscenario = "false";

  private String onbuilderror = "stop";
  private String oncleanerror = "continue";

  private String localproperties = "testdata/local.properties";

  private String delimiter = ";";
  private String solodelimiter = "false";
  private String casesensitivedelimiter = "false";

  private String treatwarningas = "warning";

  private String jdbcdriverclass;
  private String jdbcurl;
  private String jdbcusername;
  private String jdbcpassword;

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
