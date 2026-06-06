import com.github.mictaege.arete_gradle.CatppuccinFrappeColors

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("io.github.mictaege.arete") version "2026.3"
    `maven-publish`
    signing
}

group = "io.github.mictaege"
version = "2026.3"

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.github.mictaege:arete:2026.3")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("com.google.guava:guava:33.5.0-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

arete {
    captureStdout = true
    captureStderr = true
    captureMaxBuffer = 1048576
    colorScheme = object : CatppuccinFrappeColors() {
        override var arete_plantuml_theme = "amiga"
    }
}

tasks.test {
    useJUnitPlatform()
}