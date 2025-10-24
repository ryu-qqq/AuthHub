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
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.spring.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit)
    testImplementation(libs.archunit.junit5)
    testImplementation(libs.assertj.core)
}

// Test 태그 구분 (Unit vs Integration)
tasks.named<Test>("test") {
    useJUnitPlatform {
        // Test tag filtering: Set via -Dtest.tags=unit,integration in gradlew command
        // CLI Example: ./gradlew test -Dtest.tags=unit
        //
        // IDE Setup (IntelliJ IDEA):
        //   1. Run → Edit Configurations
        //   2. Select JUnit configuration
        //   3. VM options: -Dtest.tags=unit
        //
        // IDE Setup (Eclipse):
        //   1. Run → Run Configurations
        //   2. Arguments tab → VM arguments: -Dtest.tags=unit
        val testTags = System.getProperty("test.tags")
        if (!testTags.isNullOrBlank()) {
            includeTags(testTags)
        }
    }
}

description = "AuthHub Application Layer - Use cases and application services"
