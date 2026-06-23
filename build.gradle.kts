plugins {
    id("io.freefair.lombok") version "8.6" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/central/")
        maven("https://repo.md-5.net/content/repositories/snapshots/")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://jitpack.io")
        maven("https://repo.md-5.net/content/groups/public/")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}