rootProject.name = "AuthHub"

include(
    "domain",
    "application",
    "adapter-in:rest-api",
    "adapter-out:persistence-mysql",
    "bootstrap"
)
