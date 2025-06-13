@echo off
echo Building the app (Maven)...
call mvnw clean package -DskipTests

echo.
echo Starting Docker Compose services...
docker-compose up --build