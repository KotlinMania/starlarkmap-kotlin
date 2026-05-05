import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("com.android.kotlin.multiplatform.library") version "9.2.0"
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "io.github.kotlinmania"
version = "0.1.2"

val androidSdkDir: String? =
    providers.environmentVariable("ANDROID_SDK_ROOT").orNull
        ?: providers.environmentVariable("ANDROID_HOME").orNull

if (androidSdkDir != null && file(androidSdkDir).exists()) {
    val localProperties = rootProject.file("local.properties")
    if (!localProperties.exists()) {
        val sdkDirPropertyValue = file(androidSdkDir).absolutePath.replace("\\", "/")
        localProperties.writeText("sdk.dir=$sdkDirPropertyValue")
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    sourceSets.all {
        languageSettings.optIn("kotlin.time.ExperimentalTime")
        languageSettings.optIn("kotlin.concurrent.atomics.ExperimentalAtomicApi")
    }

    compilerOptions {
        allWarningsAsErrors.set(true)
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    val xcf = XCFramework("StarlarkMap")

    macosArm64 {
        binaries.framework {
            baseName = "StarlarkMap"
            xcf.add(this)
        }
    }
    macosX64 {
        binaries.framework {
            baseName = "StarlarkMap"
            xcf.add(this)
        }
    }
    linuxX64()
    mingwX64()
    iosArm64 {
        binaries.framework {
            baseName = "StarlarkMap"
            xcf.add(this)
        }
    }
    iosX64 {
        binaries.framework {
            baseName = "StarlarkMap"
            xcf.add(this)
        }
    }
    iosSimulatorArm64 {
        binaries.framework {
            baseName = "StarlarkMap"
            xcf.add(this)
        }
    }
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    swiftExport {
        moduleName = "StarlarkMap"
        flattenPackage = "io.github.kotlinmania.starlarkmap"
    }

    android {
        namespace = "io.github.kotlinmania.starlarkmap"
        compileSdk = 34
        minSdk = 24
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
            }
        }

        val commonTest by getting { dependencies { implementation(kotlin("test")) } }
    }
    jvmToolchain(21)
}

rootProject.extensions.configure<YarnRootExtension>("kotlinYarn") {
    resolution("diff", "8.0.3")
    resolution("serialize-javascript", "7.0.5")
    resolution("webpack", "5.106.2")
    resolution("follow-redirects", "1.16.0")
    resolution("lodash", "4.18.1")
    resolution("ajv", "8.20.0")
    resolution("brace-expansion", "5.0.5")
    resolution("flatted", "3.4.2")
    resolution("minimatch", "10.2.5")
    resolution("picomatch", "4.0.4")
    resolution("qs", "6.15.1")
    resolution("socket.io-parser", "4.2.6")
}


mavenPublishing {
    publishToMavenCentral()
    val signingConfigured =
        providers.gradleProperty("signingInMemoryKey").isPresent ||
            providers.gradleProperty("signing.keyId").isPresent ||
            providers.environmentVariable("SIGNING_KEY").isPresent
    if (signingConfigured) {
        signAllPublications()
    }

    coordinates(group.toString(), "starlarkmap-kotlin", version.toString())

    pom {
        name.set("starlarkmap-kotlin")
        description.set("Kotlin Multiplatform port of facebook/starlark-rust's starlark_map crate - Map implementations with starlark-specific optimizations")
        inceptionYear.set("2026")
        url.set("https://github.com/KotlinMania/starlarkmap-kotlin")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("sydneyrenee")
                name.set("Sydney Renee")
                email.set("sydney@solace.ofharmony.ai")
                url.set("https://github.com/sydneyrenee")
            }
        }

        scm {
            url.set("https://github.com/KotlinMania/starlarkmap-kotlin")
            connection.set("scm:git:git://github.com/KotlinMania/starlarkmap-kotlin.git")
            developerConnection.set("scm:git:ssh://github.com/KotlinMania/starlarkmap-kotlin.git")
        }
    }
}
