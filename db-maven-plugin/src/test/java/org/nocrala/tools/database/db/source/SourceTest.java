package org.nocrala.tools.database.db.source;

import java.io.File;

import org.junit.Test;
import org.nocrala.tools.database.db.utils.VersionNumber;

public class SourceTest {

  @Test
  public void testNullVersion() {
    File dbdir = new File("testdata/dbsrc");
    Source s = new Source(dbdir);

    VersionNumber currentVersion = new VersionNumber("1.1.0");
    s.build(currentVersion, "dev1");
  }

}
