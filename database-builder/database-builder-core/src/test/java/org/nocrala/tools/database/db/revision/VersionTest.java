package org.nocrala.tools.database.db.revision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class VersionTest {

  @Test
  public void testParsing() {

    Version v;

    try {
      new Version(null);
      fail("Null versions are not valid");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version("");
      fail("Empty versions are not valid");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    // Single chars

    new Version("0");
    new Version("9");
    new Version("a");
    new Version("z");

    // Multiple chars

    new Version("ab");
    new Version("AB");
    new Version("12");
    new Version("09");

    // Multiple atoms

    new Version("1a");
    new Version("a1");

    new Version("1a2");
    new Version("a1b");

    new Version("12ab34cd");
    new Version("ab12cd34");

    // Checking specific invalid chars

    try {
      new Version(" ");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version("\t");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version(".");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version("-");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version("_");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version(".1");
      fail("version cannot start with a dot");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Version(".a");
      fail("version cannot start with a dot");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    // Snapshot versions are allowed

    v = new Version("1-SNAPSHOT"); // Maven snapshot
    v = new Version("1-snapshot"); // Docker snapshot (Docker accepts lower case only)
    try {
      new Version("1-Snapshot");
      fail("Snapshot versions only accept full upper case SNAPSHOT, or full lower case snapshot");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }
    try {
      new Version("1-snaPshot");
      fail("Snapshot versions only accept full upper case SNAPSHOT, or full lower case snapshot");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }
    try {
      new Version("1-snapshot ");
      fail("Versions must not have trailing blanks");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    // Multi-part versions

    v = new Version("1.");
    v = new Version("a.");

    v = new Version("1.2");
    v = new Version("1.b");
    v = new Version("a.1");
    v = new Version("a.b");

    v = new Version("1.2.");
    v = new Version("1.b.");
    v = new Version("a.1.");
    v = new Version("a.b.");

    v = new Version("123abc.DEF456.789gHi012jkL.345mNo");

  }

  @Test
  public void testEquality() {

    // digits

    assertEquals(0, new Version("0").compareTo(new Version("0")));
    assertEquals(0, new Version("1").compareTo(new Version("1")));
    assertEquals(0, new Version("10").compareTo(new Version("10")));
    assertEquals(0, new Version("1234567890").compareTo(new Version("1234567890")));
    assertEquals(0, new Version("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Version("123456789012345678901234567890123456789012345678901234567890")));

    assertEquals(0, new Version("1.23.456.7890").compareTo(new Version("1.23.456.7890")));

    // alphabetic

    assertEquals(0, new Version("").compareTo(new Version("")));
    assertEquals(0, new Version("a").compareTo(new Version("a")));
    assertEquals(0, new Version("abc").compareTo(new Version("abc")));
    assertEquals(0, new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));
    assertEquals(0, new Version("a.bc.def.ghij").compareTo(new Version("a.bc.def.ghij")));

  }

  @Test
  public void testLessThan() {

    // Empty segment always before integers and/or strings

    assertNeg(new Version("").compareTo(new Version("0")));
    assertNeg(new Version("").compareTo(new Version("1")));
    assertNeg(new Version("").compareTo(new Version("12")));
    assertNeg(new Version("").compareTo(new Version("123")));

    assertNeg(new Version("").compareTo(new Version("a")));
    assertNeg(new Version("").compareTo(new Version("b")));
    assertNeg(new Version("").compareTo(new Version("ab")));
    assertNeg(new Version("").compareTo(new Version("abc")));

    // integers always before strings

    assertNeg(new Version("0").compareTo(new Version("a")));
    assertNeg(new Version("0").compareTo(new Version("A")));
    assertNeg(new Version("0").compareTo(new Version("ab")));
    assertNeg(new Version("0").compareTo(new Version("AB")));

    assertNeg(new Version("123").compareTo(new Version("a")));
    assertNeg(new Version("123").compareTo(new Version("A")));
    assertNeg(new Version("123").compareTo(new Version("ab")));
    assertNeg(new Version("123").compareTo(new Version("AB")));

    // integers

    assertNeg(new Version("0").compareTo(new Version("1")));
    assertNeg(new Version("1").compareTo(new Version("2")));
    assertNeg(new Version("9").compareTo(new Version("10")));
    assertNeg(new Version("10").compareTo(new Version("11")));
    assertNeg(new Version("1234567890").compareTo(new Version("1234567891")));
    assertNeg(new Version("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Version("123456789012345678901234567890123456789012345678901234567891")));

    // strings

    assertNeg(new Version("a").compareTo(new Version("b")));
    assertNeg(new Version("A").compareTo(new Version("a")));
    assertNeg(new Version("a").compareTo(new Version("aa")));
    assertNeg(new Version("a").compareTo(new Version("ab")));
    assertNeg(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")));
    assertNeg(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkdr")));

    // mixed

    assertNeg(new Version("alpha0").compareTo(new Version("alpha1")));
    assertNeg(new Version("alpha1").compareTo(new Version("alpha2")));
    assertNeg(new Version("alpha5").compareTo(new Version("alpha10")));

    assertNeg(new Version("alpha3").compareTo(new Version("alpha3x")));
    assertNeg(new Version("alpha3x").compareTo(new Version("alpha3x1")));
    assertNeg(new Version("alpha3x5").compareTo(new Version("alpha3x10")));
    assertNeg(new Version("alpha3x5").compareTo(new Version("alpha4x5")));
    assertNeg(new Version("alpha3x5").compareTo(new Version("alpha13x5")));

    assertNeg(new Version("3alpha").compareTo(new Version("3beta")));
    assertNeg(new Version("3alpha").compareTo(new Version("3alpha1")));
    assertNeg(new Version("3alpha1").compareTo(new Version("3alpha2")));
    assertNeg(new Version("3alpha2").compareTo(new Version("3alpha10")));
    assertNeg(new Version("3alpha5").compareTo(new Version("3alpha5c")));
    assertNeg(new Version("3alpha5").compareTo(new Version("3alpha10c")));

    // multi part

    assertNeg(new Version("1").compareTo(new Version("1.")));
    assertNeg(new Version("1.").compareTo(new Version("1.0")));
    assertNeg(new Version("1.").compareTo(new Version("1.a")));
    assertNeg(new Version("1.").compareTo(new Version("1.A")));
    assertNeg(new Version("1.0").compareTo(new Version("1.0.")));

    assertNeg(new Version("1").compareTo(new Version("1-SNAPSHOT")));
    assertNeg(new Version("a").compareTo(new Version("a-SNAPSHOT")));
    assertNeg(new Version("1").compareTo(new Version("1-snapshot")));
    assertNeg(new Version("a").compareTo(new Version("a-snapshot")));

    assertNeg(new Version("1.").compareTo(new Version("1.-SNAPSHOT")));
    assertNeg(new Version("a.1").compareTo(new Version("a.1-SNAPSHOT")));
    assertNeg(new Version("1.3.").compareTo(new Version("1.3.-snapshot")));
    assertNeg(new Version("a.4.1").compareTo(new Version("a.4.1-snapshot")));

    assertNeg(new Version("1.0").compareTo(new Version("1.1")));
    assertNeg(new Version("1.0").compareTo(new Version("1.a")));
    assertNeg(new Version("1.999").compareTo(new Version("1.a")));

    assertNeg(new Version("1.a.2.b").compareTo(new Version("1.a.2.c")));
    assertNeg(new Version("1.a.2.b").compareTo(new Version("1.a.3.b")));
    assertNeg(new Version("1.a.2.b").compareTo(new Version("1.b.2.b")));
    assertNeg(new Version("1.a.2.b").compareTo(new Version("2.a.2.b")));

    assertNeg(new Version("3.0").compareTo(new Version("12")));
    assertNeg(new Version("3.0").compareTo(new Version("12.")));
    assertNeg(new Version("3.0").compareTo(new Version("12.0")));

    assertNeg(new Version("7.3.0").compareTo(new Version("7.12")));
    assertNeg(new Version("7.3.0").compareTo(new Version("7.12.")));
    assertNeg(new Version("7.3.0").compareTo(new Version("7.12.0")));

  }

  @Test
  public void testGreaterThan() {

    // integers and strings always after empty segments

    assertPos(new Version("0").compareTo(new Version("")));
    assertPos(new Version("1").compareTo(new Version("")));
    assertPos(new Version("12").compareTo(new Version("")));
    assertPos(new Version("123").compareTo(new Version("")));

    assertPos(new Version("a").compareTo(new Version("")));
    assertPos(new Version("b").compareTo(new Version("")));
    assertPos(new Version("ab").compareTo(new Version("")));
    assertPos(new Version("abc").compareTo(new Version("")));

    // string always after integers

    assertPos(new Version("a").compareTo(new Version("0")));
    assertPos(new Version("A").compareTo(new Version("0")));
    assertPos(new Version("ab").compareTo(new Version("0")));
    assertPos(new Version("AB").compareTo(new Version("0")));

    assertPos(new Version("a").compareTo(new Version("123")));
    assertPos(new Version("A").compareTo(new Version("123")));
    assertPos(new Version("ab").compareTo(new Version("123")));
    assertPos(new Version("AB").compareTo(new Version("123")));

    // integers

    assertPos(new Version("1").compareTo(new Version("0")));
    assertPos(new Version("2").compareTo(new Version("1")));
    assertPos(new Version("10").compareTo(new Version("9")));
    assertPos(new Version("11").compareTo(new Version("10")));
    assertPos(new Version("1234567891").compareTo(new Version("1234567890")));
    assertPos(new Version("123456789012345678901234567890123456789012345678901234567891")
        .compareTo(new Version("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertPos(new Version("b").compareTo(new Version("a")));
    assertPos(new Version("a").compareTo(new Version("A")));
    assertPos(new Version("aa").compareTo(new Version("a")));
    assertPos(new Version("ab").compareTo(new Version("a")));
    assertPos(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")
        .compareTo(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));
    assertPos(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkdr")
        .compareTo(new Version("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

    // mixed

    assertPos(new Version("alpha1").compareTo(new Version("alpha0")));
    assertPos(new Version("alpha2").compareTo(new Version("alpha1")));
    assertPos(new Version("alpha10").compareTo(new Version("alpha5")));

    assertPos(new Version("alpha3x").compareTo(new Version("alpha3")));
    assertPos(new Version("alpha3x1").compareTo(new Version("alpha3x")));
    assertPos(new Version("alpha3x10").compareTo(new Version("alpha3x5")));
    assertPos(new Version("alpha4x5").compareTo(new Version("alpha3x5")));
    assertPos(new Version("alpha13x5").compareTo(new Version("alpha3x5")));

    assertPos(new Version("3beta").compareTo(new Version("3alpha")));
    assertPos(new Version("3alpha1").compareTo(new Version("3alpha")));
    assertPos(new Version("3alpha2").compareTo(new Version("3alpha1")));
    assertPos(new Version("3alpha10").compareTo(new Version("3alpha2")));
    assertPos(new Version("3alpha5c").compareTo(new Version("3alpha5")));
    assertPos(new Version("3alpha10c").compareTo(new Version("3alpha5")));

    // multi part

    assertNeg(new Version("1.").compareTo(new Version("1")));
    assertNeg(new Version("1.0").compareTo(new Version("1.")));
    assertNeg(new Version("1.a").compareTo(new Version("1.")));
    assertNeg(new Version("1.A").compareTo(new Version("1.")));
    assertNeg(new Version("1.0.").compareTo(new Version("1.0")));

    assertNeg(new Version("1-SNAPSHOT").compareTo(new Version("1")));
    assertNeg(new Version("a-SNAPSHOT").compareTo(new Version("a")));
    assertNeg(new Version("1-snapshot").compareTo(new Version("1")));
    assertNeg(new Version("a-snapshot").compareTo(new Version("a")));

    assertNeg(new Version("1.-SNAPSHOT").compareTo(new Version("1.")));
    assertNeg(new Version("a.1-SNAPSHOT").compareTo(new Version("a.1")));

    assertNeg(new Version("1.3.-snapshot").compareTo(new Version("1.3.")));
    assertNeg(new Version("a.4.1-snapshot").compareTo(new Version("a.4.1")));

    assertNeg(new Version("1.1").compareTo(new Version("1.0")));
    assertNeg(new Version("1.a").compareTo(new Version("1.0")));
    assertNeg(new Version("1.a").compareTo(new Version("1.999")));

    assertNeg(new Version("1.a.2.c").compareTo(new Version("1.a.2.b")));
    assertNeg(new Version("1.a.3.b").compareTo(new Version("1.a.2.b")));
    assertNeg(new Version("1.b.2.b").compareTo(new Version("1.a.2.b")));
    assertNeg(new Version("2.a.2.b").compareTo(new Version("1.a.2.b")));

    assertNeg(new Version("12").compareTo(new Version("3.0")));
    assertNeg(new Version("12.").compareTo(new Version("3.0")));
    assertNeg(new Version("12.0").compareTo(new Version("3.0")));

    assertNeg(new Version("7.12").compareTo(new Version("7.3.0")));
    assertNeg(new Version("7.12.").compareTo(new Version("7.3.0")));
    assertNeg(new Version("7.12.0").compareTo(new Version("7.3.0")));

  }

  // Utils

  private void assertNeg(final int c) {
    assertTrue(c < 0);
  }

  private void assertPos(final int c) {
    assertTrue(c > 0);
  }

}
