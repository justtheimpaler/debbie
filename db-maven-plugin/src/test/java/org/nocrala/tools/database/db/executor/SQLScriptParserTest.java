package org.nocrala.tools.database.db.executor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nocrala.tools.database.db.executor.SQLScriptParser.ScriptStatement;

public class SQLScriptParserTest {

  @Test
  public void testEmpty() throws IOException {
    ScriptStatement[] t;

    t = readStatements("");
    assertEquals(0, t.length);

  }

  @Test
  public void testBlanks() throws IOException {
    ScriptStatement[] t;

    t = readStatements(" ");
    assertEquals(0, t.length);

    t = readStatements("  ");
    assertEquals(0, t.length);

    t = readStatements("\t");
    assertEquals(0, t.length);

    t = readStatements(" \n ");
    assertEquals(0, t.length);

    t = readStatements(" \r ");
    assertEquals(0, t.length);

    t = readStatements(" \n  \n \n \n\n\n ");
    assertEquals(0, t.length);
  }

  @Test
  public void testCommentsOnly() throws IOException {
    ScriptStatement[] t;

    t = readStatements("--");
    assertEquals(0, t.length);

    t = readStatements("-- ");
    assertEquals(0, t.length);

    t = readStatements(" --");
    assertEquals(0, t.length);

    t = readStatements("\n--");
    assertEquals(0, t.length);

    t = readStatements("\n-- ");
    assertEquals(0, t.length);

    t = readStatements("\n --");
    assertEquals(0, t.length);

    t = readStatements("\n--\n --");
    assertEquals(0, t.length);

  }

  @Test
  public void testOneStatement() throws IOException {
    ScriptStatement[] t;

    t = readStatements("select * from t");
    assertEquals(1, t.length);

    t = readStatements("select * from t;");
    assertEquals(1, t.length);

    t = readStatements(";select * from t;");
    assertEquals(1, t.length);

    t = readStatements(" select * from t ");
    assertEquals(1, t.length);

    t = readStatements("\nselect\n*\nfrom\nt\n");
    assertEquals(1, t.length);

    t = readStatements(";\n  select\n*\nfrom\nt\n;");
    assertEquals(1, t.length);
    assertEquals(2, t[0].getLine());
    assertEquals(3, t[0].getCol());
    System.out.print(">>> s=" + t[0]);

  }

   @Test
   public void testTwoStatements() throws IOException {
     ScriptStatement[] t;

     t = readStatements("\n ; \ta ; \n --\n; b \t--;");

     assertEquals(2, t.length);
     
     assertEquals(2, t[0].getLine());
     assertEquals(3, t[0].getCol());
     assertEquals("a", t[0].getSql());
     
     assertEquals(4, t[1].getLine());
     assertEquals(2, t[1].getCol());
     assertEquals("b", t[1].getSql());

   }

  // Utils

  private ScriptStatement[] readStatements(final String s) throws IOException {
    StringReader r = new StringReader(s);

    List<ScriptStatement> sts = new ArrayList<>();
    ScriptStatement st = null;
    SQLScriptParser sqlParser = new SQLScriptParser(r, ";");
    while ((st = sqlParser.readStatement()) != null) {
      sts.add(st);
    }
    return sts.toArray(new ScriptStatement[0]);
  }

}
