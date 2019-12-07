package org.nocrala.tools.database.db;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "build", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class BuildMojo extends AbstractMojo {

  @Parameter()
  private String databasedir;

  @Parameter()
  private String version;

  @Parameter()
  private String scenario;

  @Parameter()
  private boolean layeredbuild = true;

  @Parameter()
  private boolean layeredscenarios = false;

  @Parameter()
  private boolean buildonerrorcontinue = false;

  @Parameter()
  private boolean cleanonerrorcontinue = true;

  @Parameter()
  private String localproperties = null;

  public void execute() throws MojoExecutionException {
    getLog().info("Build Mojo: dbdir=" + this.databasedir + " scenario=" + this.scenario);
  }

}