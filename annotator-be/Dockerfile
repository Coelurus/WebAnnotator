FROM openjdk:21 AS builder
WORKDIR /opt/app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests

FROM openjdk:21 AS runner
WORKDIR /opt/app
RUN mkdir -p /logs && \
    groupadd -r appgroup && useradd -r -g appgroup appuser && \
    chown -R appuser:appgroup /logs
RUN mkdir -p /opt/app && \
    chown -R appuser:appgroup /opt/app
EXPOSE 8080
COPY --from=builder /opt/app/target/*.jar /opt/app/*.jar
USER appuser
ENTRYPOINT ["java", "-jar", "/opt/app/*.jar" ]
