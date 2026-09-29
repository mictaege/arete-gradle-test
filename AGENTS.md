# AGENTS.md — arete-gradle-test

## Project purpose
`arete-gradle-test` is a small sample/integration-test project that applies the
[`arete-gradle`](https://github.com/mictaege/arete-gradle) plugin (`io.github.mictaege.arete`) to a
plain Java project in order to exercise and validate the plugin's Gradle task and reporting
behaviour end-to-end. It has no README beyond its own name — its value is as a living, buildable
consumer/example rather than as documentation.

Part of the arete family: depends on both `arete` (via `arete-gradle`'s transitive behaviour) and
directly applies the `arete-gradle` plugin.

## Technical stack
- Language: Java (sources under `src/main/java`, tests under `src/test/java`) with Kotlin plugin
  support enabled in the build (`org.jetbrains.kotlin.jvm`).
- Build tool: Gradle (Kotlin DSL, `build.gradle.kts`) — build via `./gradlew`.
- Plugins applied: `java`, `org.jetbrains.kotlin.jvm`, and `io.github.mictaege.arete` (the plugin
  under test), pinned to a specific `arete-gradle` version.
- Testing: JUnit Jupiter, run with the arete extension applied so that the `arete-gradle` reporting
  plugin has real specifications to report on.
- No standalone publishing setup (unlike `arete`/`arete-gradle`) — this project is not released to
  Maven Central; it exists purely to validate the plugin locally.

## Project semantics / domain notes
- Keep the applied `arete-gradle` plugin version here in sync with the version being developed in
  the sibling `arete-gradle` project when doing local, cross-repo testing (e.g. via
  `mavenLocal()`/`includeBuild`, or by bumping the version after a local publish).
- This project's test sources are the primary smoke test for whether `arete-gradle`'s generated
  report reflects the actual arete specifications defined here.

## Working conventions
- Run `./gradlew build` (which will also trigger the arete reporting task) after changing anything
  in `arete` or `arete-gradle` to confirm the whole pipeline still works.
- Treat this as a throwaway/example project: prefer minimal, illustrative test specs over complex
  production-like code.
