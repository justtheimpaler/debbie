# Debbie

Debbie prepares a database schema using SQL files, to bring it to a known baseline state.

### Table of Contents

- [Features](#features)
- [Example](#example)
- [Commands](#commands)
- [Error Handling](#error-handling)
- [Versioning](#versioning)
- [Data Scenarios](#data-scenarios)
- [SQL Delimiters](#sql-delimiters)
- [Programatic Use](#programatic-use)
- [Pending Features](#pending-features)
- [Appendix 1 - Configuration Properties](#appendix-1---configuration-properties)
- [Appendix 2 - Maven Example](#appendix-2---maven-example)
- [Appendix 3 - Ant Example](#appendix-3---ant-example)


## Features

Debbie:

- Can be used from Maven and Ant (or any IDE that uses these tools).
- Can follow a source folder structure that matches an evolving schema over the life of the project.
- Supports multiple data scenarios to reset the database to a baseline data scenario that could be required for testing or debugging purposes.
- Handles stored procedures gracefully.
- Supports project-wide configuration and also team member-specific configuration.
- Embraces separate databases model (one per team member), as well as unique centralized database model.
- Can be used in headless mode to prepare an automated environment for testing or when building the application.
- Supports Oracle, DB2, PostgreSQL, SQL Server, MariaDB, MySQL, SAP ASE, H2, HyperSQL, Derby. Other databases should be supported through JDBC, but are not tested.


## Example

In the simplest form Debbie can prepare a database schema from a SQL source file. If we have the following folder structure in the project:

```
src/
  database/
    1.0.0/
      build.sql
      clean.sql
```

Then we can run the `build.sql` file with Maven using:

```bash
mvn debbie:build
```

We can also run the `clean.sql` file with Maven using:

```bash
mvn debbie:clean
```

These commands will connect to the database and run the SQL statements. The location of the base source folder (`src/database`), the target version (`1.0.0`), and the database connection details are specified in Debbie's configuration.

It's up to the developer to decide which SQL statements to include in the `build.sql` and `clean.sql` files. Debbie assumes the former will populate
the database and the latter will "undo" the database changes. As an example, the `build.sql` file could include:

```sql
create table employee (
  id number(9) primary key not null,
  name varchar2(20)
);

insert into employee (id, name) values (45, 'Anne');
insert into employee (id, name) values (123, 'Alice');
insert into employee (id, name) values (6097, 'Steve');
```

and the `clean.sql` file could include:

```sql
drop table employee;
```


## Commands

Debbie implements three commands:

- `build`: to populate the database schema.
- `clean`: to clean the database schema.
- `rebuild`: to clean and build the database schema in one command.

When used in Maven they take the form: `mvn debbie:build`, `mvn debbie:clean`, or `mvn debbie:rebuild` respectively.

There are also three corresponding Ant tasks for each one. See example in Appendix.


## Error Handling

By default, errors during the *build* sequence are considered fatal and Debbie will stop the execution. Errors during the *clean* sequence are by default considered
non-fatal; Debbie will display them but will continue the execution normally. In practice a clean sequence can be run multiple times in a row. This can be useful if the developer considers the database objects dependencies will sort themselves out if `clean` is run enough times in a row.

Warnings messages are displayed by default. They can be useful in some databases like Sybase (SAP ASE), where the execution plan of a query is displayed in the warnings output stream. In other databases (such as DB2) they can be quite serious, and maybe should be treated as errors.

The handling of errors in the build and clean sequences as well as the treatment of warnings can be changed through the configuration properties.


## Versioning

This is an optional feature that can be useful to some projects.

Debbie supports the evolution of a database over time with the use of separate folders for each version. On the flip side, if no versioning is needed the entire database source code can be stored in a single folder called `1.0.0` (or similar).

When building the database, Debbie will run in sequence all `build.sql` files in the source code until the specified version. When cleaning the database, Debbie will
execute all `clean.sql` files from the target version down to the initial version, in reverse order. For example, if the database source
folder in the project is set up as `src/database`, then the database source structure could look like:

```
src/             when building...
  database/           v                       +----+
    1.0.0/            |                       |    |
      build.sql     <-+                     <-+    |
      clean.sql       |  <-+                  |  <-+
    1.0.2/            |    |                  |    |
      build.sql     <-+    |                <-+    |
      clean.sql       |  <-+                  |  <-+
    1.1.0/            |    |                  |    |
      build.sql     <-+    |                <-+    |
      clean.sql          <-+                     <-+
    1.2.0/                 |                       |
      build.sql            ^                       ^
      clean.sql       when cleaning...    when rebuilding...
```

If we assume the target version (specified in the config) is `1.1.0` then a `debbie:build` command will execute the first three `build.sql` files in
ascending order and will ignore the last one (corresponding to version 1.2.0). A `debbie:clean` command would run the corresponding
`clean.sql` files (the first three) in reverse ordering. A `debbie:rebuild` command will execute the full clean sequence 
followed by the full build sequence.

By default SQL scripts are considered "layered"; this means that each version assumes the previous SQL scripts were already run
in the database. Debbie also supports "non-layered" builds, where each `build.sql` file includes the entire schema; in this case
Debbie won't run previous SQL scripts, but only the specified version. This is set up in the configuration properties.


## Data Scenarios

This is an optional feature that can be useful to some projects.

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

In this case only one data scenario (either `bug-1732`, `feature-188`, or `official-qa`) can be selected for a build execution. The corresponding `build-data.sql` SQL script will be run on top of the standard `build.sql` file.


## SQL Delimiters

The example shown below switches the delimiter from `;` (that can be present in the same line as the SQL statement) to
`//` that must show up in a separate line (`solo` modifier). The former is suitable for most SQL statements, while the latter
is useful for code blocks, typically used in stored procedures and function definitions.

The example shown below (that runs in Oracle) switches back and forth between these delimiters. The default delimiter is `;` and, therefore, the first `create table ...` statement will be processed correctly since it ends with it.


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

There is also a `caseinsensitive` modifier. It can be useful for delimiter sequences such as "Go" that can also be typed as "go" or "GO".


## Programatic Use

Debbie can also be called programatically from any JVM application, in a similar manner as the Maven and Ant plugins are implemented.

Debbie's API is not yet documented.


## Pending Features

Multi-line comments (such as `/* multi-line comment */`) are not supported. Only single-line comments (that start with `--`) are supported.


## Appendix 1 - Configuration Properties

The basic properties are typically set up in the `pom.xml` file (or Ant script) and represent the project-wide set up. These properties can be superseded or complemented by a local properties file that each team member can tweak separately.

The full list of configuration properties is shown below:

| Property                 | Description  | Default Value |
| - | - | - |
| sourcedir                | The base folder for the database source | -- |
| targetversion            | The version that Debbie should produce | -- |
| datascenario             | An optional data scenario to produce | -- |
| layeredbuild             | Is the build layered? | true |
| layeredscenario          | Is the scenario layered? | false |
| onbuilderror             | On build error: stop or continue | stop |
| oncleanerror             | On clean error: stop or continue | continue |
| localproperties          | Location of the local properties, if any | -- |
| delimiter                | The initial SQL block delimiter | ; |
| solodelimiter            | Is the block delimiter alone in its line? | false |
| casesensitivedelimiter   | Is the block delimiter case sensitive? | false |
| treatwarningas           | Treat warnings as: ignore, info, warn, error | warn |
| jdbcdriverclass          | The JDBC driver class name | -- |
| jdbcurl                  | The JDBC URL | -- |
| jdbcusername             | The JDBC username | -- |
| jdbcpassword             | The JDBC passwod | -- |


## Appendix 2 - Maven Example

A typical Maven configuration section could look like:

```xml
  <plugin>
    <groupId>org.nocrala.tools.database.debbie</groupId>
    <artifactId>debbie-maven-plugin</artifactId>
    <version>1.3.2</version>
    <configuration>
      <sourcedir>src/database</sourcedir>
      <targetversion>1.0.0</targetversion>
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

If we don't want to type the database credentials in the `pom.xml` file (as in this case) we can place them in
the `debbie-local.properties` file, that could look like:

```properties
jdbcdriverclass=org.postgresql.Driver
jdbcurl=jdbc:postgresql://192.168.56.213:5432/mypostgresql
jdbcusername=myusername
jdbcpassword=mypassword
```

Alternatively, a full configuration with all properties in the Maven `pom.xml` file could look like:

```xml
  <plugin>
    <groupId>org.nocrala.tools.database.debbie</groupId>
    <artifactId>debbie-maven-plugin</artifactId>
    <version>1.3.2</version>
    <configuration>
      <sourcedir>src/database</sourcedir>
      <targetversion>1.15.3</targetversion>
      <datascenario>feature-188</datascenario>
      <layeredbuild>true</layeredbuild>
      <layeredscenario>false</layeredscenario>
      <onbuilderror>stop</onbuilderror>
      <oncleanerror>continue</oncleanerror>
      <localproperties>src/database/debbie-local.properties</localproperties>
      <delimiter>;</delimiter>
      <solodelimiter>true</solodelimiter>
      <casesensitivedelimiter>false</casesensitivedelimiter>
      <treatwarningas>warn</treatwarningas>
      <jdbcdriverclass>org.postgresql.Driver</jdbcdriverclass>
      <jdbcurl>jdbc:postgresql://192.168.56.213:5432/mypostgresql?currentSchema=db1</jdbcurl>
      <jdbcusername>myusername</jdbcusername>
      <jdbcpassword>mypassword</jdbcpassword>
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

Anyway, any property present in the `src/database/debbie-local.properties` file will override the Maven configuration.


## Appendix 3 - Ant Example

The following example show Debbie's three taks defined as Ant tasks. They can be called as:

```bash
ant debbie-build
```

or

```bash
ant debbie-clean
```

or

```bash
ant debbie-rebuild
```

These Ant tasks can be defined in the `build.xml` file as shown below (example):

```xml
  <target name="debbie-build">
    <taskdef name="debbie-build-task" classname="org.nocrala.tools.debbie.ant.BuildAntTask">
      <classpath>
        <pathelement location="lib/debbie-ant-plugin-1.3.2-jar-with-dependencies.jar" />
        <pathelement location="lib/postgresql-42.2.5.jre6.jar" />
      </classpath>
    </taskdef>
    <debbie-build-task sourcedir="src/database"
                    targetversion="1.1.0"
                    datascenario="dev1"
                    layeredbuild="true"
                    layeredscenario="false"
                    onbuilderror="stop"
                    oncleanerror="continue"
                    delimiter=";"
                    solodelimiter="false"
                    casesensitivedelimiter="false"
                    treatwarningas="error"
                    jdbcdriverclass="org.postgresql.Driver"
                    jdbcurl="jdbc:postgresql://10.102.50.171:5432/postgresql"
                    jdbcusername="myuser"
                    jdbcpassword="mypass" />
  </target>

  <target name="debbie-clean">
    <taskdef name="debbie-clean-task" classname="org.nocrala.tools.debbie.ant.CleanAntTask">
      <classpath>
        <pathelement location="lib/debbie-ant-plugin-1.3.2-jar-with-dependencies.jar" />
        <pathelement location="lib/postgresql-42.2.5.jre6.jar" />
      </classpath>
    </taskdef>
    <debbie-clean-task sourcedir="src/database"
                    targetversion="1.1.0"
                    datascenario="dev1"
                    layeredbuild="true"
                    layeredscenario="false"
                    onbuilderror="stop"
                    oncleanerror="continue"
                    localproperties="local.properties"
                    delimiter=";"
                    solodelimiter="false"
                    casesensitivedelimiter="false"
                    treatwarningas="error"
                    jdbcdriverclass="org.postgresql.Driver"
                    jdbcurl="jdbc:postgresql://10.102.50.171:5432/postgresql"
                    jdbcusername="myuser"
                    jdbcpassword="mypass" />
  </target>

  <target name="debbie-rebuild">
    <taskdef name="debbie-rebuild-task" classname="org.nocrala.tools.debbie.ant.RebuildAntTask">
      <classpath>
        <pathelement location="lib/debbie-ant-plugin-1.3.2-jar-with-dependencies.jar" />
        <pathelement location="lib/postgresql-42.2.5.jre6.jar" />
      </classpath>
    </taskdef>
    <debbie-rebuild-task sourcedir="src/database"
                      targetversion="1.0.2"
                      datascenario="dev1"
                      layeredbuild="true"
                      layeredscenario="false"
                      onbuilderror="stop"
                      oncleanerror="continue"
                      delimiter=";"
                      solodelimiter="false"
                      casesensitivedelimiter="false"
                      treatwarningas="error"
                      jdbcdriverclass="org.postgresql.Driver"
                      jdbcurl="jdbc:postgresql://10.102.50.171:5432/postgresql"
                      jdbcusername="myuser"
                      jdbcpassword="mypass"
                      localproperties="local.properties" />
  </target>
```






