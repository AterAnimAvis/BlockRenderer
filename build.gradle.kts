/*================================================================================================== BuildScript ==== */
buildscript {
    repositories {
        maven(url = "https://files.minecraftforge.net/maven")
        maven(url = "https://maven.fabricmc.net")
        jcenter()
        mavenCentral()
    }

    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "3.+") {
            isChanging = true
        }
        classpath(group = "fabric-loom", name = "fabric-loom.gradle.plugin", version = "0.5.+") {
            isChanging = true
        }
    }
}

/* ========================================================================================= Config -> Minecraft ==== */
val forgeVersion: String by extra
val mappingsChannel: String by extra
val mappingsVersion: String by extra
val minecraftVersion: String by extra
val yarnBuild: String by extra

/* =============================================================================================== Config -> Mod ==== */
val modId: String by extra
val modVersion: String by extra
val modGroup: String by extra
val vendor: String by extra

/* ======================================================================================== Config -> Run Config ==== */
val level: String by extra
val markers: String by extra

/* ====================================================================================== Config -> Dependencies ==== */
val fabricApiVersion: String by extra
val fabricLoaderVersion: String by extra
val log4jApiVersion: String by extra
val lwjglVersion: String by extra
val jetbrainsAnnotationVersion: String by extra

/* ================================================================================= Config -> Test Dependencies ==== */
val junitVersion: String by extra

plugins {
    id("uk.jamierocks.propatcher") version "1.3.2" apply false
}

subprojects {
    /* ================================================================================================= Plugins ==== */
    apply(plugin = "java")
    apply(plugin = "java-library")

    /* =================================================================================================== Setup ==== */

    project.group = modGroup
    project.version = "$minecraftVersion-$modVersion"

    /* Java 8 Target + Parameter Names */
    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.compilerArgs.add("-parameters")
    }

    /* Generate Package Infos */
    apply(from = "$rootDir/utils.gradle.kts")

    /* ============================================================================================ Dependencies ==== */

    repositories {
        jcenter()
        mavenCentral()
    }

    dependencies {
        /* JUnit 5 */
        "testImplementation"(group = "org.junit.jupiter", name = "junit-jupiter", version = junitVersion)

        /* Jetbrains Annotations */
        "implementation"(group = "org.jetbrains", name = "annotations", version = jetbrainsAnnotationVersion)

        if (project.name in listOf("forge", "fabric")) {
            /* Common */
            "implementation"(project(":common"))
        } else {
            /* Dependencies for Common */

            /* Logging */
            "implementation"(group = "org.apache.logging.log4j", name = "log4j-api", version = log4jApiVersion)

            /* LWJGL / OpenGL */
            "implementation"(group = "org.lwjgl", name = "lwjgl", version = lwjglVersion)
            "implementation"(group = "org.lwjgl", name = "lwjgl-opengl", version = lwjglVersion)
        }
    }
}

/* ======================================================================================================= Forge ==== */

project(":forge") {
    /* ================================================================================================= Plugins ==== */

    apply(plugin = "net.minecraftforge.gradle")

    /* ==================================================================================== Minecraft Dependency ==== */

    /* Note: Due to the way kotlin gradle works we need to define the minecraft dependency before configuration */
    dependencies {
        "minecraft"(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-$forgeVersion")
    }

    /* =============================================================================================== Minecraft ==== */

    minecraft {
        mappingChannel = mappingsChannel
        mappingVersion = mappingsVersion

        runs {
            config("client", project)
            config("server", project)

//        config("data") {
//            args("--mod", modId, "--all", "--output", file("src/generated/resources/"))
//        }
        }
    }

    /* =================================================================================================== Setup ==== */

    tasks.whenTaskAdded {
        if (name.startsWith("prepare")) {
            dependsOn(":common:jar")
        }
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
}

/* ====================================================================================================== Fabric ==== */
project(":fabric") {
    /* ================================================================================================= Plugins ==== */

    apply(plugin = "fabric-loom")

    /* =============================================================================================== Minecraft ==== */

    dependencies {
        /* Via https://modmuss50.me/fabric.html */
        "minecraft"("com.mojang:minecraft:$minecraftVersion")
        "mappings"(project.extensions.getByType<net.fabricmc.loom.LoomGradleExtension>().officialMojangMappings())

        "modImplementation"(group = "net.fabricmc", name = "fabric-loader", version = fabricLoaderVersion)
        "modImplementation"(group = "net.fabricmc.fabric-api", name = "fabric-api", version = fabricApiVersion)
    }

    /* ======================================================================================= Include Resources ==== */

    //TODO: FIX + REMOVE HACK
    if (gradle.startParameter.taskNames.filter { it.startsWith("run") }.any()) {
        println("Detected Fabric Run + Adding Resources")
        val sourceSet = project.the<JavaPluginConvention>().sourceSets["main"]
        val commonSourceSet = project(":common").the<JavaPluginConvention>().sourceSets["main"]
        sourceSet.resources.srcDirs(commonSourceSet.resources.srcDirs)
    }

    /* =================================================================================================== Setup ==== */

    /* Finalize the jar by Reobf */
    tasks.named<Jar>("jar") {
        finalizedBy("remapJar")
    }

    /* Expand version in fabric.mod.json */
    tasks.getByName<ProcessResources>("processResources") {
        inputs.property("version", project.version)

        val sourceSet = project.the<JavaPluginConvention>().sourceSets["main"]

        from(sourceSet.resources.srcDirs) {
            include("fabric.mod.json")
            expand("version" to project.version)
        }

        from(sourceSet.resources.srcDirs) {
            exclude("fabric.mod.json")
        }
    }

}

apply(from = "$rootDir/merge.gradle.kts")

tasks.create("generatePatches") {
    group = "build-tasks"
    dependsOn("generateForge2FabricPatches", "generateFabric2ForgePatches")
}

tasks.create<MakePatchesTask>("generateForge2FabricPatches") {
    group = "build-tasks"

    root = file("forge/src/main/java/com/unascribed/blockrenderer/forge")
    target = file("fabric/src/main/java/com/unascribed/blockrenderer/fabric")
    patches = file("patches/forge2fabric")
}

tasks.create<MakePatchesTask>("generateFabric2ForgePatches") {
    group = "build-tasks"

    root = file("fabric/src/main/java/com/unascribed/blockrenderer/fabric")
    target = file("forge/src/main/java/com/unascribed/blockrenderer/forge")
    patches = file("patches/fabric2forge")
}

/* =================================================================================================== Utilities ==== */

typealias Date = java.util.Date
typealias SimpleDateFormat = java.text.SimpleDateFormat

fun Date.format(format: String) = SimpleDateFormat(format).format(this)

typealias RunConfig = net.minecraftforge.gradle.common.util.RunConfig
typealias UserDevExtension = net.minecraftforge.gradle.userdev.UserDevExtension

typealias RunConfiguration = RunConfig.() -> Unit

fun Project.minecraft(configuration: UserDevExtension.() -> Unit) =
        configuration(this.extensions.getByName("minecraft") as UserDevExtension)

fun NamedDomainObjectContainerScope<RunConfig>.config(name: String, project: Project, additionalConfiguration: RunConfiguration = {}) {
    val runDirectory = project.file("run")
    val sourceSet = project.the<JavaPluginConvention>().sourceSets["main"]
    val commonSourceSet = project(":common").the<JavaPluginConvention>().sourceSets["main"]

    create(name) {
        workingDirectory(runDirectory)
        property("forge.logging.markers", markers)
        property("forge.logging.console.level", level)

        additionalConfiguration(this)

        mods { create(modId) { sources(sourceSet, commonSourceSet) } }
    }
}

typealias MakePatchesTask = uk.jamierocks.propatcher.task.MakePatchesTask