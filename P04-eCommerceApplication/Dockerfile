FROM openjdk:8-jdk-alpine
LABEL Developer="priya.laurel@gmail.com"
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/auth-course-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} auth-course.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/auth-course.jar"]