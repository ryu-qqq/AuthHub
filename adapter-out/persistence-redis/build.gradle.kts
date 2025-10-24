import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    alias(libs.plugins.spring.dependency.management)
}

the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // Application layer 의존
    implementation(project(":application"))
    implementation(project(":domain"))

    // Netty 명시적 버전 지정 (최우선 순위)
    implementation(platform("io.netty:netty-bom:4.1.107.Final"))
    implementation("io.netty:netty-common")
    implementation("io.netty:netty-handler")
    implementation("io.netty:netty-transport")
    implementation("io.netty:netty-buffer")
    implementation("io.netty:netty-codec")
    implementation("io.netty:netty-resolver")

    // Spring Data Redis (Lettuce 버전 명시)
    implementation("org.springframework.boot:spring-boot-starter-data-redis") {
        exclude(group = "io.lettuce", module = "lettuce-core")
    }
    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(rootProject.libs.bundles.testcontainers)
}

description = "AuthHub Persistence Adapter - Redis repositories and cache access"
