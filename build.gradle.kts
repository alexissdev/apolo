plugins {
    id("io.freefair.lombok") version "8.13.1" apply false
    id("com.gradleup.shadow") version "8.3.6" apply false
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/central/")
        maven("https://repo.md-5.net/content/repositories/snapshots/")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://repo.md-5.net/content/groups/public/")
        maven("https://jitpack.io")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        "compileOnly"("org.slf4j:slf4j-api:1.7.36")
    }
}