package org.nocrala.tools.database.db.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionTest {

  @Test
  public void testNullVersion() {
    assertTrue(new Version("").after(null));
    assertTrue(new Version("1").after(null));
    assertTrue(new Version("a").after(null));
    assertTrue(new Version("1.2").after(null));
    assertTrue(new Version("a.b").after(null));
  }

  @Test
  public void testSame() {
    assertTrue(new Version("").same(new Version("")));
    assertTrue(new Version("1").same(new Version("1")));
    assertTrue(new Version("0").same(new Version("0")));
    assertTrue(new Version("1.2").same(new Version("1.2")));
    assertTrue(new Version("a").same(new Version("a")));
    assertTrue(new Version("b").same(new Version("b")));
    assertTrue(new Version("a.b").same(new Version("a.b")));

    assertTrue(new Version("a.").same(new Version("a.")));
    assertTrue(new Version(".a").same(new Version(".a")));
    assertTrue(new Version("1.").same(new Version("1.")));
    assertTrue(new Version(".1").same(new Version(".1")));

    assertTrue(new Version("a.b.").same(new Version("a.b.")));
    assertTrue(new Version(".a.b").same(new Version(".a.b")));
    assertTrue(new Version("1.2.").same(new Version("1.2.")));
    assertTrue(new Version(".1.2").same(new Version(".1.2")));

    assertTrue(new Version("1.a").same(new Version("1.a")));
  }

  @Test
  public void testBefore() {
    ba(new Version(""), new Version("a"));
    ba(new Version(""), new Version("1"));
    ba(new Version(""), new Version("a.b"));
    ba(new Version(""), new Version("1.2"));

    ba(new Version("1"), new Version("2"));
    ba(new Version("0"), new Version("1"));
    ba(new Version("a"), new Version("b"));
    ba(new Version("A"), new Version("B"));

    ba(new Version("9"), new Version("10"));
    ba(new Version("A"), new Version("a"));

    ba(new Version("1.0"), new Version("1.1"));
    ba(new Version("1.0"), new Version("1.0."));
    ba(new Version("1.0"), new Version("1.0.3"));
    ba(new Version("1.0."), new Version("1.0.3"));

    ba(new Version("1.9"), new Version("1.10"));
    ba(new Version("1.a.5"), new Version("1.a.6"));
    ba(new Version("a.b"), new Version("a.c"));
    ba(new Version("a.b"), new Version("a.b."));
    ba(new Version("a.b"), new Version("a.b.c"));
    ba(new Version("a.b."), new Version("a.b.c"));

    ba(new Version("1.5.4.3.2"), new Version("1.5.4.3.3"));

    ba(new Version("a.e.d.c.b"), new Version("a.e.d.c.c"));

  }

  public void ba(final Version vb, final Version va) {
    assertNotNull(vb);
    assertNotNull(va);
    assertTrue(vb.before(va));
    assertTrue(va.after(vb));
  }

}
