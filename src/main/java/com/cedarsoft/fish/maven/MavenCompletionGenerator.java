package com.cedarsoft.fish.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * @author Johannes Schneider (<a href="mailto:js@cedarsoft.com">js@cedarsoft.com</a>)
 */
@Mojo(name = "create", requiresProject = false, aggregator = true)
public class MavenCompletionGenerator extends AbstractMojo {
  /**
   * The Plugin manager instance used to resolve Plugin descriptors.
   */
  @Component(role = PluginManager.class)
  private PluginManager pluginManager;


  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Executing...");
  }
}
