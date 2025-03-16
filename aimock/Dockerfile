FROM openjdk:21-jdk AS build
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests

FROM openjdk:21-jdk AS runtime
WORKDIR /opt/app
EXPOSE 8081
COPY --from=build /opt/app/target/*.jar /opt/app/*.jar
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]

