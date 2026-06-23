plugins {
    id("java")
    id("io.freefair.lombok")
}

dependencies {
    implementation(project(":apolo-api"))
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}
