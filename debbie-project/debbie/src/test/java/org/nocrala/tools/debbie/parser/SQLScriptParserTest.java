package org.nocrala.tools.debbie.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nocrala.tools.debbie.executor.Delimiter;
import org.nocrala.tools.debbie.executor.Feedback;
import org.nocrala.tools.debbie.parser.QuoteOpening;
import org.nocrala.tools.debbie.parser.QuoteParser;
import org.nocrala.tools.debbie.parser.SQLScriptParser;
import org.nocrala.tools.debbie.parser.ScriptSQLStatement;
import org.nocrala.tools.debbie.parser.SQLScriptParser.InvalidSQLScriptException;

public class SQLScriptParserTest {

  @Test
  public void testEmpty() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline("");
    assertEquals(0, t.length);
  }

  @Test
  public void testBlanks() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline(" ");
    assertEquals(0, t.length);

    t = readStatementsInline(" ");
    assertEquals(0, t.length);

    t = readStatementsInline("\t");
    assertEquals(0, t.length);

    t = readStatementsInline(" \n ");
    assertEquals(0, t.length);

    t = readStatementsInline(" \r ");
    assertEquals(0, t.length);

    t = readStatementsInline(" \n \n \n \n\n\n ");
    assertEquals(0, t.length);
  }

  @Test
  public void testCommentsOnly() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline("--");
    assertEquals(0, t.length);

    t = readStatementsInline("-- ");
    assertEquals(0, t.length);

    t = readStatementsInline(" --");
    assertEquals(0, t.length);

    t = readStatementsInline("\n--");
    assertEquals(0, t.length);

    t = readStatementsInline("\n-- ");
    assertEquals(0, t.length);

    t = readStatementsInline("\n --");
    assertEquals(0, t.length);

    t = readStatementsInline("\n--\n --");
    assertEquals(0, t.length);
  }

  @Test
  public void testOneStatement() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline("select * from t");
    assertEquals(1, t.length);

    t = readStatementsInline("select * from t;");
    assertEquals(1, t.length);

    t = readStatementsInline(" select * from t ");
    assertEquals(1, t.length);

    t = readStatementsInline("\nselect\n*\nfrom\nt\n");
    assertEquals(1, t.length);

    t = readStatementsInline("  \n  select\n*\nfrom\nt\n;");
    assertEquals(1, t.length);
    assertEquals(2, t[0].getLine());
    assertEquals(3, t[0].getCol());
  }

//  @Test
//  public void testTwoStatements() throws IOException, InvalidSQLScriptException {
//    ScriptSQLStatement[] t;
//
//    t = readStatementsInline("\n ;\n \ta ; \n --;\n b \t--;");
//
//    for (ScriptSQLStatement s : t) {
//      System.out.println("sql(line " + s.getLine() + ", col " + s.getCol() + "): '" + s.getSql() + "'");
//    }
//
//    assertEquals(2, t.length);
//
//    assertEquals(3, t[0].getLine());
//    assertEquals(3, t[0].getCol());
//    assertEquals("a", t[0].getSql());
//
//    assertEquals(4, t[1].getLine());
//    assertEquals(2, t[1].getCol());
//    assertEquals("b", t[1].getSql());
//  }

  @Test
  public void testGeneralQuotes() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    // Simple

    t = readStatementsInline("a'b;'");
    assertEquals(1, t.length);

    t = readStatementsInline("''");
    assertEquals(1, t.length);

    t = readStatementsInline("'a''b'");
    assertEquals(1, t.length);

    t = readStatementsInline("a\"b;\"");
    assertEquals(1, t.length);

    t = readStatementsInline("\"\"");
    assertEquals(1, t.length);
  }

  @Test
  public void testOracleQuotes() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline("an'x;'b");
    assertEquals(1, t.length);

    t = readStatementsInline("an'x;'''b");
    assertEquals(1, t.length);

    t = readStatementsInline("aN'x;'b");
    assertEquals(1, t.length);

    t = readStatementsInline("aN'x;'''b");
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

  private void tryOracleCombo(final String prefix, final char open, final char close)
      throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t = readStatementsInline("a" + prefix + "'" + open + "x;'c" + close + "'b;\nh");
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

//  @Test
//  public void testPostgreSQLQuotes() throws IOException, InvalidSQLScriptException {
//    ScriptSQLStatement[] t;
//
//    t = readStatementsInline("ae'x;\\'f'b;\nh");
//    assertEquals(2, t.length);
//
//    t = readStatementsInline("aE'x;\\'f'b;\nh");
//    assertEquals(2, t.length);
//
//    t = readStatementsInline("a$$x;'f$$b;\nh");
//    assertEquals(2, t.length);
//
//    t = readStatementsInline("a$tag1$x;'f$tag1$b;\nh");
//    assertEquals(2, t.length);
//
//    t = readStatementsInline("a$tag1$x;'f$tag2$b;\nh");
//    assertEquals(2, t.length);
//  }

  @Test
  public void testMariaDBQuotes() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline("a'x;\\'f\\\\'b;\nh");
    assertEquals(2, t.length);
  }

  @Test
  public void testSoloDelimiter() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsSolo("ago\nGo\nbgo");
    assertEquals(2, t.length);

    t = readStatementsSolo("ago\n\nbgo");
    assertEquals(1, t.length);

    t = readStatementsSolo("ago\n gO \ngob");
    assertEquals(2, t.length);
    assertEquals("ago", t[0].getSql());
    assertEquals("gob", t[1].getSql());

  }

  @Test
  public void testDirective() throws IOException, InvalidSQLScriptException {
    ScriptSQLStatement[] t;

    t = readStatementsInline("a;\n b \n ; \t \n \t --\t @delimiter \tgo  \tsolo\n c \n  go \t \n d\t\t");
    // print(t);
    assertEquals(4, t.length);

    t = readStatementsInline("a;\nb;\n-- @delimiter go solo\nc\ngo\nd\ngo\n-- @delimiter ;\ne;\nf");
    // print(t);
    assertEquals(6, t.length);

  }

  @Test
  public void testNoTrailingContentAfterDelimiter() throws IOException {
    try {
      ScriptSQLStatement[] t = readStatementsInline("a;b");
      fail("Trailing content should be rejected.");
    } catch (InvalidSQLScriptException e) {
      // OK
    }
  }

  // Utils

  private ScriptSQLStatement[] readStatementsInline(final String s) throws IOException, InvalidSQLScriptException {
    StringReader r = new StringReader(s);

    List<ScriptSQLStatement> sts = new ArrayList<>();
    ScriptSQLStatement st = null;
    Delimiter del = new Delimiter(";", false, false);
    SQLScriptParser sqlParser = new SQLScriptParser(r, del, new TestFeedback());
    while ((st = sqlParser.readStatement()) != null) {
      sts.add(st);
    }
    return sts.toArray(new ScriptSQLStatement[0]);
  }

  private ScriptSQLStatement[] readStatementsSolo(final String s) throws IOException, InvalidSQLScriptException {
    StringReader r = new StringReader(s);

    List<ScriptSQLStatement> sts = new ArrayList<>();
    ScriptSQLStatement st = null;
    Delimiter del = new Delimiter("go", false, true);
    SQLScriptParser sqlParser = new SQLScriptParser(r, del, new TestFeedback());
    while ((st = sqlParser.readStatement()) != null) {
      sts.add(st);
    }
    return sts.toArray(new ScriptSQLStatement[0]);
  }

  @SuppressWarnings("unused")
  private void print(final ScriptSQLStatement[] t) {
    System.out.println("t.length=" + t.length);
    for (ScriptSQLStatement s : t) {
      System.out.println(" - " + s);
    }
  }

  public static class TestFeedback implements Feedback {

    @Override
    public void info(final String line) {
      System.out.println("[INFO] " + line);
    }

    @Override
    public void warn(final String line) {
      System.out.println("[WARN] " + line);
    }

    @Override
    public void error(final String line) {
      System.out.println("[ERROR] " + line);
    }

  }

}
