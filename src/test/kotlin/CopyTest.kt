import ray.eldath.ixp.tool.openOrCreateDirectories
import ray.eldath.ixp.tool.openOrCreateFile
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

object CopyTest {

	@JvmStatic
	fun main(vararg args: String) {
		val sourceDir = Paths.get("G:\\test").openOrCreateDirectories()
		val targetDir = Paths.get("G:\\target").openOrCreateDirectories()

		val target1 = Paths.get(targetDir.toString() + "/1").openOrCreateDirectories()
		val target2 = Paths.get(targetDir.toString() + "/2").openOrCreateDirectories()

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
		val target = Paths.get("$targetDir/${sourceFile.fileName}").openOrCreateFile()

		val input = FileInputStream(sourceFile.toFile()).channel
		val output = FileOutputStream(target.toFile()).channel
		output.transferFrom(input, 0, input.size())
		input.close()
		output.close()
	}

	private fun copyFileToDirectoryByFiles(sourceFile: Path, targetDir: Path) {
		Files.copy(sourceFile, Paths.get("$targetDir/${sourceFile.fileName}"))
	}
}