FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/saas-backend-starter-0.0.1-SNAPSHOT.jar app.jar

# (dijagnostika opcionalno, možete ostaviti ako želite)
RUN ls -lh /app && unzip -l app.jar | grep BOOT-INF || echo "BOOT-INF NOT FOUND"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
