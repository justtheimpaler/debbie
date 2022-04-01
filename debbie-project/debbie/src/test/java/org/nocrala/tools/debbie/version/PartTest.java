package org.nocrala.tools.debbie.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nocrala.tools.debbie.version.Part;

public class PartTest {

  @Test
  public void testParsing() {

    try {
      new Part(null);
      fail("Null parts are not valid");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Part("1.2");
      fail("Parts cannot have dots in them");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    // Empty part is valid

    new Part("");

    // Single chars

    new Part("0");
    new Part("9");
    new Part("a");
    new Part("z");

    // Multiple chars

    new Part("ab");
    new Part("AB");
    new Part("12");
    new Part("09");

    // Multiple atoms

    new Part("1a");
    new Part("a1");

    new Part("1a2");
    new Part("a1b");

    new Part("12ab34cd");
    new Part("ab12cd34");

    // Checking specific invalid chars

    try {
      new Part(" ");
      fail("A space cannot be a version part");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Part("\t");
      fail("A tab cannot be a version part");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Part(".");
      fail("A dot cannot be a version part");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Part("-");
      fail("A dash cannot be a version part");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Part("_");
      fail("An underscore cannot be a version part");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    new Part("10alpha31beta7x");

  }

  @Test
  public void testEquality() {

    // integers

    assertEquals(0, new Part("0").compareTo(new Part("0")));
    assertEquals(0, new Part("1").compareTo(new Part("1")));
    assertEquals(0, new Part("10").compareTo(new Part("10")));
    assertEquals(0, new Part("1234567890").compareTo(new Part("1234567890")));
    assertEquals(0, new Part("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Part("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertEquals(0, new Part("").compareTo(new Part("")));
    assertEquals(0, new Part("a").compareTo(new Part("a")));
    assertEquals(0, new Part("abc").compareTo(new Part("abc")));
    assertEquals(0, new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

  }

  @Test
  public void testLessThan() {

    // Empty segment always before integers and/or strings

    assertNeg(new Part("").compareTo(new Part("0")));
    assertNeg(new Part("").compareTo(new Part("1")));
    assertNeg(new Part("").compareTo(new Part("12")));
    assertNeg(new Part("").compareTo(new Part("123")));

    assertNeg(new Part("").compareTo(new Part("a")));
    assertNeg(new Part("").compareTo(new Part("b")));
    assertNeg(new Part("").compareTo(new Part("ab")));
    assertNeg(new Part("").compareTo(new Part("abc")));

    // integers always before strings

    assertNeg(new Part("0").compareTo(new Part("a")));
    assertNeg(new Part("0").compareTo(new Part("A")));
    assertNeg(new Part("0").compareTo(new Part("ab")));
    assertNeg(new Part("0").compareTo(new Part("AB")));

    assertNeg(new Part("123").compareTo(new Part("a")));
    assertNeg(new Part("123").compareTo(new Part("A")));
    assertNeg(new Part("123").compareTo(new Part("ab")));
    assertNeg(new Part("123").compareTo(new Part("AB")));

    // integers

    assertNeg(new Part("0").compareTo(new Part("1")));
    assertNeg(new Part("1").compareTo(new Part("2")));
    assertNeg(new Part("9").compareTo(new Part("10")));
    assertNeg(new Part("10").compareTo(new Part("11")));
    assertNeg(new Part("1234567890").compareTo(new Part("1234567891")));
    assertNeg(new Part("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Part("123456789012345678901234567890123456789012345678901234567891")));

    // strings

    assertNeg(new Part("a").compareTo(new Part("b")));
    assertNeg(new Part("A").compareTo(new Part("a")));
    assertNeg(new Part("a").compareTo(new Part("aa")));
    assertNeg(new Part("a").compareTo(new Part("ab")));
    assertNeg(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")));
    assertNeg(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkdr")));

    // mixed

    assertNeg(new Part("alpha0").compareTo(new Part("alpha1")));
    assertNeg(new Part("alpha1").compareTo(new Part("alpha2")));
    assertNeg(new Part("alpha5").compareTo(new Part("alpha10")));

    assertNeg(new Part("alpha3").compareTo(new Part("alpha3x")));
    assertNeg(new Part("alpha3x").compareTo(new Part("alpha3x1")));
    assertNeg(new Part("alpha3x5").compareTo(new Part("alpha3x10")));
    assertNeg(new Part("alpha3x5").compareTo(new Part("alpha4x5")));
    assertNeg(new Part("alpha3x5").compareTo(new Part("alpha13x5")));

    assertNeg(new Part("3alpha").compareTo(new Part("3beta")));
    assertNeg(new Part("3alpha").compareTo(new Part("3alpha1")));
    assertNeg(new Part("3alpha1").compareTo(new Part("3alpha2")));
    assertNeg(new Part("3alpha2").compareTo(new Part("3alpha10")));
    assertNeg(new Part("3alpha5").compareTo(new Part("3alpha5c")));
    assertNeg(new Part("3alpha5").compareTo(new Part("3alpha10c")));

  }

  @Test
  public void testGreaterThan() {

    // integers and strings always after empty segments

    assertPos(new Part("0").compareTo(new Part("")));
    assertPos(new Part("1").compareTo(new Part("")));
    assertPos(new Part("12").compareTo(new Part("")));
    assertPos(new Part("123").compareTo(new Part("")));

    assertPos(new Part("a").compareTo(new Part("")));
    assertPos(new Part("b").compareTo(new Part("")));
    assertPos(new Part("ab").compareTo(new Part("")));
    assertPos(new Part("abc").compareTo(new Part("")));

    // string always after integers

    assertPos(new Part("a").compareTo(new Part("0")));
    assertPos(new Part("A").compareTo(new Part("0")));
    assertPos(new Part("ab").compareTo(new Part("0")));
    assertPos(new Part("AB").compareTo(new Part("0")));

    assertPos(new Part("a").compareTo(new Part("123")));
    assertPos(new Part("A").compareTo(new Part("123")));
    assertPos(new Part("ab").compareTo(new Part("123")));
    assertPos(new Part("AB").compareTo(new Part("123")));

    // integers

    assertPos(new Part("1").compareTo(new Part("0")));
    assertPos(new Part("2").compareTo(new Part("1")));
    assertPos(new Part("10").compareTo(new Part("9")));
    assertPos(new Part("11").compareTo(new Part("10")));
    assertPos(new Part("1234567891").compareTo(new Part("1234567890")));
    assertPos(new Part("123456789012345678901234567890123456789012345678901234567891")
        .compareTo(new Part("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertPos(new Part("b").compareTo(new Part("a")));
    assertPos(new Part("a").compareTo(new Part("A")));
    assertPos(new Part("aa").compareTo(new Part("a")));
    assertPos(new Part("ab").compareTo(new Part("a")));
    assertPos(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")
        .compareTo(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));
    assertPos(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkdr")
        .compareTo(new Part("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

    // mixed

    assertPos(new Part("alpha1").compareTo(new Part("alpha0")));
    assertPos(new Part("alpha2").compareTo(new Part("alpha1")));
    assertPos(new Part("alpha10").compareTo(new Part("alpha5")));

    assertPos(new Part("alpha3x").compareTo(new Part("alpha3")));
    assertPos(new Part("alpha3x1").compareTo(new Part("alpha3x")));
    assertPos(new Part("alpha3x10").compareTo(new Part("alpha3x5")));
    assertPos(new Part("alpha4x5").compareTo(new Part("alpha3x5")));
    assertPos(new Part("alpha13x5").compareTo(new Part("alpha3x5")));

    assertPos(new Part("3beta").compareTo(new Part("3alpha")));
    assertPos(new Part("3alpha1").compareTo(new Part("3alpha")));
    assertPos(new Part("3alpha2").compareTo(new Part("3alpha1")));
    assertPos(new Part("3alpha10").compareTo(new Part("3alpha2")));
    assertPos(new Part("3alpha5c").compareTo(new Part("3alpha5")));
    assertPos(new Part("3alpha10c").compareTo(new Part("3alpha5")));

  }

  // Utils

  private void assertNeg(final int c) {
    assertTrue(c < 0);
  }

  private void assertPos(final int c) {
    assertTrue(c > 0);
  }

}
