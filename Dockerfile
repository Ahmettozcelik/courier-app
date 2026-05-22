FROM eclipse-temurin:21-jdk

WORKDIR /app

# 🚀 Joker (*) yerine tam isim yazarak cache hatasını önlüyoruz
COPY target/tracking-0.0.1-SNAPSHOT.jar app.jar

# 🚀 Uygulamanın iç portu olan 8081 ile eşitliyoruz
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
