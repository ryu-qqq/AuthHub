plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Application layer 의존
    implementation(project(":application"))
    implementation(project(":domain"))

    // Spring Data Redis
    implementation(libs.spring.boot.starter.data.redis)

    // Test
    testImplementation(libs.bundles.testing.basic)
    testImplementation(libs.bundles.testing.spring)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.archunit.junit5)
}

description = "AuthHub Persistence Adapter - Redis repositories and cache access"
