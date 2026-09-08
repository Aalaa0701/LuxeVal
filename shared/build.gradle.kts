import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.kotlinSerialization)
}

/* -------------------------------------------------------------------------- */
/* Secrets                                                                    */
/* -------------------------------------------------------------------------- */



abstract class GenerateSecretsTask : DefaultTask() {

    @get:Input
    abstract val metalPriceApiKey: org.gradle.api.provider.Property<String>

    @get:Input
    abstract val supabaseUrl: org.gradle.api.provider.Property<String>

    @get:Input
    abstract val supabaseApiKey: org.gradle.api.provider.Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val dir = outputDirectory
            .dir("com/example/luxeval/config")
            .get()
            .asFile

        dir.mkdirs()

        File(dir, "GeneratedSecrets.kt").writeText(
            """
            package com.example.luxeval.config

            internal object GeneratedSecrets {
                const val METAL_PRICE_API_KEY = "${metalPriceApiKey.get()}"
                const val SUPABASE_URL = "${supabaseUrl.get()}"
                const val SUPABASE_API_KEY = "${supabaseApiKey.get()}"
            }
            """.trimIndent()
        )
    }
}

val secretsFile = rootProject.file("local.properties")

val secretsProperties = Properties().apply {
    if (secretsFile.exists()) {
        load(secretsFile.inputStream())
    }
}

fun secret(key: String): String =
    secretsProperties.getProperty(key)
        ?.takeIf { it.isNotBlank() }
        ?: System.getenv(key)
        ?: error(
            "Missing secret '$key' in local.properties or environment variables"
        )

val metalPriceApiKeyValue = secret("METAL_PRICE_API_KEY")
val supabaseUrlValue = secret("SUPABASE_URL")
val supabaseApiKeyValue = secret("SUPABASE_API_KEY")

val generatedSecretsDir =
    layout.buildDirectory
        .dir("generated/secrets/commonMain/kotlin")

val generateSecrets = tasks.register<GenerateSecretsTask>("generateSecrets") {
    metalPriceApiKey.set(metalPriceApiKeyValue)
    supabaseUrl.set(supabaseUrlValue)
    supabaseApiKey.set(supabaseApiKeyValue)

    outputDirectory.set(generatedSecretsDir)
}

tasks.matching {
    it.name.contains("Kotlin", ignoreCase = true) ||
            it.name.contains("Ksp", ignoreCase = true)
}.configureEach {
    dependsOn(generateSecrets)
}
kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    android {
       namespace = "com.example.luxeval.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
       withDeviceTestBuilder {
           sourceSetTreeName = "test"
       }.configure {
           instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
       }
    }
    
    sourceSets {

        commonMain{
            kotlin.srcDir(generatedSecretsDir)
            dependencies {
                // shared/build.gradle.kts — commonMain.dependencies
                implementation(libs.supabase.auth)
                implementation(libs.supabase.postgrest)
                implementation(libs.supabase.storage)
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
                implementation(libs.kotlinx.datetime)
                implementation(libs.navigation.compose)
                implementation(libs.kotlinx.serialization.json)
                implementation("com.russhwolf:multiplatform-settings:1.1.1")
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
                implementation(libs.androidx.room.runtime)
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation(libs.sqlite)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization)
                implementation(libs.ktor.client.logging)
                implementation(libs.coroutines)
            }

        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
room {
    schemaDirectory("$projectDir/schemas")
}
dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    androidRuntimeClasspath(libs.compose.uiTooling)
}