import org.jetbrains.intellij.IntelliJPluginExtension

val kotlinVersion: String by extra
val detektVersion: String by extra
val detektIntellijPluginVersion: String by extra

project.group = "io.gitlab.arturbosch.detekt"
project.version = detektIntellijPluginVersion

repositories {
  mavenLocal()
  jcenter()
  maven { setUrl("http://dl.bintray.com/jetbrains/intellij-plugin-service") }
}

plugins {
  id("org.jetbrains.intellij").version("0.4.8")
  id("com.github.ben-manes.versions") version "0.20.0"
  kotlin("jvm").version("1.3.31")
  id("org.sonarqube") version "2.6.2"
}

val customBuild = DetektSourceSetCreator(project)
customBuild.initDetektSourceSets()
customBuild.initDetektTestSourceSets()

val detektVersions = DetektVersions(project)

dependencies {
  implementation(project(":detekt-intellij-api"))
  implementation(project(":detekt-bridge"))
}

configure<IntelliJPluginExtension> {
  pluginName = "Detekt IntelliJ Plugin"
  version = "2019.1.2"
  updateSinceUntilBuild = false
  setPlugins("IntelliLang", "Kotlin")
}

tasks.withType<Wrapper> {
  gradleVersion = "5.4.1"
  distributionType = Wrapper.DistributionType.ALL
}
