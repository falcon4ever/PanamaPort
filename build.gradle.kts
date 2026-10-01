plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    // Honor a -Pversion override (e.g. JitPack injects the git ref as the
    // version); fall back to the fixed fork version for local publishing.
    version = providers.gradleProperty("version").getOrElse("0.1.5-kanama-r8.1")
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            configure<com.android.build.api.dsl.LibraryExtension> {
                enableKotlin = false

                // Kanama fork: stay on compileSdk 36. AGP writes the library's
                // compileSdk into the AAR metadata as minCompileSdk, and Kanama's
                // consumers (its Android plugin AAR and Godot 4.7's generated
                // Android Gradle project, AGP 8.6.1) compile against android-36,
                // so a 37 here fails their checkAarMetadata. Nothing in the
                // sources needs the API 37 stubs.
                compileSdk {
                    version = release(36)
                }

                defaultConfig {
                    minSdk = 26
                }
            }
            configure<JavaPluginExtension> {
                toolchain.languageVersion = JavaLanguageVersion.of(21)
            }
        }
    }
}
