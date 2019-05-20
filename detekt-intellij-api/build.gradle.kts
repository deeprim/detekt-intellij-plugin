import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by extra
val detektVersion: String by extra

repositories {
  mavenLocal()
  jcenter()
}

plugins {
  java
  kotlin("jvm")
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = "1.8"
}
