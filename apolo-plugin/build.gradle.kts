plugins {
    id("java")
    id("io.freefair.lombok")
    id("com.gradleup.shadow")
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
        register("gamemode") {
            aliases = listOf("gm")
            description = "Change your own or another player's game mode."
            usage = "/<command> <survival|creative|adventure|spectator> [player]"
            permission = "apolo.gamemode"
        }
        register("tpa") {
            description = "Send a teleport request to another player."
            usage = "/<command> <player>"
            permission = "apolo.tpa"
        }
        register("tpaccept") {
            description = "Accept the pending TPA request."
            usage = "/<command>"
        }
        register("tpadeny") {
            description = "Deny the pending TPA request."
            usage = "/<command>"
        }
        register("tphere") {
            description = "Request another player to teleport to your location."
            usage = "/<command> <player>"
            permission = "apolo.tphere"
        }
        register("warp") {
            description = "Teleport to an existing warp."
            usage = "/<command> <name>"
            permission = "apolo.warp"
        }
        register("setwarp") {
            description = "Create a warp at your current location."
            usage = "/<command> <name>"
            permission = "apolo.warp.set"
        }
        register("delwarp") {
            description = "Delete an existing warp."
            usage = "/<command> <name>"
            permission = "apolo.warp.delete"
        }
        register("warps") {
            description = "List all available warps with pagination."
            usage = "/<command> [page]"
            permission = "apolo.warp"
        }
        register("fly") {
            description = "Toggle flight for yourself or another player."
            usage = "/<command> [player]"
            permission = "apolo.fly"
        }
        register("godmode") {
            aliases = listOf("god")
            description = "Toggle god mode for yourself or another player."
            usage = "/<command> [player]"
            permission = "apolo.godmode"
        }
        register("repair") {
            description = "Repair the item in hand, equipped armor, or both."
            usage = "/<command> [hand|armor|all]"
            permission = "apolo.repair"
        }
        register("balance") {
            aliases = listOf("bal", "money")
            description = "Show your own or another player's balance."
            usage = "/<command> [player]"
        }
        register("pay") {
            aliases = listOf("transfer")
            description = "Transfer money to another player."
            usage = "/<command> <player> <amount>"
        }
        register("eco") {
            aliases = listOf("economy")
            description = "Manage a player's balance (set/give/take). Admin only."
            usage = "/<command> <set|give|take> <player> <amount>"
            permission = "apolo.eco"
        }
        register("baltop") {
            aliases = listOf("balancetop")
            description = "Show the top players ranked by balance."
            usage = "/<command>"
        }
        register("msg") {
            aliases = listOf("message", "tell", "whisper")
            description = "Send a private message to a player (cross-server)."
            usage = "/<command> <player> <message>"
        }
        register("reply") {
            aliases = listOf("r")
            description = "Reply to the last private message received."
            usage = "/<command> <message>"
        }
        register("socialspy") {
            aliases = listOf("ss")
            description = "Toggle spying on private messages between players."
            usage = "/<command>"
            permission = "apolo.socialspy"
        }
        register("commandspy") {
            aliases = listOf("cs")
            description = "Toggle spying on commands executed across the network."
            usage = "/<command>"
            permission = "apolo.commandspy"
        }
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
        mergeServiceFiles()
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/INDEX.LIST")
        duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.EXCLUDE
        relocate("com.google.inject", "dev.apolo.shaded.guice")
        relocate("com.google.common", "dev.apolo.shaded.guava")
        relocate("com.google.thirdparty", "dev.apolo.shaded.guava.thirdparty")
        relocate("redis.clients.jedis", "dev.apolo.shaded.jedis")
        relocate("com.google.gson", "dev.apolo.shaded.gson")
        relocate("org.mongodb", "dev.apolo.shaded.mongodb")
    }
    build {
        dependsOn(shadowJar)
    }
}
