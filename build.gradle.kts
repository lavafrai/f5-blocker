import java.text.SimpleDateFormat
import java.util.Date

plugins {
    eclipse
    idea
    id("maven-publish")
    id("net.minecraftforge.gradle") version "[6.0.16,6.2)"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.parchmentmc.librarian.forgegradle") version "1.+"
}

repositories {
    maven {
        name = "Kotlin for Forge"
        setUrl("https://thedarkcolour.github.io/KotlinForForge/")
    }

    maven {
        name = "Curse Maven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }

    maven {
        name = "svocraftRepository"
        url = uri("https://reposilite.artembay.ru/releases")
    }
}

val mod_group_id: String by project
val mod_version: String by project
val mod_id: String by project
val mapping_channel: String by project
val mapping_version: String by project

fun getGitCommitHash(): String {
    return try {
        val stdout = providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get()
        stdout.trim()
    } catch (_: Exception) {
        "unknown"
    }
}

group = mod_group_id
version = "$mod_version-${getGitCommitHash()}"

base {
    archivesName.set(mod_id)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

minecraft {
    mappings(mapping_channel, mapping_version)
    copyIdeResources.set(true)

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${project.projectDir}/build/createSrgToMcp/output.srg")

            mods {
                create(mod_id) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            property("forge.enabledGameTestNamespaces", mod_id)
            args("--nogui")
            jvmArg("-Dgeckolib.disable_examples=true")
        }

        create("client") {
            property("forge.enabledGameTestNamespaces", mod_id)
            jvmArg("-Dgeckolib.disable_examples=true")
        }
    }
}

dependencies {
    "minecraft"("net.minecraftforge:forge:${project.property("minecraft_version")}-${project.property("forge_version")}")
    implementation("thedarkcolour:kotlinforforge:4.11.0")

    implementation("ru.lavafrai.svogame:svocraft:1.0-SNAPSHOT-55b1736")
    implementation("ru.lavafrai.svogame.runtime:svoruntime:1.0.0-55b1736")
}

tasks.withType<ProcessResources> {
    val replaceProperties = mapOf(
            "minecraft_version" to project.property("minecraft_version"),
            "minecraft_version_range" to project.property("minecraft_version_range"),
            "forge_version" to project.property("forge_version"),
            "forge_version_range" to project.property("forge_version_range"),
            "loader_version_range" to project.property("loader_version_range"),
            "mod_id" to mod_id,
            "mod_name" to project.property("mod_name"),
            "mod_license" to project.property("mod_license"),
            "mod_version" to mod_version,
            "mod_authors" to project.property("mod_authors"),
            "mod_description" to project.property("mod_description")
    )
    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties)
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Specification-Title" to mod_id,
                "Specification-Vendor" to project.property("mod_authors"),
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to project.property("mod_authors"),
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        ))
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = base.archivesName.get()
            version = project.version.toString()
        }
    }

    repositories {
        maven {
            name = "reposilite"
            url = uri("https://reposilite.artembay.ru/releases")

            credentials {
                username = gradle.extra["reposilite.user"]?.toString() ?: error("reposilite.user is not defined")
                password = gradle.extra["reposilite.token"]?.toString() ?: error("reposilite.token is not defined")
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}