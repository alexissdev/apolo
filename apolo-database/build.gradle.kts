plugins {
    id("java")
    id("io.freefair.lombok")
}

dependencies {
    implementation(project(":apolo-api"))
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}
