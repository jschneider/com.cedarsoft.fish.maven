package com.cedarsoft.fish.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.InvalidPluginException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.PluginManagerException;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.version.PluginVersionNotFoundException;
import org.apache.maven.plugin.version.PluginVersionResolutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.DefaultProjectBuilderConfiguration;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;

import java.util.List;

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

  /**
   * The project builder instance used to retrieve the super-project instance
   * in the event there is no current MavenProject instance. Some MavenProject
   * instance has to be present to use in the plugin manager APIs.
   */
  @Component(role = MavenProjectBuilder.class)
  private MavenProjectBuilder projectBuilder;

  /**
   * The current project, if there is one. This is listed as optional, since
   * the help plugin should be able to function on its own. If this
   * parameter is empty at execution time, this Mojo will instead use the
   * super-project.
   */
  @Component
  private MavenProject project;

  /**
   * The current build session instance. This is used for
   * plugin manager API calls.
   */
  @Component
  private MavenSession session;

  /**
   * The local repository ArtifactRepository instance. This is used
   * for plugin manager API calls.
   */
  @org.apache.maven.plugins.annotations.Parameter(defaultValue = "${localRepository}", required = true, readonly = true)
  private ArtifactRepository localRepository;

  /**
   * Remote repositories used for the project.
   *
   * @since 2.1
   */
  @org.apache.maven.plugins.annotations.Parameter(defaultValue = "${project.remoteArtifactRepositories}",
    required = true, readonly = true)
  private List<ArtifactRepository> remoteRepositories;

  @Component
  private ArtifactFactory artifactFactory;

  //@Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    getLog().info("Executing...");

    try {
      DefaultProjectBuilderConfiguration configuration = new DefaultProjectBuilderConfiguration();
      configuration.setLocalRepository(localRepository);

      project = projectBuilder.buildStandaloneSuperProject(configuration);

      System.out.println("Project " + project);


      Artifact artifact = artifactFactory.createPluginArtifact(
        "net.sourceforge.cobertura"
        , "cobertura",
        VersionRange.createFromVersion("2.1.1"));

      System.out.println("Articact " + artifact);
      System.out.println("available versions " + artifact.getAvailableVersions());


      Plugin plugin = new Plugin();
      plugin.setGroupId("net.sourceforge.cobertura");
      plugin.setArtifactId("cobertura");
      plugin.setVersion("2.1.1");

      plugin.setGroupId("com.cedarsoft.fish");
      plugin.setArtifactId("maven");
      plugin.setVersion("1.0.0-SNAPSHOT");

      //Old plugin?
      plugin.setGroupId("maven-plugins");
      plugin.setArtifactId("maven-cobertura-plugin");
      plugin.setVersion("1.4");

      plugin.setGroupId("org.apache.maven.plugins");
      plugin.setArtifactId("maven-compiler-plugin");
      plugin.setVersion("3.3");



      PluginDescriptor pluginDescriptor = pluginManager.loadPluginDescriptor(plugin, project, session);
      System.out.println("############");
      System.out.println("pluginDescriptor = " + pluginDescriptor);
      System.out.println("description = " + pluginDescriptor.getDescription());

      List<MojoDescriptor> mojos = pluginDescriptor.getMojos();
      for (MojoDescriptor mojo : mojos) {
        System.out.println("-------------");
        System.out.println("goal " + mojo.getGoal());
        System.out.println("full goal name = " + mojo.getFullGoalName());
        System.out.println("description: " + mojo.getDescription());
      }


      //pluginManager.getPluginDescriptorForPrefix()


    } catch (ProjectBuildingException | PluginManagerException | ArtifactResolutionException | PluginNotFoundException | PluginVersionNotFoundException | InvalidPluginException | ArtifactNotFoundException | PluginVersionResolutionException | InvalidVersionSpecificationException e) {
      throw new RuntimeException(e);
    }
  }
}
