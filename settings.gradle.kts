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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "FitnessApp"

include(":app")
include(":core:common")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:security")
include(":core:testing")

include(":feature:nutrition")
include(":feature:workout")
include(":feature:health")
include(":feature:profile")
include(":feature:sync")
include(":feature:onboarding")

include(":testing")