val modId: String by extra
val modVersion: String by extra
val minecraftVersion: String by extra

tasks.create("merge") {
    group = "merge"

    dependsOn(":common:jar", ":forge:jar", ":fabric:remapJar")

    val forge = project(":forge").tasks["jar"].outputs.files.singleFile
    val fabric = project(":fabric").tasks["remapJar"].outputs.files.singleFile
    val common = project(":common").tasks["jar"].outputs.files.singleFile
    val output = "build/libs/$modId-$minecraftVersion-$modVersion.jar"

    inputs.files("merging.policy", forge, fabric, common)
    outputs.file(output)

    doLast {
        val policies = mutableMapOf<String, String>()

        file("merging.policy").forEachLine {
            if (it.isBlank() || it.startsWith("#")) return@forEachLine
            val env = it.substring(0, it.indexOf(' '))
            if (env !in listOf("COMMON", "FABRIC", "FORGE")) {
                throw IllegalStateException("Illegal env $env at $it")
            }
            policies[it.substring(env.length + 1)] = env
        }

        ZipOutputStream(FileOutputStream(output)).use {
            mergeZipFiles(policies, it, ZipFile(forge), ZipFile(fabric), ZipFile(common))
        }
    }
}


fun mergeZipFiles(
        policies: Map<String, String>,
        output: ZipOutputStream,
        forge: ZipFile,
        fabric: ZipFile,
        common: ZipFile
) {
    val entries = mutableMapOf<String, Pair<ZipFile, ZipEntry>>()

    common.entries().iterator().forEachRemaining {
        entries[it.name] = common to it
    }

    forge.entries().iterator().forEachRemaining {
        if (entries.containsKey(it.name) && !it.isDirectory) {
            val policyEnv = policies[it.name] ?: throw IllegalStateException("Unhandled 'FORGE' duplicate file: ${it.name}")
            println("Found Duplicate between 'COMMON' and 'FORGE' for '${it.name}' chose $policyEnv")
            if (policyEnv != "FORGE") return@forEachRemaining
        }

        entries[it.name] = forge to it
    }

    fabric.entries().iterator().forEachRemaining {
        if (entries.containsKey(it.name) && !it.isDirectory) {
            val policyEnv = policies[it.name] ?: throw IllegalStateException("Unhandled 'FABRIC' duplicate file: ${it.name}")
            val source = if (entries[it.name] == common) "COMMON" else "FORGE"
            println("Found Duplicate between '$source' and 'FABRIC' for '${it.name}' chose $policyEnv")
            if (policyEnv != "FABRIC") return@forEachRemaining
        }

        entries[it.name] = fabric to it
    }

    entries.forEach { _, (source, entry) ->
        output.putNextEntry(entry)
        if (!entry.isDirectory) source.getInputStream(entry).copyTo(output, 1024)
        output.closeEntry()
    }
}

typealias FileOutputStream = java.io.FileOutputStream

typealias Enumeration<T> = java.util.Enumeration<T>
typealias ZipFile = java.util.zip.ZipFile
typealias ZipEntry = java.util.zip.ZipEntry
typealias ZipOutputStream = java.util.zip.ZipOutputStream