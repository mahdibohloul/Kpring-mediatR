import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.5.5"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id("org.sonarqube") version "3.3"
  kotlin("jvm") version "1.5.31"
  kotlin("plugin.spring") version "1.5.31"
}

group = "cab.mb"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
//  mavenCentral()
  jcenter()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.4") {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
  }
  implementation("org.springframework.boot:spring-boot-starter:2.5.4")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.4")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "11"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

sonarqube {
  properties {
    property("sonar.projectKey", "mahdibohloul_spring-reactive-mediatR")
    property("sonar.organization", "mahdibohloul")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}