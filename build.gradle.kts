import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val kodeinVersion: String by project

val config4kVersion: String by project

plugins {
    kotlin("jvm") version "1.6.21" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "1.6.21"
    id("io.ktor.plugin") version "2.2.4"
    application
}

ktor {
    fatJar {
        archiveFileName.set("fat_server.jar")
    }
}

group = "com.remcoil"
version = "0.2.0"

application {
    mainClass.set("com.remcoil.ApplicationKt")
}

repositories {
    mavenCentral()
}

tasks.create("stage") {
    dependsOn("installDist")
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")


    implementation("org.jetbrains.kotlin-wrappers:kotlin-css:1.0.0-pre.301-kotlin-1.6.10")


    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")


    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("org.flywaydb:flyway-core:9.10.1")
    implementation("org.postgresql:postgresql:42.5.1")
    implementation("com.zaxxer:HikariCP:5.0.1")


    implementation("io.github.config4k:config4k:$config4kVersion")

    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion")

    implementation(kotlin("stdlib-jdk8"))
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}