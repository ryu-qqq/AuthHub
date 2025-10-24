rootProject.name = "AuthHub"

include(
    "domain",
    "application",
    "adapter-in:rest-api",
    "adapter-out:persistence-mysql",
    "adapter-out:persistence-redis",
    "bootstrap"
)
