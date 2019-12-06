package org.nocrala.tools.database.db;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "build", defaultPhase = LifecyclePhase.COMPILE) // Goal name set to "build"
public class BuildMojo extends AbstractMojo {

  @Parameter(property = "db-dir")
  private String dbdir;

  @Parameter(property = "scenario")
  private String scenario;

  public void execute() throws MojoExecutionException {
    getLog().info("Build Mojo: dbdir=" + this.dbdir + " scenario=" + this.scenario);
  }

}