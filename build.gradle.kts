plugins {
    id("java")
    id("java-library")
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "net.pwing.itemattributes"
version = "1.0.0"

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
}

tasks {
    shadowJar {
        from("src/main/java/resources") {
            include("*")
        }

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
