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
            description = "Cambia el modo de juego propio o de otro jugador."
            usage = "/<command> <survival|creative|adventure|spectator> [jugador]"
            permission = "apolo.gamemode"
        }
        register("tpa") {
            description = "Solicita teletransportarte a la ubicación de otro jugador."
            usage = "/<command> <jugador>"
            permission = "apolo.tpa"
        }
        register("tpaccept") {
            description = "Acepta la solicitud de TPA pendiente."
            usage = "/<command>"
        }
        register("tpadeny") {
            description = "Rechaza la solicitud de TPA pendiente."
            usage = "/<command>"
        }
        register("tphere") {
            description = "Solicita que otro jugador se teletransporte a tu ubicación."
            usage = "/<command> <jugador>"
            permission = "apolo.tphere"
        }
        register("warp") {
            description = "Teletransporta a un warp existente."
            usage = "/<command> <nombre>"
            permission = "apolo.warp"
        }
        register("setwarp") {
            description = "Crea un warp en tu posición actual."
            usage = "/<command> <nombre>"
            permission = "apolo.warp.set"
        }
        register("delwarp") {
            description = "Elimina un warp existente."
            usage = "/<command> <nombre>"
            permission = "apolo.warp.delete"
        }
        register("warps") {
            description = "Lista todos los warps disponibles con paginación."
            usage = "/<command> [página]"
            permission = "apolo.warp"
        }
        register("fly") {
            description = "Activa o desactiva el vuelo propio o de otro jugador."
            usage = "/<command> [jugador]"
            permission = "apolo.fly"
        }
        register("godmode") {
            aliases = listOf("god")
            description = "Activa o desactiva el modo dios propio o de otro jugador."
            usage = "/<command> [jugador]"
            permission = "apolo.godmode"
        }
        register("repair") {
            description = "Repara el ítem en mano, la armadura o ambos."
            usage = "/<command> [hand|armor|all]"
            permission = "apolo.repair"
        }
        register("balance") {
            aliases = listOf("bal", "money")
            description = "Muestra el saldo económico propio o de otro jugador."
            usage = "/<command> [jugador]"
        }
        register("pay") {
            aliases = listOf("transfer")
            description = "Transfiere dinero a otro jugador."
            usage = "/<command> <jugador> <cantidad>"
        }
        register("eco") {
            aliases = listOf("economy")
            description = "Administra el balance económico de los jugadores (set/give/take)."
            usage = "/<command> <set|give|take> <jugador> <cantidad>"
            permission = "apolo.eco"
        }
        register("baltop") {
            aliases = listOf("balancetop")
            description = "Muestra el ranking de jugadores con mayor saldo."
            usage = "/<command>"
        }
        register("msg") {
            aliases = listOf("message", "tell", "whisper")
            description = "Envía un mensaje privado a otro jugador (funciona entre servidores)."
            usage = "/<command> <jugador> <mensaje>"
        }
        register("reply") {
            aliases = listOf("r")
            description = "Responde al último mensaje privado recibido."
            usage = "/<command> <mensaje>"
        }
        register("socialspy") {
            aliases = listOf("ss")
            description = "Activa o desactiva la escucha de mensajes privados de otros jugadores."
            usage = "/<command>"
            permission = "apolo.socialspy"
        }
        register("commandspy") {
            aliases = listOf("cs")
            description = "Activa o desactiva el espionaje de comandos ejecutados en la red."
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
