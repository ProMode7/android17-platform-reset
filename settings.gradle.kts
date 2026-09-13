pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android17-platform-reset"

include(":app")
include(":core-design")
include(":feature-appfunctions")
include(":feature-adaptive")
include(":feature-handoff")
include(":feature-npu")
include(":feature-security")
