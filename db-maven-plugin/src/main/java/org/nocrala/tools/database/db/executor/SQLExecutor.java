package org.nocrala.tools.database.db.executor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.nocrala.tools.database.db.parser.SQLScriptParser;
import org.nocrala.tools.database.db.parser.ScriptSQLStatement;
import org.nocrala.tools.database.db.utils.EUtil;

public class SQLExecutor {

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

  public SQLExecutor(final File localdatabaseproperties, final Feedback feedback, final Delimiter delimiter)
      throws InvalidPropertiesFileException, CouldNotConnectToDatabaseException {

    this.feedback = feedback;
    this.delimiter = delimiter;

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
      SQLScriptParser sqlParser = new SQLScriptParser(r, this.delimiter);
      while ((st = sqlParser.readStatement()) != null) {
        try {
          this.stmt.execute(st.getSql());
          stats.addSuccessful();
        } catch (SQLException e) {
          stats.addFailed();
          String msg = "Failed to execute statement (" + f + ":" + st.getLine() + "):\n" + st.getSql() + "\n"
              + e.getMessage();
          this.feedback.error(msg);
          if (onErrorContinue) {
            this.feedback.info("-- continuing... ");
            this.feedback.info("");
          } else {
            stats.setFailedSQLStatement(st);
            this.feedback.info("> " + f + " -- " + stats.render());
            throw new SQLScriptAbortedException(msg, e);
          }
        }
      }
      this.feedback.info("> " + f + " -- " + stats.render());
    } catch (IOException e) {
      this.feedback.error("Could not read SQL script (" + f + "): " + EUtil.renderException(e));
      throw new CouldNotReadSQLScriptException("Could not read SQL script: " + f, e);
    }
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
