plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Application layer 의존
    implementation(project(":application"))
    implementation(project(":domain"))

    // Persistence layer 의존 (통합 테스트용)
    // testImplementation(project(":adapter-out-persistence-redis")) // TODO: Enable for integration tests

    // Spring Web MVC
    implementation(libs.bundles.spring.web)

    // JWT (JJWT 0.12.x)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.rest.assured)
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}

description = "AuthHub REST API Adapter - REST controllers and DTOs"
