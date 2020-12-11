import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
    kotlin("plugin.serialization") version "1.4.10"
}
group = "net.willemml"
version = "0.0.1"

repositories {
    mavenCentral()
    jcenter()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://maven.daporkchop.net")
}
dependencies {
    implementation("com.github.willemml:kt-cmd:v1.6.2")
    implementation("org.slf4j:slf4j-nop:1.7.9")
    implementation("io.ktor:ktor-jackson:1.4.0")
    implementation("io.ktor:ktor-network:1.4.0")
    implementation("io.ktor:ktor-server-jetty:1.4.0")
    implementation("io.ktor:ktor-html-builder:1.4.0")
    implementation("io.ktor:ktor-client-apache:1.4.0")
    implementation("io.ktor:ktor-client-jackson:1.4.0")
    implementation("io.ktor:ktor-client-logging:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
    implementation("com.github.Steveice10:MCProtocolLib:976c2d0f89")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("net.daporkchop.lib:minecraft-text:0.5.5-20201106.114836-37")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0-RC2")
}

val run: JavaExec by tasks.getting(JavaExec::class) {
    standardInput = System.`in`
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}
