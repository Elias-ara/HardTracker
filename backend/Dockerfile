FROM eclipse-temurin:17-jdk-alpine

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo .jar gerado pelo Maven para dentro do container
COPY target/argus-api-0.0.1-SNAPSHOT.jar app.jar

# Expõe a porta que o Spring Boot usa (padrão 8080)
EXPOSE 8080

# O comando que roda o aplicativo quando o container sobe
ENTRYPOINT ["java", "-jar", "app.jar"]