package org.nocrala.tools.database.db.version;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.nocrala.tools.database.db.version.VersionNumber;

public class VersionNumberTest {

  @Test
  public void testNullVersion() {
    assertTrue(new VersionNumber("").after(null));
    assertTrue(new VersionNumber("1").after(null));
    assertTrue(new VersionNumber("a").after(null));
    assertTrue(new VersionNumber("1.2").after(null));
    assertTrue(new VersionNumber("a.b").after(null));
  }

  @Test
  public void testSame() {
    assertTrue(new VersionNumber("").same(new VersionNumber("")));
    assertTrue(new VersionNumber("1").same(new VersionNumber("1")));
    assertTrue(new VersionNumber("0").same(new VersionNumber("0")));
    assertTrue(new VersionNumber("1.2").same(new VersionNumber("1.2")));
    assertTrue(new VersionNumber("a").same(new VersionNumber("a")));
    assertTrue(new VersionNumber("b").same(new VersionNumber("b")));
    assertTrue(new VersionNumber("a.b").same(new VersionNumber("a.b")));

    assertTrue(new VersionNumber("a.").same(new VersionNumber("a.")));
    assertTrue(new VersionNumber(".a").same(new VersionNumber(".a")));
    assertTrue(new VersionNumber("1.").same(new VersionNumber("1.")));
    assertTrue(new VersionNumber(".1").same(new VersionNumber(".1")));

    assertTrue(new VersionNumber("a.b.").same(new VersionNumber("a.b.")));
    assertTrue(new VersionNumber(".a.b").same(new VersionNumber(".a.b")));
    assertTrue(new VersionNumber("1.2.").same(new VersionNumber("1.2.")));
    assertTrue(new VersionNumber(".1.2").same(new VersionNumber(".1.2")));

    assertTrue(new VersionNumber("1.a").same(new VersionNumber("1.a")));
  }

  @Test
  public void testBefore() {
    ba(new VersionNumber(""), new VersionNumber("a"));
    ba(new VersionNumber(""), new VersionNumber("1"));
    ba(new VersionNumber(""), new VersionNumber("a.b"));
    ba(new VersionNumber(""), new VersionNumber("1.2"));

    ba(new VersionNumber("1"), new VersionNumber("2"));
    ba(new VersionNumber("0"), new VersionNumber("1"));
    ba(new VersionNumber("a"), new VersionNumber("b"));
    ba(new VersionNumber("A"), new VersionNumber("B"));

    ba(new VersionNumber("9"), new VersionNumber("10"));
    ba(new VersionNumber("A"), new VersionNumber("a"));

    ba(new VersionNumber("1.0"), new VersionNumber("1.1"));
    ba(new VersionNumber("1.0"), new VersionNumber("1.0."));
    ba(new VersionNumber("1.0"), new VersionNumber("1.0.3"));
    ba(new VersionNumber("1.0."), new VersionNumber("1.0.3"));

    ba(new VersionNumber("1.9"), new VersionNumber("1.10"));
    ba(new VersionNumber("1.a.5"), new VersionNumber("1.a.6"));
    ba(new VersionNumber("a.b"), new VersionNumber("a.c"));
    ba(new VersionNumber("a.b"), new VersionNumber("a.b."));
    ba(new VersionNumber("a.b"), new VersionNumber("a.b.c"));
    ba(new VersionNumber("a.b."), new VersionNumber("a.b.c"));

    ba(new VersionNumber("1.5.4.3.2"), new VersionNumber("1.5.4.3.3"));
    ba(new VersionNumber("a.e.d.c.b"), new VersionNumber("a.e.d.c.c"));
  }

  public void ba(final VersionNumber vb, final VersionNumber va) {
    assertNotNull(vb);
    assertNotNull(va);
    assertTrue(vb.before(va));
    assertTrue(va.after(vb));
  }

}
