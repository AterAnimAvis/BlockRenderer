package extensions

import java.io.File

//----------------------------------------------------------------------------------------------------------------------

fun File.ensureParentDirectoriesExist(): File {
    parentFile.ensureExists()

    return this
}

fun File.ensureExists(): File {
    if (!exists()) mkdirs()

    return this
}

//----------------------------------------------------------------------------------------------------------------------