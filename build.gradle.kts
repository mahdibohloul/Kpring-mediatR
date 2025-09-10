import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.23"
  kotlin("plugin.spring") version "1.9.23"
  id("io.spring.dependency-management") version "1.1.7"

  id("com.vanniktech.maven.publish") version "0.34.0"
  id("com.diffplug.spotless") version "7.2.1"
  id("io.gitlab.arturbosch.detekt") version "1.23.6"
  `java-library`
}

group = "io.github.mahdibohloul"
version = "2.0.0"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}


repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework:spring-context:6.2.10")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:1.9.0")
  implementation("org.slf4j:slf4j-api:2.0.17")
  implementation("org.springframework.boot:spring-boot-autoconfigure:3.5.5")

  testImplementation("org.springframework.boot:spring-boot-starter-test:3.5.5")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.23")
  testImplementation("io.projectreactor:reactor-test:3.7.9")
  testImplementation("io.projectreactor:reactor-core:3.7.9")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.9.0")

  testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

spotless {
  kotlin {
    target("src/**/*.kt")
    ktlint()
      .editorConfigOverride(
        mapOf(
          "indent_size" to 2,
          "ktlint_standard_filename" to "disabled",
          "ktlint_standard_max-line-length" to "120"
        )
      )
    trimTrailingWhitespace()
    leadingTabsToSpaces()
    endWithNewline()
  }
}

detekt {
  buildUponDefaultConfig = true
  allRules = true
  config.setFrom("$projectDir/detekt.yml")
  baseline = file("$projectDir/detekt-baseline.xml")
}

//tasks.register("verifyReadmeContent") {
//  doLast {
//    val readmeFile = file("README.md")
//    val content = readmeFile.readText()
//
//    // List of checks
//    val checks = listOf(
//      Check("group ID", """<groupId>${project.group}</groupId>"""),
//      Check("version", """<version>${project.version}</version>"""),
//      Check("Spring Boot version", "spring-boot-starter-actuator"),
//    )
//
//    val errors = checks.mapNotNull { check ->
//      if (!content.contains(check.expectedValue)) {
//        "Missing or incorrect ${check.name}: ${check.expectedValue}"
//      } else null
//    }
//
//    if (errors.isNotEmpty()) {
//      throw GradleException(
//        """
//                README content verification failed!
//                ${errors.joinToString("\n")}
//                Please update the README.md with correct values
//            """.trimIndent()
//      )
//    }
//  }
//}
//
//tasks.check {
//  dependsOn("verifyReadmeContent")
//}

data class Check(val name: String, val expectedValue: String)
