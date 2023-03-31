FROM maven:3.8.4-jdk-11-slim as builder
WORKDIR /src
COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./src ./src
RUN mvn package -DskipTests
RUN ls
RUN mvn sonar:sonar \
  -Dsonar.projectName=afinny-credit-dev \
  -Dsonar.projectKey=afinny-credit-dev \
  -Dsonar.host.url=http://sonarqube9.astondevs.ru:9000 \
  -Dsonar.login=squ_574b0a98897c92c6dc17276293ff6b1cc592038f \
  -Dsonar.java.binaries=/src/target/classes/**/* \
  -Dsonar.java.test.binaries=/src/target/test-classes/**/* \
  -Dsonar.java.test.exclude=test \
  -Dsonar.jacoco.reportPath=/src/target/jacoco.exec \
  -Dsonar.coverage.exclusions=**/Jwt*.java,**/Dto*.java

FROM alpine:3.15.3
RUN apk --no-cache add openjdk11-jre
COPY --from=builder /src/target/credit-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
