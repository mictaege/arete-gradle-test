plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("io.github.mictaege.arete") version "2022.6"
    `maven-publish`
    signing
}

group = "io.github.mictaege"
version = "2022.6"

tasks.wrapper {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("io.github.mictaege:arete:2022.6")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.google.guava:guava:31.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

tasks.test {
    useJUnitPlatform()
}