@file:JvmName("Copy")

package ray.eldath.ixp.tool

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun copyFileToDirectory(sourceFile: Path, targetDir: Path) {
	Files.copy(sourceFile, Paths.get(targetDir.toString() + "/${sourceFile.fileName}"))
}
