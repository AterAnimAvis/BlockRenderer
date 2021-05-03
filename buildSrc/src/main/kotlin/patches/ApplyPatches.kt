package patches

import codechicken.diffpatch.cli.PatchOperation
import codechicken.diffpatch.util.LoggingOutputStream
import codechicken.diffpatch.util.PatchMode
import extensions.ensureParentDirectoriesExist
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

open class ApplyPatches : DefaultTask() {

    @Internal
    val debug = false

    @InputDirectory
    lateinit var source: File

    @OutputDirectory
    lateinit var target: File

    @InputDirectory
    lateinit var patches: File

    @TaskAction
    @Throws(IOException::class)
    open fun apply() {
        target.ensureParentDirectoriesExist()

        PatchOperation
                .builder()
                .basePath(source.toPath())
                .outputPath(target.toPath())
                .patchesPath(patches.toPath())
                .logTo(LoggingOutputStream(project.logger, LogLevel.LIFECYCLE))
                .mode(PatchMode.ACCESS)
                .verbose(debug)
                .summary(true)
                .build()
                .operate()
                .throwOnError()
    }
}