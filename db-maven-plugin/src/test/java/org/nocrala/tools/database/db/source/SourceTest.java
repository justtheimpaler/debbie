package org.nocrala.tools.database.db.source;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;
import org.nocrala.tools.database.db.executor.SQLExecutor;
import org.nocrala.tools.database.db.executor.SQLExecutor.JDBCInitializationException;
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
    } catch (IOException e) {
      e.printStackTrace();
      return;
    } catch (JDBCInitializationException e) {
      e.printStackTrace();
      return;
    }

    try {

      s.build(currentVersion, scenario, sqlExecutor);
      s.clean(currentVersion, sqlExecutor);
      s.rebuild(currentVersion, scenario, sqlExecutor);

    } catch (SQLException e) {
      e.printStackTrace();
      return;
    }

  }

}
