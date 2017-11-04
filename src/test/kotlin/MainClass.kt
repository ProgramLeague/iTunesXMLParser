import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

object MainClass {
	@JvmStatic
	fun main(vararg args: String) {
		val source = Paths.get("G:\\library.xml")
		val targetDir = Paths.get("G:\\target")
		val target = Paths.get(targetDir.toString() + "/${source.fileName}")
		if (!Files.exists(target))
			Files.createFile(target)

		val input = FileInputStream(source.toFile()).channel
		val output = FileOutputStream(target.toFile()).channel
		output.transferFrom(input, 0, input.size())
		input.close()
		output.close()
	}
}