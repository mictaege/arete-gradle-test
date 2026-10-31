import com.github.mictaege.arete_gradle.CatppuccinFrappeColors

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "2.4.21"
    id("io.github.mictaege.arete") version "2026.7"
    `maven-publish`
    signing
}

group = "io.github.mictaege"
version = "2026.7"

tasks.wrapper {
    gradleVersion = "9.8.2"
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
    testImplementation("io.github.mictaege:arete:2026.7")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("com.google.guava:guava:33.7.2-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
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