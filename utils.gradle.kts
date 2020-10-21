/* Based on: https://stackoverflow.com/a/40091450 */
tasks.create("generatePackageInfos") {
    group = "utility"
    description = "Generates package-info.java for all directories"

    // Never UP-TO-DATE
    outputs.upToDateWhen { false }

    doLast {
        val sourceDirectory = file(_Paths.get(projectDir.absolutePath, "src", "main", "java"))

        recurseDirectories(listOf(sourceDirectory)) { directory ->
            // If file contains java source files
            if (directory.list { _, name -> name.endsWith(".java") }!!.isEmpty()) return@recurseDirectories
            println("Visiting: $directory")

            val packageInfo = File(directory, "package-info.java")
            // And package-info.java doesn't exist
            if (packageInfo.exists()) return@recurseDirectories

            // Create package-info.java
            println("Creating: $packageInfo")
            _Files.newBufferedWriter(packageInfo.toPath()).use {
                it.write(generatePackageInfo(getPackageName(sourceDirectory, directory)))
            }
        }
    }
}

fun generatePackageInfo(packageName: String) =
        """|@NonnullDefault
           |package $packageName;
           |
           |import org.lwjgl.system.NonnullDefault;
        """.trimMargin("|")

typealias _Files = java.nio.file.Files
typealias _Paths = java.nio.file.Paths

tailrec fun recurseDirectories(directories: List<File>, callback: (File) -> Unit) {
    val next = mutableListOf<File>()
    directories.forEach { directory ->
        directory.listFiles()!!.forEach directory@{
            if (!it.isDirectory) return@directory
            callback(it)
            next += it
        }
    }

    if (next.isEmpty()) return

    recurseDirectories(next, callback)
}

fun getPackageName(root: File, current: File): String {
    val src = root.absolutePath
    val cur = current.absolutePath

    val prefix = cur.indexOf(src)
    val result = cur.substring(prefix + src.length).replace("\\", "/")
    return result.replace("/", ".").removePrefix(".")
}