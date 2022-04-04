# Debbie

Debbie prepares database schemas from source SQL files stored in the repository project.


## Features

Debbie:

- Can be used from Maven and Ant and any IDE that uses these tools as well.
- Follows a strict source folder versioning structure that matches an evolving schema over the life of the application.
- Supports multiple data scenarios to reset the database to any baseline data required for testing or debugging.
- Handles stored procedures as well as common SQL.
- Supports project-wide configuration and also team member-specific configuration.
- Embraces separate databases model (one per team member), as well as unique centralized database model.
- Can be used in a headless mode in an automated environment for testing or building the application.
- Supports at least Oracle, DB2, PostgrSQL, SQL Server, MariaDB, MySQL, SAP ASE, H2, HyperSQL, Derby.


## Example

In the simplest form Debbie can prepare a database schema from a SQL source file using:

```bash
mvn debbie:build
```

This command will connect to the sandbox database and run the SQL statements in the `build.sql` located in the source folder. 
The connection details and location of the source folder are specified in the `pom.xml` file (or Ant properties). They can be 
set up locally as well if these values differ from the project-wide configuration.


## Commands

Debbie implements three commands:

- `mvn debbie:build`: to populate the database schema.
- `mvn debbie:clean`: to clean the database schema.
- `mvn debbie:rebuild`: to clean and build the database schema.


## Versioning

When building the database, Debbie will run all `build.sql` files in the source code until the specified version. When cleaning the database, Debbie will
execute all `clean.sql` files from the target version down to the initial version, in reverse order. For example, if the source
folder is set up as `src/database`, then the database source structure could look like:

```
src/
  database/
    1.0.0/
      build.sql
      clean.sql
    1.0.2/
      build.sql
      clean.sql
    1.1.0/
      build.sql
      clean.sql
    1.2.0/
      build.sql
      clean.sql
```

If we assume the target version (specified in the config) is `1.1.0` then a `debbie:build` command will execute the first three `build.sql` files in
ascending sequence and will ignore the last one (corresponding to version 1.2.0). A `debbie:clean` command would run the corresponding
`clean.sql` files (the first three) in reverse ordering.

By default SQL scripts are considered "layered"; this means that each version assumes the previous SQL scripts were already run 
in the database. Debbie also supports "non-layered" builds, where each SQL `build.sql` file includes the entire schema; in this case 
Debbie won't run previous SQL scripts, but only the specified version. This is set up in the configuration properties.


## Error Control

By default, errors during the *build* sequence are considered fatal and Debbie will stop the execution. Errors during the *clean* sequence are by default not considered
fatal and Debbie will display them, but will continue the execution. In practice a clean sequence can be run multiple times in a row, if the developer
considers the errors will sort themselves out.

Warnings are never considered fatal and are displayed (by default). They can be useful in some databases like Sybase (SAP ASE), where the execution
plan of a query is displayed in the warnings output stream. 

The behavior of the build and clean sequences, as well as the treatment of warnings, can be changed in the configuration properties.


## Data Scenarios

Each version folder (like `1.0.2` in the example above) can optionally include a `scenarios` subfolder that can in turn include one or more
data scenarios. Only one of these scenarios can be chosen to complement the build sequence; it's selected with the `datascenario` property.

For example, the directory for one of the versions could look like:

```
    1.0.2/
      scenarios/
        bug-1732/
          build-data.sql
          clean-data.sql
        feature-188/
          build-data.sql
          clean-data.sql
        official-qa/
          build-data.sql
          clean-data.sql
      build.sql
      clean.sql
```

In this case only one data scenario (either `bug-1732`, `feature-188`, or `official-qa`) can be selected for a build execution.

## Configuration properties

The basic properties are typically set up in the `pom.xml` file (or Ant script) and represent the project-wide set up. These properties
can be superseded or complemented by local properties that each team member can tweak separately.  

A typical Maven configuration could look like:

```xml
          <plugin>
            <groupId>org.nocrala.tools.database.debbie</groupId>
            <artifactId>debbie-maven-plugin</artifactId>
            <version>1.3.0</version>
            <configuration>
              <sourcedir>src/database</sourcedir>
              <targetversion>1.2.3</targetversion>
              <datascenario>feature-188</datascenario>
              <layeredbuild>true</layeredbuild>
              <layeredscenario>false</layeredscenario>
              <onbuilderror>stop</onbuilderror>
              <oncleanerror>continue</oncleanerror>
              <localproperties>src/database/debbie-local.properties</localproperties>
            </configuration>
            <dependencies>
              <dependency>
                <groupId>org.postgresql</groupId>
                <artifactId>postgresql</artifactId>
                <version>42.2.5.jre6</version>
              </dependency>
            </dependencies>
          </plugin>
```

The full list of configuration properties is shown below:

| Property                 | Description  | Default Value |
| - | - | - |
| sourcedir                | The base folder for the database source | -- |
| targetversion            | The version that Debbie should produce | -- |
| datascenario             | An optional data scenario to produce | -- |
| layeredbuild             | Is the build layered or non-layered? | true |
| layeredscenario          | Is the scenario layered or non-layered? | false |
| onbuilderror             | On build error: stop or continue | stop |
| oncleanerror             | On clean error: stop or continue | continue |
| localproperties          | Location of the local properties, if any | -- |
| delimiter                | SQL block delimiter | ; |
| solodelimiter            | Is block delimiter alone in its line? | false |
| casesensitivedelimiter   | Is block delimiter case sensitive? | false |
| treatwarningas           | Treat warnings as: ignore, info, warn, error | warn | 
| jdbcdriverclass          | The JDBC driver class name | -- |
| jdbcurl                  | The JDBC URL | -- |
| jdbcusername             | The JDBC username | -- |
| jdbcpassword             | The JDBC passwod | -- |


## Example of a Stored Procedure

The example shown below switches the delimiter from `;` (that can be present in the same line as the SQL statement) to
`\\` that must show up in a separate line (`solo` modifier). The former is suitable for most SQL statements, while the second one
is useful for code blocks, typically used in stored procedures and function definitions.

The example switches back and forth between these delimiters.


```sql

create table account (
  id number(12) primary key not null,
  acct varchar2(20) not null
);

-- @delimiter // solo

create procedure plustwo(a in number, b in out number)
as
begin
  b := a + 2;
end;
//

create function addtwo(n in number) return number
as
begin
  return n + 2;
end;
//

-- @delimiter ;

create sequence seq_account;

```


## Programatic Use

Debbie can also be called programatically from any JVM application, in a similar manner as the Maven and Ant plugins are implemented. 
Debbie's API is not document, though.


## Bugs and Pending Features

Multi-line comments (`/* comment */`) are not supported. Only (that start with `--`) comments are supported.





