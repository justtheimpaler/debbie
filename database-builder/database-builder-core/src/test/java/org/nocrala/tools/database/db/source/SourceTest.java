package org.nocrala.tools.database.db.source;

import java.io.File;

import org.nocrala.tools.database.db.ConfigurationProperties;
import org.nocrala.tools.database.db.ConfigurationProperties.ConfigurationException;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;

public class SourceTest {

  // @Test
  public void testMain() {

    performTest();

  }

  private void performTest() {

    // Parameters

    File basedir = new File(".");

    String sourcedir = "testdata/dbsrc";
    String targetversion = "1.1.0";
    String datascenario = "dev1";

    String layeredBuild = "true";
    String layeredScenario = "false";
    String onbuilderror = "stop";
    String oncleanerror = "continue";

    String localproperties = "testdata/localdatabase.properties";

    String delimitersequence = ";";
    String solodelimiter = "false";
    String casesensitivedelimiter = "false";

    String treatwarningsas = null;

    // Execution

    TestFeedback feedback = new TestFeedback();

    Source s;
    try {
      ConfigurationProperties config = null;
      s = new Source(config, feedback);
    } catch (InvalidDatabaseSourceException e1) {
      e1.printStackTrace();
      return;
    }

    SQLExecutor sqlExecutor = null;

    ConfigurationProperties config;
    try {
      config = new ConfigurationProperties(basedir, sourcedir, targetversion, datascenario, layeredBuild,
          layeredScenario, onbuilderror, oncleanerror, delimitersequence, solodelimiter, casesensitivedelimiter,
          treatwarningsas, localproperties, feedback);

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

}
