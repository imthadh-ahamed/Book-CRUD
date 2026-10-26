# ── Stage 1: Build ────────────────────────────────────────────────────────────
#
# Build context is the repository root (see compose.yaml build.context).
# All paths here are relative to that root.
#
# Why the repo root as context?
# pom.xml's Maven resource plugin copies database/migrations onto the classpath:
#   <directory>${project.basedir}/../database/migrations</directory>
# That path resolves to one level above backend/, which is the repository root.
# The build context must include both backend/ and database/ for the Maven build
# to succeed inside the container.
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper. chmod makes the script executable regardless of how the
# host filesystem stored the execute bit (e.g. on Windows without gitattributes).
COPY backend/.mvn ./backend/.mvn
COPY backend/mvnw ./backend/mvnw
RUN chmod +x ./backend/mvnw

# Copy pom.xml and resolve dependencies before copying source.
# This layer is only re-run when pom.xml changes, not when src/ changes.
COPY backend/pom.xml ./backend/pom.xml

# Copy the Flyway migration SQL files. The Maven resource plugin references
# them at ${project.basedir}/../database/migrations, which resolves to
# /build/database/migrations inside this container.
COPY database/migrations ./database/migrations

RUN cd backend && ./mvnw dependency:go-offline -B

# Copy application source and package. Tests are skipped here; run
# `./mvnw test` locally or in CI before building the image.
COPY backend/src ./backend/src
RUN cd backend && ./mvnw package -DskipTests -B

# ── Stage 2: Runtime ──────────────────────────────────────────────────────────
#
# Use a JRE-only image to keep the final image smaller. The JDK is not needed
# to run a packaged Spring Boot JAR.
FROM eclipse-temurin:21-jre-alpine AS runtime

# Run the application as a non-root user. addgroup/adduser are BusyBox
# commands available on Alpine-based images.
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# The Spring Boot Maven plugin repackages the application into a single
# executable JAR. The filename comes from pom.xml artifactId and version:
#   artifactId=backend, version=0.0.1-SNAPSHOT → backend-0.0.1-SNAPSHOT.jar
COPY --from=builder /build/backend/target/backend-0.0.1-SNAPSHOT.jar app.jar

USER appuser

EXPOSE 8080

# JVM memory sizing note:
# -XX:MaxRAMPercentage=75.0 is a common production flag that caps heap at 75% of
# the container's cgroup memory limit. It is intentionally omitted here because
# compose.yaml sets no mem_limit on the app service — without a limit the JVM
# would read total host RAM, which is too permissive for a dev environment.
# To enable it, add `mem_limit: 512m` (or similar) to the app service in
# compose.yaml and then add -XX:MaxRAMPercentage=75.0 to this ENTRYPOINT.
#
# Health check note:
# A HEALTHCHECK instruction requires an HTTP endpoint to probe. The simplest
# option is spring-boot-starter-actuator (/actuator/health). It is not included
# in this project's pom.xml to keep dependencies minimal. If you add actuator,
# replace the ENTRYPOINT line below with:
#   HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
#     CMD wget -q --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
