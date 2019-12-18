package org.nocrala.tools.database.db.executor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.stream.Collectors;

import org.nocrala.tools.database.db.parser.SQLScriptParser;
import org.nocrala.tools.database.db.parser.ScriptSQLStatement;
import org.nocrala.tools.database.db.utils.EUtil;
import org.nocrala.tools.database.db.utils.StreamUtil;

public class SQLExecutor {

  public static enum TreatWarningAs {
    IGNORE, INFO, WARN, ERROR
  }

  private static final String JDBC_DRIVER_CLASS_PROP = "jdbc.driverclass";
  private static final String JDBC_URL_PROP = "jdbc.url";
  private static final String JDBC_USERNAME_PROP = "jdbc.username";
  private static final String JDBC_PASSWORD_PROP = "jdbc.password";

  private String jdbcDriverClass;
  private String jdbcURL;
  private String jdbcUsername;
  private String jdbcPassword;

  private Connection conn;
  private Statement stmt;

  private Feedback feedback;
  private Delimiter delimiter;
  private TreatWarningAs treatWarningAs;

  public SQLExecutor(final File localdatabaseproperties, final Feedback feedback, final Delimiter delimiter,
      final TreatWarningAs treatWarningAs) throws InvalidPropertiesFileException, CouldNotConnectToDatabaseException {

    this.feedback = feedback;
    this.delimiter = delimiter;
    this.treatWarningAs = treatWarningAs;

    Properties props = new Properties();
    try {
      props.load(new FileReader(localdatabaseproperties));
    } catch (FileNotFoundException e) {
      this.feedback.error("Local properties file not found (" + localdatabaseproperties + "): " + e.getMessage());
      throw new InvalidPropertiesFileException("File not found: " + localdatabaseproperties, e);
    } catch (IOException e) {
      this.feedback.error("Could not read local properties file (" + localdatabaseproperties + "): " + e.getMessage());
      throw new InvalidPropertiesFileException("Could not read file: " + localdatabaseproperties, e);
    }

    this.jdbcDriverClass = props.getProperty(JDBC_DRIVER_CLASS_PROP);
    this.jdbcURL = props.getProperty(JDBC_URL_PROP);
    this.jdbcUsername = props.getProperty(JDBC_USERNAME_PROP);
    this.jdbcPassword = props.getProperty(JDBC_PASSWORD_PROP);

    if (empty(this.jdbcDriverClass)) {
      String msg = "Mandatory property (" + JDBC_DRIVER_CLASS_PROP + ") is not specified in the properties file: "
          + localdatabaseproperties;
      this.feedback.error(msg);
      throw new InvalidPropertiesFileException(msg, null);
    }

    if (empty(this.jdbcURL)) {
      String msg = "Mandatory property (" + JDBC_URL_PROP + ") is not specified in the properties file: "
          + localdatabaseproperties;
      this.feedback.error(msg);
      throw new InvalidPropertiesFileException(msg, null);
    }

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

  public void run(final File f, final boolean onErrorContinue)
      throws CouldNotReadSQLScriptException, SQLScriptAbortedException {
    SQLStats stats = new SQLStats();
    ScriptSQLStatement st = null;
    try (Reader r = new FileReader(f)) {
      SQLScriptParser sqlParser = new SQLScriptParser(r, this.delimiter, this.feedback);
      while ((st = sqlParser.readStatement()) != null) {
        try {
          this.stmt.execute(st.getSql());
          SQLWarning w = this.stmt.getWarnings();
          if (w != null) {
            String warnings = StreamUtil.asStream(new SQLWarningIterator(w)) //
                .map(x -> renderSQLWarning(x)) //
                .collect(Collectors.joining("\n"));
            switch (this.treatWarningAs) {
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
              handleError(f, onErrorContinue, stats, st, warnings, null);
              break;
            default: // Ignore
              stats.addSuccessful();
            }
          } else {
            stats.addSuccessful();
          }

        } catch (SQLException e) {
          handleError(f, onErrorContinue, stats, st, null, e);
        }
      }
      this.feedback.info("> " + f.getPath() + " -- " + stats.render());
    } catch (IOException e) {
      this.feedback.error("Could not read SQL script (" + f.getPath() + "): " + EUtil.renderException(e));
      throw new CouldNotReadSQLScriptException("Could not read SQL script: " + f, e);
    }
  }

  private void handleError(final File f, final boolean onErrorContinue, final SQLStats stats,
      final ScriptSQLStatement st, final String warnings, final SQLException e) throws SQLScriptAbortedException {
    stats.addFailed();
    String msg = "Failed to execute statement (" + f.getPath() + ":" + st.getLine() + "):\n" + st.getSql()
        + (e == null ? "" : "\n" + e.getMessage()) + (warnings == null ? "" : ("\n" + warnings));
    this.feedback.error(msg);

    if (onErrorContinue) {
      this.feedback.info("-- continuing... ");
      this.feedback.info("");
    } else {
      stats.setFailedSQLStatement(st);
      this.feedback.info("> " + f.getPath() + " -- " + stats.render());
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
      Class.forName(this.jdbcDriverClass);
    } catch (ClassNotFoundException e) {
      throw new SQLException("JDBC driver class not found: " + this.jdbcDriverClass);
    }
    this.conn = DriverManager.getConnection(this.jdbcURL, this.jdbcUsername, this.jdbcPassword);
  }

  public static class JDBCInitializationException extends Exception {

    private static final long serialVersionUID = 1L;

    public JDBCInitializationException(final String message) {
      super(message);
    }

  }

  // Utils

  private boolean empty(final String s) {
    return s == null || s.trim().isEmpty();
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

}
