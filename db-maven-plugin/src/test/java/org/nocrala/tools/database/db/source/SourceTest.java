package org.nocrala.tools.database.db.source;

import java.io.File;

import org.junit.Test;
import org.nocrala.tools.database.db.executor.Delimiter;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.executor.SQLExecutor.TreatWarningAs;
import org.nocrala.tools.database.db.source.Source.InvalidDatabaseSourceException;
import org.nocrala.tools.database.db.utils.VersionNumber;

public class SourceTest {

  @Test
  public void testMain() {

    // performTest();

  }

  private void performTest() {
    // Parameters

    String databaseDir = "testdata/dbsrc";
    String version = "1.1.0";
    String scenario = "dev1";

    boolean layeredBuild = true;
    boolean layeredScenarios = false;
    boolean buildOnErrorContinue = false;
    boolean cleanOnErrorContinue = true;

    String localdatabaseproperties = "testdata/localdatabase.properties";

    Delimiter del = new Delimiter(";", false, false);

    // Execution

    TestFeedback feedback = new TestFeedback();

    File dbdir = new File(databaseDir);
    File propsFile = new File(localdatabaseproperties);
    VersionNumber currentVersion = new VersionNumber(version);

    Source s;
    try {
      s = new Source(dbdir, layeredBuild, layeredScenarios, buildOnErrorContinue, cleanOnErrorContinue, feedback);
    } catch (InvalidDatabaseSourceException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return;
    }

    SQLExecutor sqlExecutor = null;

    try {
      sqlExecutor = new SQLExecutor(propsFile, feedback, del, TreatWarningAs.ERROR);
    } catch (InvalidPropertiesFileException e) {
      System.out.println("Invalid properties file: " + renderException(e));
      // e.printStackTrace();
      return;
    } catch (CouldNotConnectToDatabaseException e) {
      System.out.println("Could not connect to database: " + renderException(e));
      // e.printStackTrace();
      return;
    }

    try {

      s.build(currentVersion, scenario, sqlExecutor);
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
