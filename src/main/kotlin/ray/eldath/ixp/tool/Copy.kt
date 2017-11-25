@file:JvmName("FileUtils")

package ray.eldath.ixp.tool

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun copyFileToDirectory(sourceFile: Path, targetDir: Path, fileList: List<Path> = emptyList()) {
	if (!fileList.isEmpty()) {
		if (!fileList.contains(sourceFile))
			Files.copy(sourceFile, Paths.get("$targetDir/${sourceFile.fileName}"))
	} else
		Files.copy(sourceFile, Paths.get("$targetDir/${sourceFile.fileName}"))
}

fun Path.openOrCreateDirectories(err: String = "") = apply {
	println("${err}Directory for `$fileName` does not exist, created.")
	if (!Files.exists(this)) Files.createDirectories(this)
}

fun Path.openOrCreateFile() = apply {
	if (!Files.exists(this)) Files.createFile(this)
}
