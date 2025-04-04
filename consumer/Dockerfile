FROM eclipse-temurin:21-jre-jammy

ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

ADD target/consumer*SNAPSHOT-executable.jar /opt/app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "/opt/app.jar"]