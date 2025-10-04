plugins {
    id("java")
    id("java-library")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.0"
    id("com.modrinth.minotaur") version "2.+"
}

val supportedVersions = listOf(
    "1.21.3", "1.21.4", "1.21.5", "1.21.6", "1.21.7", "1.21.8",
    "1.21.9"
)

group = "net.pwing.itemattributes"
version = "1.0.0-SNAPSHOT"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://jitpack.io/")
    maven("https://repo.oraxen.com/releases")
    maven("https://mvn.lumine.io/repository/maven-public/") {
        metadataSources {
            artifact()
        }
    }
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.adventure.platform.bukkit)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.adventure.text.serializer.plain)
    compileOnly(libs.magicspells) {
        isTransitive = false
    }
    compileOnly(libs.oraxen)
    compileOnly(libs.mythic)
    compileOnly(libs.mythiclib)
    compileOnly(libs.itemsadder)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.nexo)

    api(libs.configlib)
    api(libs.exp4j)

    implementation(libs.bstats.bukkit)
}

tasks {
    shadowJar {
        from("src/main/java/resources") {
            include("*")
        }

        relocate("org.bstats", "net.pwing.itemattributes.shaded.bstats")

        archiveClassifier.set("")
    }

    jar {
        archiveClassifier.set("unshaded")
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        minecraftVersion("1.21.4")
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand("version" to rootProject.version)
        }
    }
}

modrinth {
    val snapshot = "SNAPSHOT" in rootProject.version.toString()

    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set("item-attributes")
    versionNumber.set(rootProject.version as String + if (snapshot) "-" + System.getenv("BUILD_NUMBER") else "")
    versionType.set(if (snapshot) "beta" else "release")
    changelog.set(System.getenv("CHANGELOG") ?: "")
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(supportedVersions)
}
