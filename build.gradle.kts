import com.github.mictaege.arete_gradle.CatppuccinFrappeColors

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("io.github.mictaege.arete") version "2025.1-rc1"
    `maven-publish`
    signing
}

group = "io.github.mictaege"
version = "2025.1-rc1"

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.github.mictaege:arete:2025.1-rc1")
    testImplementation("org.hamcrest:hamcrest:3.0")
    testImplementation("com.google.guava:guava:33.5.0-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

arete {
    colorScheme = CatppuccinFrappeColors()
}

tasks.test {
    useJUnitPlatform()
}