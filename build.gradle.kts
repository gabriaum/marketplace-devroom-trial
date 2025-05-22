import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.gabriaum.afterjournal"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()

    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshot")
    maven("https://libraries.minecraft.net")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigot"
        content {
            includeGroup("org.spigotmc")
            includeGroup("com.destroystokyo.paper")
        }
    }
}

val lombokVersion = "1.18.30"
val spigotVersion = "1.21.4-R0.1-SNAPSHOT"
dependencies {
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    compileOnly("org.spigotmc:spigot-api:$spigotVersion")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("de.tr7zw:item-nbt-api:2.15.0")
    implementation("org.mongodb:mongo-java-driver:3.12.14")
    implementation("me.devnatan:inventory-framework-platform-bukkit:3.3.9")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

bukkit {
    name = "DevRoom-Trial"
    version = gitVersion()
    author = "gabriaum"
    main = "com.gabriaum.devroom.MarketMain"
    apiVersion = "1.21.4"
    depend = listOf("Vault")
}

tasks.shadowJar {
    relocate("de.tr7zw.changeme.nbtapi", "br.com.centralcart.libs.nbtapi")
    archiveFileName.set("DevRoom-Trial.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--release", "8"))
}

tasks.register<JavaCompile>("compileJava8") {
    source = sourceSets["main"].java
    destinationDir = file("$buildDir/classes/java/main")
    classpath = sourceSets["main"].compileClasspath
    options.release.set(8)
}

tasks.register<JavaCompile>("compileJava17") {
    source = sourceSets["main"].java
    destinationDir = file("$buildDir/classes/java/main-17")
    classpath = sourceSets["main"].compileClasspath
    options.release.set(17)
}

tasks.register<JavaCompile>("compileJava24") {
    source = sourceSets["main"].java
    destinationDir = file("$buildDir/classes/java/main-24")
    classpath = sourceSets["main"].compileClasspath
    options.release.set(24)
}

fun gitVersion(): String {
    val commitId = "git rev-parse --short HEAD".runCommand().trim()
    val branch = "git rev-parse --abbrev-ref HEAD".runCommand().trim()
    val date = SimpleDateFormat("dd/MM/yyyy 'at' HH:mm").format(Date())
    return "$commitId from $branch ($date)"
}

fun String.runCommand(): String {
    return ProcessBuilder(*split(" ").toTypedArray())
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
}