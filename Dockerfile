# First stage 

# Specific the jdk version and give it a name
FROM eclipse-temurin:25-jdk AS build

# From now on build in this foler
WORKDIR /workspace

# Copy file from computer to docker image
# Its purpose is to copy the setup file(The Maven Warpper and Exact maven version)
# The first one is soruce file and the
# Second is a folder name that we told docker to create inside container
COPY .mvn/ .mvn/
# Maven need to understand project dependencies in pom.xml
COPY mvnw pom.xml ./

# Chmod +x mvnw this give maven permission to execute
# The rest is download dependency that require by maven project(dependency from pom.xml)
RUN chmod +x mvnw && ./mvnw -B -q -Dmaven.test.skip=true dependency:go-offline
# ./mvnw = Execute Maven Warpper
# -B = Batch mode (Disables interactive prompts, Uses default values, Cleans up build logs, Fails fast on errors) 
# -q = Quick mode (less output)
# -Dmaven.test.skip=true = skip running test
# Dependency:go-offline = Download dependencies in advance
# Why down in advance? because use layer caching 
# If pom.xml hasn't changed, Docker can reuse the dependency-download layer instead of downloading everything again.

# Copy the source code into the container
# Copy from src foler and creae src folder inside container
COPY src/ src/

# Build the project and put it in the package
RUN ./mvnw -B -q -Dmaven.test.skip=true package

# Second stage

# Specific the environment and run the application
FROM eclipse-temurin:25-jre

# From now on run in this folder and we will put our application in JAR 
WORKDIR /app

# Create user and system user assign id ot user and give it a name
# The purpose : prevent hacker from install malware etc..
RUN useradd --system --uid 1001 spring
# useradd = create new Linux user
# --system = create a system user
# --uid 1001 = give that user an Id
# spring = is a name we gave to that user

# Important = we are copying the only file jar not entire build environment
COPY --from=build /workspace/target/*.jar app.jar
# --from=build = copy file from the first stage
# /workspace/target/...jar = The Source file that it need to copy
# app.jar = Destination filename.

# Tells Docker to execute subsequent commands as the spring user instead of root.
USER spring

# Port application
EXPOSE 8080

# This tells Docker what command to execute when the container starts
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]
