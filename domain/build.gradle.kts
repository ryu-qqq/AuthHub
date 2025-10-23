plugins {
    java
}

dependencies {
    // Domain layer는 외부 의존성 최소화 (순수 Java)
    // 필요 시 Jakarta Validation만 허용
    compileOnly(libs.jakarta.validation.api)
}

description = "AuthHub Domain Layer - Pure business logic and domain models"
