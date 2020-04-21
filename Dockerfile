FROM maven:3.6.0-jdk-8 as maven

COPY ./pom.xml ./pom.xml

RUN mvn dependency:go-offline -B

COPY ./src ./src

RUN mvn package -DskipTests

FROM openjdk:8-jre-alpine

WORKDIR /app

COPY --from=maven target/neotech-demo-0.0.1-SNAPSHOT.jar ./

ARG args
ENV app_args=$args

ENTRYPOINT ["sh", "-c", "java -jar ./neotech-demo-0.0.1-SNAPSHOT.jar $app_args"]