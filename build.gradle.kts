import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
    kotlin("plugin.serialization") version "1.4.10"
}
group = "dev.wnuke"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}
dependencies {
    implementation("io.ktor:ktor-jackson:1.4.0")
    implementation("io.ktor:ktor-server-netty:1.4.0")
    implementation("io.ktor:ktor-html-builder:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "14"
}
application {
    mainClassName = "ServerKt"
}