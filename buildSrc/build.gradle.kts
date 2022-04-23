plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    maven {
        name = "MinecraftForge Maven"
        url = uri("https://maven.minecraftforge.net/")
    }
}

dependencies {
    implementation("net.minecraftforge:DiffPatch:2.0.5:all")
}