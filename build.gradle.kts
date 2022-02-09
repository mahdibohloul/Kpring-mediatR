import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.sonarqube") version "3.3"
  kotlin("jvm") version "1.5.31"
  `maven-publish`
  signing
  id("org.jetbrains.dokka") version "0.10.1"
}

group = "io.github.mahdibohloul"
version = "1.1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
  mavenCentral()
}

dependencies {
  implementation(group = "org.springframework", name = "spring-context", version = "5.1.4.RELEASE")
  implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.25")
  implementation(group = "javax.validation", name = "validation-api", version = "2.0.1.Final")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
  implementation("junit:junit:4.13.2")
  testImplementation("org.springframework.boot:spring-boot-starter-test:2.6.1")
  testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
  testImplementation(group = "org.mockito", name = "mockito-core", version = "2.23.4")
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

tasks.withType<Sign> {
  onlyIf {
    !version.toString().endsWith("SNAPSHOT")
  }
}

java {
  withJavadocJar()
  withSourcesJar()
}

signing {
  sign(publishing.publications)
}

publishing {
  repositories {
    maven {
      name = "GitHubPackages"
      url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      credentials {
        username = project.property("ossrhUsername").toString()
        password = project.property("ossrhPassword").toString()
      }
    }
  }

  publications {
    create("mavenJava", MavenPublication::class.java).apply {
      groupId = project.group.toString()
      this.artifactId = artifactId
      version = project.version.toString()
      pom {
        description.set("Implementation of Mediator pattern using Kotlin coroutines for JVM and Spring Framework")
        name.set(artifactId)
        url.set("https://github.com/mahdibohloul/Kpring-mediatR")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
            distribution.set("repo")
          }
        }
        developers {
          developer {
            id.set("mahdibohloul")
            name.set("Mahdi Bohloul")
            email.set("mahdiibohloul@gmail.com")
          }
        }
        scm {
          url.set("https://github.com/mahdibohloul/Kpring-mediatR")
        }
      }
      from(components["java"])
    }
  }
}

sonarqube {
  properties {
    property("sonar.projectKey", "mahdibohloul_spring-reactive-mediatR")
    property("sonar.organization", "mahdibohloul")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}
