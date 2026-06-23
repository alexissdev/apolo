plugins {
    id("java")
    id("io.freefair.lombok")
    id("com.github.johnrengelman.shadow")
    id("net.minecrell.plugin-yml.bukkit")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

bukkit {
    name = "Apolo"
    main = "dev.apolo.plugin.ApoloPlugin"
    version = "1.0.0"
    apiVersion = "1.13"
    authors = listOf("TuNombre")
    softDepend = listOf("Vault")
    commands {
        register("gamemode") { aliases = listOf("gm") }
        register("tpa")
        register("tpaccept")
        register("tpadeny")
        register("tphere")
        register("warp")
        register("setwarp")
        register("delwarp")
        register("warps")
        register("fly")
        register("godmode") { aliases = listOf("god") }
        register("repair")
        register("balance") { aliases = listOf("bal", "money") }
        register("pay") { aliases = listOf("transfer") }
        register("eco") { aliases = listOf("economy") }
        register("baltop") { aliases = listOf("balancetop") }
        register("msg") { aliases = listOf("message", "tell", "whisper") }
        register("reply") { aliases = listOf("r") }
        register("socialspy") { aliases = listOf("ss") }
        register("commandspy") { aliases = listOf("cs") }
    }
}

dependencies {
    implementation(project(":apolo-api"))
    implementation(project(":apolo-messaging"))
    implementation(project(":apolo-redis"))
    implementation(project(":apolo-database"))
    implementation(project(":apolo-core"))
    implementation(project(":apolo-economy"))
    implementation(project(":apolo-commands"))
    implementation(project(":apolo-listeners"))
    implementation("com.google.inject:guice:7.0.0")
    implementation("redis.clients:jedis:4.4.3")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("com.google.inject", "dev.apolo.shaded.guice")
        relocate("redis.clients.jedis", "dev.apolo.shaded.jedis")
        relocate("com.google.gson", "dev.apolo.shaded.gson")
        relocate("org.mongodb", "dev.apolo.shaded.mongodb")
    }
    build {
        dependsOn(shadowJar)
    }
}
