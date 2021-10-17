import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.sonarqube") version "3.3"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

group = "io.mb"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.5.5") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter:2.5.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.2-native-mt")
    implementation("org.aspectj:aspectjweaver:1.8.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2-native-mt")
    implementation("junit:junit:4.13.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation("io.projectreactor:reactor-test:3.4.10")
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
