
FROM maven:3.9-amazoncorretto-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY core/domain/pom.xml           core/domain/pom.xml
COPY core/application/pom.xml      core/application/pom.xml
COPY infrastructure/mysql/pom.xml  infrastructure/mysql/pom.xml
COPY infrastructure/security/pom.xml infrastructure/security/pom.xml
COPY infrastructure/rest/pom.xml   infrastructure/rest/pom.xml
COPY bootstrap/pom.xml             bootstrap/pom.xml

RUN mvn dependency:go-offline -q


COPY core/       core/
COPY infrastructure/ infrastructure/
COPY bootstrap/  bootstrap/

RUN mvn package -DskipTests -q

FROM amazoncorretto:21-al2023-headless

WORKDIR /app

USER 1000:1000

COPY --from=builder /app/bootstrap/target/library-management-system.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
