package org.nocrala.tools.debbie.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtlTest {

  @Test
  public void testMin() {

    // Simple min

    assertEquals(0, Utl.min(0, 0));
    assertEquals(1, Utl.min(1, 1));
    assertEquals(2, Utl.min(2, 2));
    assertEquals(10, Utl.min(10, 10));

    assertEquals(0, Utl.min(0, 1));
    assertEquals(0, Utl.min(0, 2));
    assertEquals(0, Utl.min(0, 10));

    assertEquals(0, Utl.min(1, 0));
    assertEquals(0, Utl.min(2, 0));
    assertEquals(0, Utl.min(10, 0));

    assertEquals(1, Utl.min(2, 1));
    assertEquals(1, Utl.min(3, 1));
    assertEquals(1, Utl.min(10, 1));

    // not-found min

    assertEquals(-1, Utl.min(-1, -1));

    assertEquals(0, Utl.min(0, -1));
    assertEquals(1, Utl.min(1, -1));
    assertEquals(2, Utl.min(2, -1));
    assertEquals(10, Utl.min(10, -1));

    assertEquals(0, Utl.min(-1, 0));
    assertEquals(1, Utl.min(-1, 1));
    assertEquals(2, Utl.min(-1, 2));
    assertEquals(10, Utl.min(-1, 10));

    // Combined

// Combined sq=-1 dq=-1 del=-1 cm=2 min=-1
    int sq = -1;
    int dq = -1;
    int del = -1;
    int cm = 2;
    int min = Utl.min(Utl.min(sq, dq), Utl.min(del, cm));
    assertEquals(2, min);

  }

}
