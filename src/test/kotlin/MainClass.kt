import java.nio.file.Files
import java.nio.file.Paths


fun main(vararg args: String) {
	val source = Paths.get("G:\\library.xml")
	val target = Paths.get("G:\\target\\library.xml")

	if (!Files.exists(target))
		Files.copy(source, target)
}
