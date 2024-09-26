FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./target/transfers-1.0.0.jar /app

EXPOSE 8080

CMD ["java", "-jar", "transfers-1.0.0.jar", "--spring.main.class=com.distribuidos.transfers.Application"]
