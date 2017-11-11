package ray.eldath.ixp.tool

import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object Copy {
	fun copyFileToDirectory(sourceFile: Path, targetDir: Path) {
		val target = Paths.get(targetDir.toString() + "/${sourceFile.fileName}")
		if (!Files.exists(target))
			Files.createFile(target)

		val input = FileInputStream(sourceFile.toFile()).channel
		val output = FileOutputStream(target.toFile()).channel
		output.transferFrom(input, 0, input.size())
		input.close()
		output.close()
	}
}