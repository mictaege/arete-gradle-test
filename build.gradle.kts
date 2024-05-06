plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("io.github.mictaege.arete") version "2024.2"
    `maven-publish`
    signing
}

group = "io.github.mictaege"
version = "2024.2"

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.github.mictaege:arete:2024.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.google.guava:guava:33.2.0-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.test {
    useJUnitPlatform()
}