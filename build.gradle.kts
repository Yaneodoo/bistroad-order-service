import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    kotlin("plugin.allopen") version "1.3.72"
}

group = "kr.bistroad"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // Spring Boot JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("mysql:mysql-connector-java")
    testRuntimeOnly("com.h2database:h2")

    // Spring Cloud Kubernetes
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes:1.1.4.RELEASE")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-config:1.1.4.RELEASE")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-ribbon:1.1.4.RELEASE")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
