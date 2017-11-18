@file:JvmName("FileUtils")

package ray.eldath.ixp.tool

import java.nio.file.*

fun copyFileToDirectory(sourceFile: Path, targetDir: Path) {
	Files.copy(sourceFile, Paths.get("$targetDir/${sourceFile.fileName}"))
}

fun Path.openOrCreateDirectories(err: String = "") = apply {
	println("${err}Directory for `$fileName` does not exist, created.")
	if (!Files.exists(this)) Files.createDirectories(this)
}

fun Path.openOrCreateFile() = apply {
	if (!Files.exists(this)) Files.createFile(this)
}
