# Legacy Notes App (Java 1.2 Style)

This is a simple notes application written in a style authentic to the early 2000s (Java 1.2 - 1.4).

It is designed to be a test case for a migration tool that converts legacy applications to modern Java 17 and Spring Boot 3.

## Key Legacy Features:
- **Build System:** Uses Apache Ant (`build.xml`) instead of Maven or Gradle.
- **Web Layer:** Uses the raw `javax.servlet` API with a `web.xml` deployment descriptor.
- **Code Style:** Does not use Generics (e.g., `Hashtable` instead of `Hashtable<Long, Note>`).
- **Dependencies:** Relies on a manually managed JAR file (`servlet-api.jar`) in the `lib/` directory.