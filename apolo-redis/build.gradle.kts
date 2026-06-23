plugins {
    id("java")
    id("io.freefair.lombok")
}

dependencies {
    implementation(project(":apolo-api"))
    implementation(project(":apolo-core"))
    implementation("redis.clients:jedis:4.4.3")
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}
