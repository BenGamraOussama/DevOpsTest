FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8089
ADD target/student-management-0.0.1-SNAPSHOT.jar student-management-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/student-management-0.0.1-SNAPSHOT.jar"]