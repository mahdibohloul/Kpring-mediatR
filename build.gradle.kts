import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.sonarqube") version "3.3"
    kotlin("jvm") version "1.5.31"
}

group = "io.github.mahdibohloul"
version = "1.0.0"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("junit:junit:4.13.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.5")
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

sonarqube {
    properties {
        property("sonar.projectKey", "mahdibohloul_spring-reactive-mediatR")
        property("sonar.organization", "mahdibohloul")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}
