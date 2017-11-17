import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

object CopyTest {

	@JvmStatic
	fun main(vararg args: String) {
		val sourceDir = Paths.get("G:\\test")
		val targetDir = Paths.get("G:\\target")
		if (!Files.exists(targetDir))
			Files.createDirectories(targetDir)

		val target1 = Paths.get(targetDir.toString() + "/1")
		if (!Files.exists(target1))
			Files.createDirectories(target1)
		val target2 = Paths.get(targetDir.toString() + "/2")
		if (!Files.exists(target2))
			Files.createDirectories(target2)

		val files = Files.list(sourceDir).collect(Collectors.toList())

		val start = System.currentTimeMillis()

		println("In `files` way...")
		for (file in files)
			copyFileToDirectoryByFiles(file, target2)
		println("\t Finished. Used time: ${System.currentTimeMillis() - start} ms.")

		println("In `channel` way...")
		for (file in files)
			copyFileToDirectoryByChannel(file, target1)
		println("\t Finished. Used time: ${System.currentTimeMillis() - start} ms.")
	}

	private fun copyFileToDirectoryByChannel(sourceFile: Path, targetDir: Path) {
		val target = Paths.get(targetDir.toString() + "/${sourceFile.fileName}")
		if (!Files.exists(target))
			Files.createFile(target)

		val input = FileInputStream(sourceFile.toFile()).channel
		val output = FileOutputStream(target.toFile()).channel
		output.transferFrom(input, 0, input.size())
		input.close()
		output.close()
	}

	private fun copyFileToDirectoryByFiles(sourceFile: Path, targetDir: Path) {
		Files.copy(sourceFile, Paths.get(targetDir.toString() + "/${sourceFile.fileName}"))
	}
}