# AGENTS.md — arete-gradle-test

> New to the arete family? Read the onboarding guide in the family store first:
> https://github.com/mictaege/arete-openspec (README.md).

Project context lives in OpenSpec: `openspec/config.yaml`. This project is the sample/integration
consumer of the arete family — behavior specs live in the sibling repos (`arete/openspec/specs/`,
`arete-gradle/openspec/specs/`), family-level planning in the store `arete` (`~/openspec/arete`,
use `openspec ... --store arete`).

## Working conventions

- Run `./gradlew build` (which will also trigger the arete reporting task) after changing anything
  in `arete` or `arete-gradle` to confirm the whole pipeline still works.
- Treat this as a throwaway/example project: prefer minimal, illustrative test specs over complex
  production-like code.
- `settings.gradle.kts` includes `../arete` and `../arete-gradle` via `includeBuild`, so local
  changes there are exercised here without publishing.
