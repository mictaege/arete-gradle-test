plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("io.github.mictaege.arete") version "2022.9"
    `maven-publish`
    signing
}

group = "io.github.mictaege"
version = "2022.9"

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.github.mictaege:arete:2022.9")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.google.guava:guava:31.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}