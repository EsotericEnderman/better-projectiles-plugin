import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("xyz.jpenilla.run-paper") version "2.3.0"
  id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  kotlin("jvm")
}

fun capitaliseFirstLetter(string: String): String {
  return string.first().uppercase() + string.slice(IntRange(1, string.length - 1))
}

fun snakecase(kebabCaseString: String): String {
  return kebabCaseString.lowercase().replace('-', '_')
}

fun pascalcase(kebabCaseString: String): String {
  var pascalCaseString = ""

  val splitString = kebabCaseString.split("-")

  for (part in splitString) {
    pascalCaseString += capitaliseFirstLetter(part)
  }

  return pascalCaseString
}

val projectAuthor = "Esoteric Enderman"

group = "dev.enderman"
version = "0.1.0"
description = "A plugin that improves Minecraft's projectiles."

val javaVersion = 21
val paperApiVersion = "1.21.3"

java {
  toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
}

dependencies {
  paperweight.paperDevBundle(paperApiVersion + "-R0.1-SNAPSHOT")
  implementation(kotlin("stdlib-jdk8"))
}

tasks {
  compileJava {
    options.release = javaVersion
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }

  shadowJar {
    archiveClassifier.set("")
    configurations = listOf(project.configurations.runtimeClasspath.get())
  }

  build {
    dependsOn(shadowJar)
  }
}

bukkitPluginYaml {
  main = project.group.toString() + ".minecraft.plugins.projectiles.better." + pascalcase(rootProject.name)
  name = "BetterProjectiles"
  load = BukkitPluginYaml.PluginLoadOrder.STARTUP
  authors.add(projectAuthor)
  apiVersion = paperApiVersion
}

repositories {
  mavenCentral()
}

kotlin {
  jvmToolchain(21)
}
