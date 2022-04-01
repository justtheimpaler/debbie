package org.nocrala.tools.debbie.version;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nocrala.tools.debbie.version.Atom;
import org.nocrala.tools.debbie.version.Atom.Type;

public class AtomTest {

  private static final int MAX_CHAR_TESTED = 1000;

  @Test
  public void testParsing() {

    Atom a = null;

    try {
      new Atom(null);
      fail("Null atoms are not valid");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Atom("1.2");
      fail("Atoms cannot have dots in them");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    // Test alphabetic characters

    char c = 1;
    try {
      for (c = 1; c <= MAX_CHAR_TESTED; c++) {
        if (Character.isAlphabetic(c)) {
          a = new Atom("" + c);
          assertTrue(a.getType() == Type.ALPHABETIC);
        }
      }
    } catch (IllegalArgumentException e) {
      fail("Atoms should accept the alphabetic character " + ((int) c) + " ('" + c + "')");
    }

    // Test Latin digits 0-9

    try {
      for (c = '0'; c <= '9'; c++) {
        a = new Atom("" + c);
        assertTrue(a.getType() == Type.NUMERIC);
      }
    } catch (IllegalArgumentException e) {
      fail("Atoms should accept the Latin digit " + ((int) c) + " ('" + c + "')");
    }

    // Test non-alphabetic, non-digit characters

    try {
      for (c = 1; c <= MAX_CHAR_TESTED; c++) {
        if (Character.isAlphabetic(c) || (c >= '0' && c <= '9')) {
          // OK, skip it
        } else {
          new Atom("" + c);
          fail("Should not accept non-alphabetic, non-digit character " + ((int) c) + " ('" + c + "')");
        }
      }
    } catch (IllegalArgumentException e) {
    }

    // Empty String

    a = new Atom("");
    assertTrue(a.getType() == Type.EMPTY);

    // Alphabetic and digit mixes are not valid

    try {
      new Atom("a1");
      fail("Cannot mix alphabetic and digits");
    } catch (IllegalArgumentException e) {
      // OK
    }

    try {
      new Atom("1a");
      fail("Cannot mix alphabetic and digits");
    } catch (IllegalArgumentException e) {
      // OK
    }

  }

  @Test
  public void testEquality() {

    // integers

    assertEquals(0, new Atom("0").compareTo(new Atom("0")));
    assertEquals(0, new Atom("1").compareTo(new Atom("1")));
    assertEquals(0, new Atom("10").compareTo(new Atom("10")));
    assertEquals(0, new Atom("1234567890").compareTo(new Atom("1234567890")));
    assertEquals(0, new Atom("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Atom("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertEquals(0, new Atom("").compareTo(new Atom("")));
    assertEquals(0, new Atom("a").compareTo(new Atom("a")));
    assertEquals(0, new Atom("abc").compareTo(new Atom("abc")));
    assertEquals(0, new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

  }

  @Test
  public void testLessThan() {

    // Empty segment always before integers and/or strings

    assertNeg(new Atom("").compareTo(new Atom("0")));
    assertNeg(new Atom("").compareTo(new Atom("1")));
    assertNeg(new Atom("").compareTo(new Atom("12")));
    assertNeg(new Atom("").compareTo(new Atom("123")));

    assertNeg(new Atom("").compareTo(new Atom("a")));
    assertNeg(new Atom("").compareTo(new Atom("b")));
    assertNeg(new Atom("").compareTo(new Atom("ab")));
    assertNeg(new Atom("").compareTo(new Atom("abc")));

    // integers always before strings

    assertNeg(new Atom("0").compareTo(new Atom("a")));
    assertNeg(new Atom("0").compareTo(new Atom("A")));
    assertNeg(new Atom("0").compareTo(new Atom("ab")));
    assertNeg(new Atom("0").compareTo(new Atom("AB")));

    assertNeg(new Atom("123").compareTo(new Atom("a")));
    assertNeg(new Atom("123").compareTo(new Atom("A")));
    assertNeg(new Atom("123").compareTo(new Atom("ab")));
    assertNeg(new Atom("123").compareTo(new Atom("AB")));

    // integers

    assertNeg(new Atom("0").compareTo(new Atom("1")));
    assertNeg(new Atom("1").compareTo(new Atom("2")));
    assertNeg(new Atom("9").compareTo(new Atom("10")));
    assertNeg(new Atom("10").compareTo(new Atom("11")));
    assertNeg(new Atom("1234567890").compareTo(new Atom("1234567891")));
    assertNeg(new Atom("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Atom("123456789012345678901234567890123456789012345678901234567891")));

    // strings

    assertNeg(new Atom("a").compareTo(new Atom("b")));
    assertNeg(new Atom("A").compareTo(new Atom("a")));
    assertNeg(new Atom("a").compareTo(new Atom("aa")));
    assertNeg(new Atom("a").compareTo(new Atom("ab")));
    assertNeg(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")));
    assertNeg(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkdr")));

  }

  @Test
  public void testGreaterThan() {

    // integers and strings always after empty segments

    assertPos(new Atom("0").compareTo(new Atom("")));
    assertPos(new Atom("1").compareTo(new Atom("")));
    assertPos(new Atom("12").compareTo(new Atom("")));
    assertPos(new Atom("123").compareTo(new Atom("")));

    assertPos(new Atom("a").compareTo(new Atom("")));
    assertPos(new Atom("b").compareTo(new Atom("")));
    assertPos(new Atom("ab").compareTo(new Atom("")));
    assertPos(new Atom("abc").compareTo(new Atom("")));

    // string always after integers

    assertPos(new Atom("a").compareTo(new Atom("0")));
    assertPos(new Atom("A").compareTo(new Atom("0")));
    assertPos(new Atom("ab").compareTo(new Atom("0")));
    assertPos(new Atom("AB").compareTo(new Atom("0")));

    assertPos(new Atom("a").compareTo(new Atom("123")));
    assertPos(new Atom("A").compareTo(new Atom("123")));
    assertPos(new Atom("ab").compareTo(new Atom("123")));
    assertPos(new Atom("AB").compareTo(new Atom("123")));

    // integers

    assertPos(new Atom("1").compareTo(new Atom("0")));
    assertPos(new Atom("2").compareTo(new Atom("1")));
    assertPos(new Atom("10").compareTo(new Atom("9")));
    assertPos(new Atom("11").compareTo(new Atom("10")));
    assertPos(new Atom("1234567891").compareTo(new Atom("1234567890")));
    assertPos(new Atom("123456789012345678901234567890123456789012345678901234567891")
        .compareTo(new Atom("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertPos(new Atom("b").compareTo(new Atom("a")));
    assertPos(new Atom("a").compareTo(new Atom("A")));
    assertPos(new Atom("aa").compareTo(new Atom("a")));
    assertPos(new Atom("ab").compareTo(new Atom("a")));
    assertPos(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")
        .compareTo(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));
    assertPos(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkdr")
        .compareTo(new Atom("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

  }

  // Utils

  private void assertNeg(final int c) {
    assertTrue(c < 0);
  }

  private void assertPos(final int c) {
    assertTrue(c > 0);
  }

}
