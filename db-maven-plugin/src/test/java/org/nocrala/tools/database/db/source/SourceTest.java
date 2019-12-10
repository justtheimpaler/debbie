package org.nocrala.tools.database.db.source;

import java.io.File;

import org.junit.Test;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotConnectToDatabaseException;
import org.nocrala.tools.database.db.executor.SQLExecutor.CouldNotReadSQLScriptException;
import org.nocrala.tools.database.db.executor.SQLExecutor.InvalidPropertiesFileException;
import org.nocrala.tools.database.db.executor.SQLExecutor.SQLScriptAbortedException;
import org.nocrala.tools.database.db.utils.VersionNumber;

public class SourceTest {

  @Test
  public void testMain() {

    // Parameters

    String databaseDir = "testdata/dbsrc";
    String version = "1.1.0";
    String scenario = "dev1";

    boolean layeredBuild = true;
    boolean layeredScenarios = false;
    boolean buildOnErrorContinue = false;
    boolean cleanOnErrorContinue = true;

    String localdatabaseproperties = "testdata/localdatabase.properties";

    // Execution

    File dbdir = new File(databaseDir);
    File propsFile = new File(localdatabaseproperties);
    VersionNumber currentVersion = new VersionNumber(version);

    Source s = new Source(dbdir, layeredBuild, layeredScenarios, buildOnErrorContinue, cleanOnErrorContinue);

    SQLExecutor sqlExecutor = null;

    try {
      sqlExecutor = new SQLExecutor(propsFile);
    } catch (InvalidPropertiesFileException e) {
      System.out.println("Invalid properties file.");
      System.out.println(" -> " + renderException(e));
      e.printStackTrace();
      return;
    } catch (CouldNotConnectToDatabaseException e) {
      System.out.println("Could not connect to database.");
      System.out.println(" -> " + renderException(e));
      e.printStackTrace();
      return;
    }

    try {

       s.build(currentVersion, scenario, sqlExecutor);
//       s.clean(currentVersion, sqlExecutor);
//      s.rebuild(currentVersion, scenario, sqlExecutor);

    } catch (CouldNotReadSQLScriptException e) {
      System.out.println("Could not read SQL script file.");
      System.out.println(" -> " + renderException(e));
      e.printStackTrace();
      return;
    } catch (SQLScriptAbortedException e) {
      System.out.println("SQL statement failed.");
      System.out.println(" -> " + renderException(e));
      e.printStackTrace();
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
