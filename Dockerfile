# Stage 1: Cache Gradle dependencies
FROM gradle:latest AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY *.gradle.kts gradle.properties /home/gradle/app/

RUN mkdir -p /home/gradle/app/gradle
COPY gradle/libs.versions.toml /home/gradle/app/gradle/

RUN mkdir -p /home/gradle/app/server
COPY server/build.gradle.kts /home/gradle/app/server/

RUN mkdir -p /home/gradle/app/shared
COPY shared/build.gradle.kts /home/gradle/app/shared/

RUN mkdir -p /home/gradle/app/shared/src/commonMain/kotlin/error
COPY shared/src/commonMain/kotlin/error/ErrorCodes.kt /home/gradle/app/shared/src/commonMain/kotlin/error/ErrorCodes.kt

WORKDIR /home/gradle/app
RUN gradle server:clean server:build -i --stacktrace --build-cache

# Stage 2: Build Application
FROM gradle:latest AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY . /usr/src/app/
WORKDIR /usr/src/app
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Build the fat JAR, Gradle also supports shadow
# and boot JAR by default.
RUN gradle server:buildFatJar --no-daemon --build-cache

# Stage 3: Create the Runtime Image
FROM amazoncorretto:23-alpine AS runtime
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/stgin.jar

ENTRYPOINT ["java","-jar","/app/stgin.jar"]