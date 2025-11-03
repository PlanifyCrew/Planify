plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.spring") version "1.9.10"
}

group = "com.heroku"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.postgresql:postgresql")
    implementation("org.apache.commons:commons-dbcp2")
    implementation("software.amazon.awssdk:sqs:2.34.8")
    implementation("org.springframework.boot:spring-boot-starter-amqp") // RabbitMQ
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") // Datenbank
    implementation("org.springframework.amqp:spring-rabbit:3.2.6")
    implementation("org.springframework.amqp:spring-amqp:3.2.6")
    implementation("org.postgresql:postgresql") // PostgreSQL Treiber
    // implementation("software.amazon.awssdk:aws-sdk-java") // A bit heavy, loads the whole skd
}
