plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

dependencies {
    // Application layer 의존
    implementation(project(":application"))
    implementation(project(":domain"))

    // Spring Data JPA
    implementation(libs.bundles.spring.data.jpa)

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:${libs.versions.querydsl.get()}:jakarta")
    annotationProcessor("com.querydsl:querydsl-apt:${libs.versions.querydsl.get()}:jakarta")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Database
    runtimeOnly(libs.postgresql)
    runtimeOnly(libs.h2)

    // Test
    testImplementation(libs.bundles.testcontainers)
}

description = "AuthHub Persistence Adapter - JPA repositories and database access"
