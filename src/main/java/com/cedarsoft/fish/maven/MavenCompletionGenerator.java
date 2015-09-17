package com.cedarsoft.fish.maven;

import com.google.common.collect.ImmutableList;
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
import org.apache.maven.tools.plugin.generator.GeneratorUtils;

import java.util.ArrayList;
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


      addDefaultPlugins();
      addMojoHausPlugins();
      //plugin.setGroupId("net.sourceforge.cobertura");
      //plugin.setArtifactId("cobertura");
      //plugin.setVersion("2.1.1");
      //
      //plugin.setGroupId("com.cedarsoft.fish");
      //plugin.setArtifactId("maven");
      //plugin.setVersion("1.0.0-SNAPSHOT");
      //
      ////Old plugin?
      //plugin.setGroupId("maven-plugins");
      //plugin.setArtifactId("maven-cobertura-plugin");
      //plugin.setVersion("1.4");


      for (Plugin plugin : plugins) {
        printCompletion(plugin);
      }

    } catch (ProjectBuildingException | PluginManagerException | ArtifactResolutionException | PluginNotFoundException | PluginVersionNotFoundException | InvalidPluginException | ArtifactNotFoundException | PluginVersionResolutionException | InvalidVersionSpecificationException e) {
      throw new RuntimeException(e);
    }
  }

  private void addMojoHausPlugins() {
    for (String defaultPluginName : mojoHausPlugins) {
      Plugin plugin = new Plugin();
      plugin.setGroupId("org.codehaus.mojo");
      plugin.setArtifactId(defaultPluginName + "-maven-plugin");
      plugins.add(plugin);
    }
  }

  private void addDefaultPlugins() {
    for (String defaultPluginName : defaultPluginNames) {
      Plugin plugin = new Plugin();
      plugin.setGroupId("org.apache.maven.plugins");
      plugin.setArtifactId("maven-" + defaultPluginName + "-plugin");
      plugins.add(plugin);
    }
  }

  private final List<String> defaultPluginNames = ImmutableList.of(
    "clean",
    "compiler",
    "deploy",
    "failsafe",
    "install",
    "resources",
    "site",
    "surefire",
    "verifier",

    "ear",
    "ejb",
    "jar",
    "rar",
    //"app-client/acr",
    "shade",
    "source",

    "changelog",
    "changes",
    "checkstyle",
    "doap",
    "docck",
    "javadoc",
    "jxr",
    "linkcheck",
    "pmd",
    "project-info-reports",
    "surefire-report",

    "ant",
    "antrun",
    "archetype",
    "assembly",
    "dependency",
    "enforcer",
    "gpg",
    "help",
    "invoker",
    "jarsigner",
    "patch",
    "pdf",
    "plugin",
    "release",
    "remote-resources",
    "repository",
    "scm",
    "scm-publish",
    "stage",
    "toolchains",
    "eclipse"
  );

  private final List<String> mojoHausPlugins = ImmutableList.of(
    "jboss-packaging",
    "was6",
    "weblogic",
    "antlr",
    "aspectj",
    "axistools",
    "castor",
    "commons-attributes",
    "gwt",
    "hibernate3",
    "idlj",
    "javacc",
    "jaxb2",
    "jpox",
    "jslint",
    "js-import",
    "jspc",
    "openjpa",
    "rmic",
    "sablecc",
    "sqlj",
    "xdoclet",
    "xmlbeans",
    "nbm",
    "clirr",
    "cobertura",
    "scmchangelog",
    "sonar",
    "taglist",
    "javancss",
    "jdepend",
    "codenarc",
    "findbugs",
    "fitnesse",
    "selenium",
    "webtest",
    "chronos-jmeter",
    "chronos-surefire",
    "chronos-report",
    "animal-sniffer",
    "appassembler",
    "build-helper",
    "buildnumber",
    "cassandra",
    "ditaot",
    "exec",
    "keytool",
    "latex",
    "license",
    "ounce",
    "rpm",
    "siteskinner",
    "sql",
    "truezip",
    "versions",
    "vfs",
    "xml"
  );

  private final List<Plugin> plugins = new ArrayList<>();

  private void printCompletion(Plugin plugin) throws ArtifactResolutionException, PluginVersionResolutionException, ArtifactNotFoundException, InvalidVersionSpecificationException, InvalidPluginException, PluginManagerException, PluginNotFoundException, PluginVersionNotFoundException {
    PluginDescriptor pluginDescriptor = pluginManager.loadPluginDescriptor(plugin, project, session);
    List<MojoDescriptor> mojos = pluginDescriptor.getMojos();
    for (MojoDescriptor mojo : mojos) {
      StringBuilder builder = new StringBuilder();
      builder.append("complete -c mvn -a \"").append(mojo.getFullGoalName()).append("\" -d \"").append(replaceSpecialChars(mojo.getDescription())).append("\"");
      System.out.println(builder.toString());
    }
  }

  private static String replaceSpecialChars(String description) {
    return GeneratorUtils.toText(description).replace("\n", "\\n").replace("\t", "\\t");
  }
}
