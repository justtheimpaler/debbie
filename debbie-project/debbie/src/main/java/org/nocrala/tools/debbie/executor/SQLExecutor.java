package org.nocrala.tools.debbie.executor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.nocrala.tools.debbie.Configuration;
import org.nocrala.tools.debbie.Configuration.OnError;
import org.nocrala.tools.debbie.parser.SQLScriptParser;
import org.nocrala.tools.debbie.parser.ScriptSQLStatement;
import org.nocrala.tools.debbie.parser.SQLScriptParser.InvalidSQLScriptException;
import org.nocrala.tools.debbie.utils.EUtil;
import org.nocrala.tools.debbie.utils.FUtil;
import org.nocrala.tools.debbie.utils.StreamUtil;

public class SQLExecutor implements AutoCloseable {

  private static final String PROMPT = "sql>";

  public static enum TreatWarningAs {
    IGNORE, INFO, WARN, ERROR
  }

  private Connection conn;
  private Statement stmt;

  private Configuration config;
  private Feedback feedback;

  public SQLExecutor(final Configuration config, final Feedback feedback)
      throws InvalidPropertiesFileException, CouldNotConnectToDatabaseException {

    this.config = config;
    this.feedback = feedback;

    this.conn = null;
    this.stmt = null;
    try {
      connect();
      this.stmt = this.conn.createStatement();
    } catch (SQLException e) {
      this.feedback.error("Could not connect to database: " + EUtil.renderException(e));
      throw new CouldNotConnectToDatabaseException("Could not connect to database", e);
    }

  }

  // typical delimiters: ; // go (separated line)

  public void run(final File f, final File basedir, final OnError onError)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    SQLStats stats = new SQLStats();
    ScriptSQLStatement st = null;
    try (Reader r = new FileReader(f)) {
      SQLScriptParser sqlParser = new SQLScriptParser(r, this.config.getDelimiter(), this.feedback);
      while ((st = sqlParser.readStatement()) != null) {
        try {
          this.stmt.execute(st.getSql());
          SQLWarning w = this.stmt.getWarnings();
          if (w != null) {
            String warnings = StreamUtil.asStream(new SQLWarningIterator(w)) //
                .map(x -> renderSQLWarning(x)) //
                .collect(Collectors.joining("\n"));
            switch (this.config.getTreatWarningAs()) {
            case INFO:
              stats.addSuccessful();
              this.feedback.info(warnings);
              break;
            case WARN:
              stats.addSuccessful();
              this.feedback.warn("Statement produced SQL warnings (" + f.getPath() + ":" + st.getLine() + "):\n"
                  + st.getSql() + "\n" + warnings);
              break;
            case ERROR:
              handleError(f, basedir, onError, stats, st, warnings, null);
              break;
            default: // Ignore
              stats.addSuccessful();
            }
          } else {
            stats.addSuccessful();
          }

        } catch (SQLException e) {
          handleError(f, basedir, onError, stats, st, null, e);
        }
      }
      String relPath = FUtil.relativize(basedir, f).getPath();
      this.feedback.info(PROMPT + " " + relPath + " -- " + stats.render());
    } catch (IOException e) {
      this.feedback.error("Could not read SQL script " + f.getPath() + ": " + EUtil.renderException(e));
      throw new CouldNotReadSQLScriptException("Could not read SQL script: " + f, e);
    } catch (InvalidSQLScriptException e) {
      this.feedback.error(
          "Invalid SQL script " + f.getPath() + " (line " + e.getLineNumber() + "): " + EUtil.renderException(e));
      throw new SQLScriptAbortedException("Invalid SQL script: " + f, e);
    }
  }

  private void handleError(final File f, final File basedir, final OnError onError, final SQLStats stats,
      final ScriptSQLStatement st, final String warnings, final SQLException e) throws SQLScriptAbortedException {
    stats.addFailed();
    String msg = "Failed to execute statement (" + f.getPath() + ":" + st.getLine() + "):\n" + st.getSql()
        + (e == null ? "" : "\n" + e.getMessage()) + (warnings == null ? "" : ("\n" + warnings));
    this.feedback.error(msg);

    if (onError == OnError.CONTINUE) {
      this.feedback.info("-- continuing... ");
      this.feedback.info("");
    } else {
      stats.setFailedSQLStatement(st);
      String relPath = FUtil.relativize(basedir, f).getPath();
      this.feedback.info(PROMPT + " " + relPath + " -- " + stats.render());
      throw new SQLScriptAbortedException(msg, e);
    }
  }

  private String renderSQLWarning(final SQLWarning w) {
    StringBuilder sb = new StringBuilder("");
    if (w.getMessage() != null) {
      sb.append(w.getMessage() + ", ");
    }
    if (w.getSQLState() != null) {
      sb.append("SQLState: " + w.getSQLState() + ", ");
    }
    sb.append("Error Code: " + w.getErrorCode());
    if (w.getCause() != null) {
      sb.append("\nException: " + EUtil.renderException(w.getCause()));
    }
    return sb.toString();
  }

  private void connect() throws SQLException {
    try {
      Class.forName(this.config.getJdbcDriverClass());
    } catch (ClassNotFoundException e) {
      throw new SQLException("JDBC driver class not found: " + this.config.getJdbcDriverClass());
    }
    this.conn = DriverManager.getConnection(this.config.getJdbcURL(), this.config.getJdbcUsername(),
        this.config.getJdbcPassword());
  }

  public static class JDBCInitializationException extends Exception {

    private static final long serialVersionUID = 1L;

    public JDBCInitializationException(final String message) {
      super(message);
    }

  }

  // Iterator

  public static class SQLWarningIterator implements Iterator<SQLWarning> {

    private SQLWarning warning;

    public SQLWarningIterator(final SQLWarning warning) {
      this.warning = warning;
    }

    @Override
    public boolean hasNext() {
      return this.warning != null;
    }

    @Override
    public SQLWarning next() {
      SQLWarning w = this.warning;
      this.warning = this.warning.getNextWarning();
      return w;
    }

  }

  // Exceptions

  public static class InvalidPropertiesFileException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidPropertiesFileException(final String message, final Throwable cause) {
      super(message, cause);
    }

  }

  public static class CouldNotConnectToDatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    public CouldNotConnectToDatabaseException(final String message, final Throwable cause) {
      super(message, cause);
    }

  }

  public static class CouldNotReadSQLScriptException extends Exception {

    private static final long serialVersionUID = 1L;

    public CouldNotReadSQLScriptException(final String message, final Throwable cause) {
      super(message, cause);
    }

  }

  public static class SQLScriptAbortedException extends Exception {

    private static final long serialVersionUID = 1L;

    public SQLScriptAbortedException(final String message, final Throwable cause) {
      super(message, cause);
    }

  }

  @Override
  public void close() throws Exception {
    if (this.conn != null) {
      this.conn.close();
    }
  }

}
