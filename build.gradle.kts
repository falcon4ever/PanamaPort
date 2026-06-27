plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    // Honor a -Pversion override (e.g. JitPack injects the git ref as the
    // version); fall back to the fixed fork version for local publishing.
    version = providers.gradleProperty("version").getOrElse("0.1.3-kanama-r8.1")
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            configure<com.android.build.api.dsl.LibraryExtension> {
                enableKotlin = false

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
