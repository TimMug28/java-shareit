FROM amazoncorretto:11-alpine-jdk
COPY target/shareit-server-0.0.1-SNAPSHOT.jar app.jar
ENV TZ="Asia/Yekaterinburg"
ENTRYPOINT ["java","-jar","/app.jar"]