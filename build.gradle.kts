/*================================================================================================== BuildScript ==== */
buildscript {
    repositories {
        maven(url = "https://files.minecraftforge.net/maven")
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "3.+") {
            isChanging = true
        }
    }
}

/*========================================================================================== Config -> Minecraft ==== */
val forgeVersion: String by extra
val mappingsChannel: String by extra
val mappingsVersion: String by extra
val minecraftVersion: String by extra

/*================================================================================================ Config -> Mod ==== */
val modId: String by extra
val modVersion: String by extra
val modGroup: String by extra
val vendor: String by extra

/*========================================================================================= Config -> Run Config ==== */
val level: String by extra
val markers: String by extra

/*======================================================================================= Config -> Dependencies ==== */
val jetbrainsAnnotationVersion: String by extra

/*================================================================================== Config -> Test Dependencies ==== */
val junitVersion: String by extra

/*====================================================================================================== Plugins ==== */
plugins {
    `java-library`
}

apply(plugin = "net.minecraftforge.gradle")

/*========================================================================================= Minecraft Dependency ==== */

/* Note: Due to the way kotlin gradle works we need to define the minecraft dependency before we configure Minecraft */
dependencies {
    "minecraft"(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-$forgeVersion")
}

/*==================================================================================================== Minecraft ==== */

minecraft {
    mappingChannel = mappingsChannel
    mappingVersion = mappingsVersion

    runs {
        config("client")
        config("server")

//        config("data") {
//            args("--mod", modId, "--all", "--output", file("src/generated/resources/"))
//        }
    }
}

/*======================================================================================================== Setup ==== */
project.group = modGroup
project.version = "$minecraftVersion-$modVersion"

/* Java 8 Target + Parameter Names */
tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
    options.compilerArgs.add("-parameters")
}

/* Finalize the jar by Reobf */
tasks.named<Jar>("jar") {
    finalizedBy("reobfJar")
}

/* Manifest */
tasks.withType<Jar> {
    manifest {
        attributes(
                "Specification-Title" to modId,
                "Specification-Vendor" to vendor,
                "Specification-Version" to modVersion,
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to vendor,
                "Implementation-Timestamp" to Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
        )
    }
}

/* Generate Package Infos */
apply(from = "utils.gradle.kts")

/*================================================================================================= Dependencies ==== */

dependencies {
    /* JUnit 5 */
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = junitVersion)

    /* Jetbrains Annotations */
    implementation(group = "org.jetbrains", name = "annotations", version = jetbrainsAnnotationVersion)
}

/*==================================================================================================== Utilities ==== */

typealias Date = java.util.Date
typealias SimpleDateFormat = java.text.SimpleDateFormat

fun Date.format(format: String) = SimpleDateFormat(format).format(this)

typealias RunConfig = net.minecraftforge.gradle.common.util.RunConfig
typealias UserDevExtension = net.minecraftforge.gradle.userdev.UserDevExtension

typealias RunConfiguration = RunConfig.() -> Unit

fun minecraft(configuration: UserDevExtension.() -> Unit) =
        configuration(extensions.getByName("minecraft") as UserDevExtension)

fun NamedDomainObjectContainerScope<RunConfig>.config(name: String, additionalConfiguration: RunConfiguration = {}) {
    val runDirectory = project.file("run")
    val sourceSet = the<JavaPluginConvention>().sourceSets["main"]

    create(name) {
        workingDirectory(runDirectory)
        property("forge.logging.markers", markers)
        property("forge.logging.console.level", level)
        environment("MOD_VERSION", modVersion)

        additionalConfiguration(this)

        mods { create(modId) { source(sourceSet) } }
    }
}