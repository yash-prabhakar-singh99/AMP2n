FROM maven:3.8.1-openjdk-17 AS MAVEN_BUILD
COPY pom.xml /build/
COPY mvnw /build/
COPY .mvn /build/.mvn
COPY src /build/src/
WORKDIR /build/
RUN mvn clean install -Dmaven.test.skip=true -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -B --no-transfer-progress -e && mvn package  -Dmaven.test.skip=true -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=WARN -B --no-transfer-progress -e

FROM openjdk:17-oracle
WORKDIR /appdocker
COPY --from=MAVEN_BUILD /build/target/AMP2-0.0.1-SNAPSHOT.jar /appdocker/amp2docker.jar
EXPOSE 8080
ENTRYPOINT [ "java","-Dfile.encoding=UTF-8","-Djava.security.egd=file:/dev/./urandom","-jar","/appdocker/amp2docker.jar"]

