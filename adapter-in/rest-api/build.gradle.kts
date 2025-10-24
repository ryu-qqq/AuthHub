plugins {
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Application layer 의존
    implementation(project(":application"))
    implementation(project(":domain"))

    // Spring Web MVC
    implementation(libs.bundles.spring.web)

    // Test
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.restdocs.mockmvc)
    testImplementation(libs.rest.assured)
}

description = "AuthHub REST API Adapter - REST controllers and DTOs"
