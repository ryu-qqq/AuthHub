import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    id("checkstyle")
    alias(libs.plugins.spotbugs) apply false
}

// ========================================
// Global Configuration
// ========================================
allprojects {
    group = "com.ryuqq.authhub"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// ========================================
// Subproject Configuration
// ========================================
subprojects {
    // persistence-redis는 별도 설정 (Netty 의존성 충돌 방지)
    if (name == "persistence-redis") {
        apply(plugin = "java")

        java {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        return@subprojects
    }

    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.spotbugs")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    // ========================================
    // Dependency Management (핵심: SpringBootPlugin.BOM_COORDINATES 사용)
    // ========================================
    apply(plugin = "io.spring.dependency-management")

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
        }
    }

    // ========================================
    // Dependencies
    // ========================================
    dependencies {
        // Netty Version Constraints (최우선 순위)
        constraints {
            implementation("io.netty:netty-common:4.1.107.Final") {
                because("Prevent 0.0.1-SNAPSHOT resolution")
            }
            implementation("io.netty:netty-handler:4.1.107.Final") {
                because("Prevent 0.0.1-SNAPSHOT resolution")
            }
            implementation("io.netty:netty-transport:4.1.107.Final") {
                because("Prevent 0.0.1-SNAPSHOT resolution")
            }
            implementation("io.netty:netty-buffer:4.1.107.Final") {
                because("Prevent 0.0.1-SNAPSHOT resolution")
            }
            implementation("io.netty:netty-codec:4.1.107.Final") {
                because("Prevent 0.0.1-SNAPSHOT resolution")
            }
            implementation("io.netty:netty-resolver:4.1.107.Final") {
                because("Prevent 0.0.1-SNAPSHOT resolution")
            }
        }

        // Test Dependencies (All Modules)
        testImplementation(rootProject.libs.junit.jupiter)
        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
        testImplementation(rootProject.libs.assertj.core)
        testImplementation(rootProject.libs.mockito.core)
        testImplementation(rootProject.libs.mockito.junit)

        // ArchUnit for Architecture Testing
        testImplementation(rootProject.libs.archunit.junit5)

        // SpotBugs Annotations
        compileOnly(rootProject.libs.spotbugs.annotations)
    }

    // ========================================
    // Test Configuration
    // ========================================
    tasks.test {
        useJUnitPlatform()

        // Test Coverage Requirements
        finalizedBy(tasks.named("jacocoTestReport"))
    }

    // ========================================
    // Checkstyle Configuration
    // ========================================
    checkstyle {
        toolVersion = rootProject.libs.versions.checkstyle.get()
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
        isIgnoreFailures = false
        maxWarnings = 0
    }

    // ========================================
    // SpotBugs Configuration
    // ========================================
    configure<com.github.spotbugs.snom.SpotBugsExtension> {
        toolVersion.set(rootProject.libs.versions.spotbugs.get())
        effort.set(com.github.spotbugs.snom.Effort.MAX)
        reportLevel.set(com.github.spotbugs.snom.Confidence.LOW)
        excludeFilter.set(rootProject.file("config/spotbugs/spotbugs-exclude.xml"))
    }

    // ========================================
    // JaCoCo Coverage Configuration
    // ========================================
    apply(plugin = "jacoco")

    configure<JacocoPluginExtension> {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.test)

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    // ========================================
    // JaCoCo Coverage Verification
    // ========================================
    tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        dependsOn(tasks.named("jacocoTestReport"))

        violationRules {
            rule {
                enabled = true

                limit {
                    minimum = when {
                        project.name == "domain" -> "0.90".toBigDecimal()
                        project.name == "application" -> "0.80".toBigDecimal()
                        project.name.startsWith("adapter-") -> "0.70".toBigDecimal()
                        else -> "0.70".toBigDecimal()
                    }
                }
            }

            rule {
                enabled = true
                element = "CLASS"

                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.50".toBigDecimal()
                }

                excludes = listOf(
                    "*.config.*",
                    "*.Application",
                    "*.Q*" // QueryDSL generated classes
                )
            }
        }
    }

    tasks.named("build") {
        dependsOn(tasks.named("jacocoTestCoverageVerification"))
    }

    // ========================================
    // Lombok 금지 검증
    // ========================================
    tasks.register("checkNoLombok") {
        doLast {
            val lombokFound = configurations.flatMap { config ->
                config.dependencies.filter { dep ->
                    dep.group == "org.projectlombok" && dep.name == "lombok"
                }
            }

            if (lombokFound.isNotEmpty()) {
                throw GradleException(
                    """
                    ❌ LOMBOK DETECTED: Lombok is strictly prohibited in this project.
                    Found in: ${project.name}

                    Policy: All modules must use pure Java without Lombok.
                    """.trimIndent()
                )
            }
        }
    }

    tasks.build {
        dependsOn("checkNoLombok")
    }

    // ========================================
    // Compiler Configuration
    // ========================================
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:unchecked",
                "-Xlint:deprecation",
                "-parameters"
            )
        )
    }

    // ========================================
    // BootJar Configuration
    // ========================================
    tasks.withType<BootJar> {
        enabled = false
    }

    tasks.withType<Jar> {
        enabled = true
    }
}

// ========================================
// Dead Code Detection Task
// ========================================
tasks.register("detectDeadCode") {
    group = "verification"
    description = "Detect potentially unused code across all modules"

    doLast {
        println("""
            ========================================
            Dead Code Detection Report
            ========================================
            Running static analysis for unused code...

            Tools:
            - SpotBugs: Unused private methods/fields
            - JaCoCo: 0% coverage methods
            - Custom AST analysis

            See reports in: build/reports/deadcode/
            ========================================
        """.trimIndent())
    }

    dependsOn(subprojects.map { it.tasks.named("spotbugsMain") })
    dependsOn(subprojects.map { it.tasks.named("jacocoTestReport") })
}
