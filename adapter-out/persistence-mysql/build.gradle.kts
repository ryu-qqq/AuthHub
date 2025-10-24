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

    // QueryDSL - 현재 미사용, 필요 시 주석 해제
    // implementation("com.querydsl:querydsl-jpa:${libs.versions.querydsl.get()}:jakarta")
    // annotationProcessor("com.querydsl:querydsl-apt:${libs.versions.querydsl.get()}:jakarta")
    // annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    // annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    // Database
    runtimeOnly(libs.mysql.connector)
    runtimeOnly(libs.h2)

    // Test
    testImplementation(libs.bundles.testing.basic)
    testImplementation(libs.bundles.testing.spring)
    testImplementation(libs.bundles.testcontainers)
    testImplementation(libs.archunit.junit5)
}

description = "AuthHub Persistence Adapter - JPA repositories and database access"
