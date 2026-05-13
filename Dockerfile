# Imagen base con Java 17
FROM eclipse-temurin:17-jdk

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar todo el proyecto al contenedor
COPY . .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Compilar el proyecto con Maven (sin ejecutar tests)
RUN ./mvnw clean package -DskipTests

# Ejecutar el JAR generado users-0.0.1-SNAPSHOT
CMD ["java", "-jar", "target/users-0.0.1-SNAPSHOT.jar"]
