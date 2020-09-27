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
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://repo1.maven.org/maven2/org/bouncycastle")
}
dependencies {
    implementation("io.ktor:ktor-jackson:1.4.0")
    implementation("io.ktor:ktor-network:1.4.0")
    implementation("io.ktor:ktor-server-jetty:1.4.0")
    implementation("io.ktor:ktor-html-builder:1.4.0")
    implementation("io.ktor:ktor-client-apache:1.4.0")
    implementation("io.ktor:ktor-client-jackson:1.4.0")
    implementation("io.ktor:ktor-client-logging:1.4.0")
    implementation("org.bouncycastle:bcprov-jdk14:1.65")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
    implementation("com.github.Steveice10:MCProtocolLib:976c2d0f89")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC2")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "14"
}
application {
    mainClassName = "ServerKt"
}