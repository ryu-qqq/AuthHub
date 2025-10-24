rootProject.name = "AuthHub"

include(
    "domain",
    "application",
    "adapter-in:rest-api",
    "adapter-out:persistence-mysql",
    // "adapter-out:persistence-redis",  // bootstrap에 통합됨 (Netty 의존성 충돌 방지)
    "bootstrap"
)
