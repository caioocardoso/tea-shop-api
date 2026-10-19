# Estágio 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia pom.xml e baixa dependências para aproveitar cache de camadas
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código-fonte e compila gerando o JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final para execução (JRE leve)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copia apenas o JAR gerado no estágio de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
