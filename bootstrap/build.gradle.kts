import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // 모든 모듈 의존
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":adapter-in:rest-api"))
    implementation(project(":adapter-out:persistence-mysql"))

    // Spring Boot Starters
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.security)

    // Observability
    implementation(libs.bundles.observability)

    // API Documentation
    implementation(libs.springdoc.openapi)

    // Configuration Processor
    annotationProcessor(libs.spring.boot.configuration.processor)

    // Test
    testImplementation(libs.bundles.testing.spring)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.archunit.junit5)
}

// Bootstrap 모듈만 bootJar 활성화
tasks.named<BootJar>("bootJar") {
    enabled = true
    archiveClassifier.set("boot")
}

tasks.named<Jar>("jar") {
    enabled = true
}

description = "AuthHub Bootstrap - Application entry point and configuration"
