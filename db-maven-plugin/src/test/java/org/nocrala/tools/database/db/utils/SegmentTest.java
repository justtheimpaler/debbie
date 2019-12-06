package org.nocrala.tools.database.db.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.nocrala.tools.database.db.utils.Segment.Type;

public class SegmentTest {

  @Test
  public void testNonNullSegment() {

    Segment s = null;

    try {
      new Segment(null);
      fail("Null segments are not valid");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    try {
      new Segment("1.2");
      fail("Segments cannot have dots in them");
    } catch (IllegalArgumentException e) {
      // OK - test passed
    }

    s = new Segment("");
    assertTrue(s.getType() == Type.EMPTY);

    s = new Segment("a");
    assertTrue(s.getType() == Type.STRING);

    s = new Segment("0");
    assertTrue(s.getType() == Type.NUMERIC);

    s = new Segment("-1"); // becomes a string
    assertTrue(s.getType() == Type.STRING);

  }

  @Test
  public void testEquality() {

    // integers

    assertEquals(0, new Segment("0").compareTo(new Segment("0")));
    assertEquals(0, new Segment("1").compareTo(new Segment("1")));
    assertEquals(0, new Segment("10").compareTo(new Segment("10")));
    assertEquals(0, new Segment("1234567890").compareTo(new Segment("1234567890")));
    assertEquals(0, new Segment("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Segment("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertEquals(0, new Segment("").compareTo(new Segment("")));
    assertEquals(0, new Segment("a").compareTo(new Segment("a")));
    assertEquals(0, new Segment("abc").compareTo(new Segment("abc")));
    assertEquals(0, new Segment("abc123").compareTo(new Segment("abc123")));
    assertEquals(0, new Segment("123abc").compareTo(new Segment("123abc")));
    assertEquals(0, new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

  }

  @Test
  public void testLessThan() {

    // Empty segment always before integers and/or strings

    assertNeg(new Segment("").compareTo(new Segment("0")));
    assertNeg(new Segment("").compareTo(new Segment("1")));
    assertNeg(new Segment("").compareTo(new Segment("12")));
    assertNeg(new Segment("").compareTo(new Segment("123")));

    assertNeg(new Segment("").compareTo(new Segment("a")));
    assertNeg(new Segment("").compareTo(new Segment("b")));
    assertNeg(new Segment("").compareTo(new Segment("ab")));
    assertNeg(new Segment("").compareTo(new Segment("abc")));

    // integers always before strings

    assertNeg(new Segment("0").compareTo(new Segment("a")));
    assertNeg(new Segment("0").compareTo(new Segment("A")));
    assertNeg(new Segment("0").compareTo(new Segment("ab")));
    assertNeg(new Segment("0").compareTo(new Segment("AB")));

    assertNeg(new Segment("123").compareTo(new Segment("a")));
    assertNeg(new Segment("123").compareTo(new Segment("A")));
    assertNeg(new Segment("123").compareTo(new Segment("ab")));
    assertNeg(new Segment("123").compareTo(new Segment("AB")));

    // integers

    assertNeg(new Segment("0").compareTo(new Segment("1")));
    assertNeg(new Segment("1").compareTo(new Segment("2")));
    assertNeg(new Segment("9").compareTo(new Segment("10")));
    assertNeg(new Segment("10").compareTo(new Segment("11")));
    assertNeg(new Segment("1234567890").compareTo(new Segment("1234567891")));
    assertNeg(new Segment("123456789012345678901234567890123456789012345678901234567890")
        .compareTo(new Segment("123456789012345678901234567890123456789012345678901234567891")));

    // strings

    assertNeg(new Segment("a").compareTo(new Segment("b")));
    assertNeg(new Segment("A").compareTo(new Segment("a")));
    assertNeg(new Segment("a").compareTo(new Segment("aa")));
    assertNeg(new Segment("a").compareTo(new Segment("ab")));
    assertNeg(new Segment("abc123").compareTo(new Segment("abc123a")));
    assertNeg(new Segment("abc123").compareTo(new Segment("abc123z")));
    assertNeg(new Segment("123abc").compareTo(new Segment("123abca")));
    assertNeg(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")));
    assertNeg(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")
        .compareTo(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd1")));

  }

  @Test
  public void testGreaterThan() {

    // integers and strings always after empty segments

    assertPos(new Segment("0").compareTo(new Segment("")));
    assertPos(new Segment("1").compareTo(new Segment("")));
    assertPos(new Segment("12").compareTo(new Segment("")));
    assertPos(new Segment("123").compareTo(new Segment("")));

    assertPos(new Segment("a").compareTo(new Segment("")));
    assertPos(new Segment("b").compareTo(new Segment("")));
    assertPos(new Segment("ab").compareTo(new Segment("")));
    assertPos(new Segment("abc").compareTo(new Segment("")));

    // string always after integers

    assertPos(new Segment("a").compareTo(new Segment("0")));
    assertPos(new Segment("A").compareTo(new Segment("0")));
    assertPos(new Segment("ab").compareTo(new Segment("0")));
    assertPos(new Segment("AB").compareTo(new Segment("0")));

    assertPos(new Segment("a").compareTo(new Segment("123")));
    assertPos(new Segment("A").compareTo(new Segment("123")));
    assertPos(new Segment("ab").compareTo(new Segment("123")));
    assertPos(new Segment("AB").compareTo(new Segment("123")));

    // integers

    assertPos(new Segment("1").compareTo(new Segment("0")));
    assertPos(new Segment("2").compareTo(new Segment("1")));
    assertPos(new Segment("10").compareTo(new Segment("9")));
    assertPos(new Segment("11").compareTo(new Segment("10")));
    assertPos(new Segment("1234567891").compareTo(new Segment("1234567890")));
    assertPos(new Segment("123456789012345678901234567890123456789012345678901234567891")
        .compareTo(new Segment("123456789012345678901234567890123456789012345678901234567890")));

    // strings

    assertPos(new Segment("b").compareTo(new Segment("a")));
    assertPos(new Segment("a").compareTo(new Segment("A")));
    assertPos(new Segment("aa").compareTo(new Segment("a")));
    assertPos(new Segment("ab").compareTo(new Segment("a")));
    assertPos(new Segment("abc123a").compareTo(new Segment("abc123")));
    assertPos(new Segment("abc123z").compareTo(new Segment("abc123")));
    assertPos(new Segment("123abcx").compareTo(new Segment("123abc")));
    assertPos(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalke")
        .compareTo(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));
    assertPos(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd1")
        .compareTo(new Segment("asldkjeqweopijsdlfksjdtliujlsirjslALDKAJSDLAKSDJalkd")));

  }

  // Utils

  private void assertNeg(final int c) {
    assertTrue(c < 0);
  }

  private void assertPos(final int c) {
    assertTrue(c > 0);
  }

}
