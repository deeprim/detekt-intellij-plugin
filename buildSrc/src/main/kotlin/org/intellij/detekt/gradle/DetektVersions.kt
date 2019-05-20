import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

class DetektVersions(
    private val project: Project

)  {
  private val supportedDetektVersions =
      listOf("1.0.0-RC14.1")
}

class DetektSourceSetCreator(
    val project: Project
) {

  fun printProjectName() {
    println(project.name)
  }

  /**
   * Initialize detekt source sets.
   */
  fun initDetektSourceSets(): DetektSourceSetCreator {
    val sourceSets = project.properties["sourceSets"]
     as SourceSetContainer

    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

    // Create detekt source set
    val detektSourceSet = sourceSets.create("detektsource").apply {
      compileClasspath += mainSourceSet.output
      runtimeClasspath += mainSourceSet.output
    }

    sourceSets.add(detektSourceSet)

    // Derive all its configurations from 'main', so 'detektSourceSets' code can see 'main' code
    project.configurations.apply {
      getByName(detektSourceSet.compileConfigurationName)
          .extendsFrom(getByName(mainSourceSet.compileConfigurationName))

      getByName(detektSourceSet.compileOnlyConfigurationName)
          .extendsFrom(getByName(mainSourceSet.compileOnlyConfigurationName))

      getByName(detektSourceSet.compileClasspathConfigurationName)
          .extendsFrom(getByName(mainSourceSet.compileClasspathConfigurationName))

      getByName(detektSourceSet.runtimeConfigurationName)
          .extendsFrom(getByName(mainSourceSet.runtimeConfigurationName))
    }

    // Wire task dependencies  to match the classpath dependencies
    // - compileTest -> compileDetektJava
    // - tettClasses -> detektClasses
    // - jar         -> detektClasses
    project.tasks.apply {
      getByName(JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME)
          .dependsOn(getByName(detektSourceSet.compileJavaTaskName))

      getByName(JavaPlugin.TEST_CLASSES_TASK_NAME)
          .dependsOn(getByName(detektSourceSet.classesTaskName))

      getByName(JavaPlugin.JAR_TASK_NAME)
          .dependsOn(getByName(detektSourceSet.classesTaskName))
    }

    return this
  }

  fun initDetektTestSourceSets() {
    println("Initializing test source set")
  }

}
