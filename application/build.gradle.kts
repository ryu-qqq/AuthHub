plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Domain layer 의존
    implementation(project(":domain"))

    // Spring Framework (DI 컨테이너만 사용, Boot는 사용 안 함)
    implementation(libs.spring.context)
    implementation(libs.spring.tx)

    // Validation
    implementation(libs.jakarta.validation.api)
    implementation(libs.hibernate.validator)

    // Test
    testImplementation(libs.spring.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
}

description = "AuthHub Application Layer - Use cases and application services"
