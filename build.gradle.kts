import com.palantir.gradle.gitversion.VersionDetails
import java.util.Date

plugins {
    kotlin("jvm") version "2.2.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.palantir.git-version") version "4.1.0"
    application
}

group = "io.github.lmfs.box"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
    implementation("org.fusesource.jansi:jansi:2.4.2")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("org.jline:jline:3.30.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

val versionDetails: groovy.lang.Closure<VersionDetails> by extra

tasks.withType<ProcessResources> {
    val resourceTargets = listOf(
        "META-INF/lmfsbox.properties",
        "META-INF/lmfsbox.license.txt",
        "META-INF/lmfsbox.copyright.txt"
    )
    val replaceProperties = mapOf<String, Any?>(
        "gradle" to gradle,
        "project" to project,
        "date" to Date(),
        "building" to "Gradle v${gradle.gradleVersion} (JavaToolchain ${java.toolchain.languageVersion.get()}) ${System.getProperty("os.arch").uppercase()}",
        "licenseText" to rootProject.file("LICENSE").readText(),
        "copyrightText" to rootProject.file("COPYRIGHT").readText(),
        "gitVersion" to versionDetails()
    )
    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}

application {
    mainClass.set("io.github.lmfs.box.LMFSBoxKt")
}
