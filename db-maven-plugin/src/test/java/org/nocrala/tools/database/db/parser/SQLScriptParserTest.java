package org.nocrala.tools.database.db.parser;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SQLScriptParserTest {

  @Test
  public void testEmpty() throws IOException {
    ScriptSQLStatement[] t;

    t = readStatements("");
    assertEquals(0, t.length);
  }

  @Test
  public void testBlanks() throws IOException {
    ScriptSQLStatement[] t;

    t = readStatements(" ");
    assertEquals(0, t.length);

    t = readStatements(" ");
    assertEquals(0, t.length);

    t = readStatements("\t");
    assertEquals(0, t.length);

    t = readStatements(" \n ");
    assertEquals(0, t.length);

    t = readStatements(" \r ");
    assertEquals(0, t.length);

    t = readStatements(" \n \n \n \n\n\n ");
    assertEquals(0, t.length);
  }

  @Test
  public void testCommentsOnly() throws IOException {
    ScriptSQLStatement[] t;

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
    ScriptSQLStatement[] t;

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
  }

  @Test
  public void testTwoStatements() throws IOException {
    ScriptSQLStatement[] t;

    t = readStatements("\n ; \ta ; \n --\n; b \t--;");

    assertEquals(2, t.length);

    assertEquals(2, t[0].getLine());
    assertEquals(3, t[0].getCol());
    assertEquals("a", t[0].getSql());

    assertEquals(4, t[1].getLine());
    assertEquals(2, t[1].getCol());
    assertEquals("b", t[1].getSql());
  }

  @Test
  public void testGeneralQuotes() throws IOException {
    ScriptSQLStatement[] t;

    // Simple

    t = readStatements("a'b;'");
    assertEquals(1, t.length);

    t = readStatements("''");
    assertEquals(1, t.length);

    t = readStatements("'a''b'");
    assertEquals(1, t.length);

    t = readStatements("a\"b;\"");
    assertEquals(1, t.length);

    t = readStatements("\"\"");
    assertEquals(1, t.length);
  }

  @Test
  public void testOracleQuotes() throws IOException {
    ScriptSQLStatement[] t;

    t = readStatements("an'x;'b");
    assertEquals(1, t.length);

    t = readStatements("an'x;'''b");
    assertEquals(1, t.length);

    t = readStatements("aN'x;'b");
    assertEquals(1, t.length);

    t = readStatements("aN'x;'''b");
    assertEquals(1, t.length);

    // Custom quotes

    tryOracleCombo("q", '_', '_');
    tryOracleCombo("q", '<', '>');
    tryOracleCombo("q", '[', ']');
    tryOracleCombo("q", '{', '}');
    tryOracleCombo("q", '(', ')');
    tryOracleCombo("Q", '_', '_');
    tryOracleCombo("Q", '<', '>');
    tryOracleCombo("Q", '[', ']');
    tryOracleCombo("Q", '{', '}');
    tryOracleCombo("Q", '(', ')');

    tryOracleCombo("nq", '_', '_');
    tryOracleCombo("nq", '<', '>');
    tryOracleCombo("nq", '[', ']');
    tryOracleCombo("nq", '{', '}');
    tryOracleCombo("nq", '(', ')');
    tryOracleCombo("nQ", '_', '_');
    tryOracleCombo("nQ", '<', '>');
    tryOracleCombo("nQ", '[', ']');
    tryOracleCombo("nQ", '{', '}');
    tryOracleCombo("nQ", '(', ')');

    tryOracleCombo("Nq", '_', '_');
    tryOracleCombo("Nq", '<', '>');
    tryOracleCombo("Nq", '[', ']');
    tryOracleCombo("Nq", '{', '}');
    tryOracleCombo("Nq", '(', ')');
    tryOracleCombo("NQ", '_', '_');
    tryOracleCombo("NQ", '<', '>');
    tryOracleCombo("NQ", '[', ']');
    tryOracleCombo("NQ", '{', '}');
    tryOracleCombo("NQ", '(', ')');
  }

  private void tryOracleCombo(final String prefix, final char open, final char close) throws IOException {
    ScriptSQLStatement[] t = readStatements("a" + prefix + "'" + open + "x;'c" + close + "'b;h");
    assertEquals(2, t.length);
  }

  @Test
  public void testQuoteOpening() throws IOException {

    QuoteOpening a = new QuoteOpening(1, new QuoteParser("'", "'"));
    QuoteOpening b = new QuoteOpening(2, new QuoteParser("'", "'"));

    assertEquals(null, QuoteOpening.first(null, null));
    assertEquals(a, QuoteOpening.first(a, null));
    assertEquals(a, QuoteOpening.first(null, a));
    assertEquals(a, QuoteOpening.first(a, a));
    assertEquals(a, QuoteOpening.first(a, b));
    assertEquals(a, QuoteOpening.first(b, a));

  }

  @Test
  public void testPostgreSQLQuotes() throws IOException {
    ScriptSQLStatement[] t;

    t = readStatements("ae'x;\\'f'b;h");
    assertEquals(2, t.length);

    t = readStatements("aE'x;\\'f'b;h");
    assertEquals(2, t.length);

    t = readStatements("a$$x;'f$$b;h");
    assertEquals(2, t.length);

    t = readStatements("a$tag1$x;'f$tag1$b;h");
    assertEquals(2, t.length);

    t = readStatements("a$tag1$x;'f$tag2$b;h");
    assertEquals(2, t.length);
  }

  @Test
  public void testMariaDBQuotes() throws IOException {
    ScriptSQLStatement[] t;

    t = readStatements("a'x;\\'f\\\\'b;h");
    // System.out.println("t.length=" + t.length);
    // for (ScriptSQLStatement s : t) {
    // System.out.println(" - " + s);
    // }
    assertEquals(2, t.length);
  }

  // Utils

  private ScriptSQLStatement[] readStatements(final String s) throws IOException {
    StringReader r = new StringReader(s);

    List<ScriptSQLStatement> sts = new ArrayList<>();
    ScriptSQLStatement st = null;
    SQLScriptParser sqlParser = new SQLScriptParser(r, ";");
    while ((st = sqlParser.readStatement()) != null) {
      sts.add(st);
    }
    return sts.toArray(new ScriptSQLStatement[0]);
  }

}
