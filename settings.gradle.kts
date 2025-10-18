pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "TikTokDemo"
include(":app")
include(":common:theme")
include(":common:composable")
include(":core")
include(":data")
include(":domain")
include(":feature:home")
include(":feature:commentlisting")
include(":feature:creatorprofile")
include(":feature:inbox")
include(":feature:authentication")
include(":feature:loginwithemailphone")
include(":feature:friends")
include(":feature:myprofile")
include(":feature:setting")
include(":feature:cameramedia")
