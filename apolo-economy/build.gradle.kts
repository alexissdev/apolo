plugins {
    id("java")
    id("io.freefair.lombok")
}

dependencies {
    implementation(project(":apolo-api"))
    implementation(project(":apolo-core"))
    implementation(project(":apolo-database"))
    implementation(project(":apolo-messaging"))
    implementation(project(":apolo-redis"))
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.google.inject:guice:7.0.0")
}
