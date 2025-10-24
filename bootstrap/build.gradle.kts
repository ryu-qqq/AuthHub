import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Netty 명시적 버전 지정 (최우선 순위)
    implementation(platform("io.netty:netty-bom:4.1.107.Final"))
    implementation("io.netty:netty-common")
    implementation("io.netty:netty-handler")
    implementation("io.netty:netty-transport")
    implementation("io.netty:netty-buffer")
    implementation("io.netty:netty-codec")
    implementation("io.netty:netty-resolver")

    // 모든 모듈 의존
    implementation(project(":domain"))
    implementation(project(":application"))
    implementation(project(":adapter-in:rest-api"))
    implementation(project(":adapter-out:persistence-mysql"))
    // persistence-redis 코드는 bootstrap에 직접 통합됨 (Netty 의존성 충돌 방지)

    // Spring Boot Starters
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis) {
        exclude(group = "io.lettuce", module = "lettuce-core")
    }
    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")  // Use specific version without Netty conflict
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
