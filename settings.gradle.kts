pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        jcenter()
        maven(url= "https://jitpack.io")
        maven(url= "https://maven.google.com")
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven(url= "https://jitpack.io")
        maven(url= "https://maven.google.com")
        mavenCentral()
    }
}

rootProject.name = "PediatricCareAssistant"
include(":app")
 