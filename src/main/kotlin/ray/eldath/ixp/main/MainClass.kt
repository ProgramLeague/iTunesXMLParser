@file:JvmName("MainClass")

package ray.eldath.ixp.main

import ray.eldath.ixp.tool.Copy
import ray.eldath.ixp.util.Constants
import java.nio.file.*
import java.util.*
import kotlin.collections.ArrayList

fun main(args: Array<String>) {
	val scanner = Scanner(System.`in`)
	println("iTunesXMLParser - v${Constants.VERSION}")

	println("Please input the location of the XML file you want to parse: ")
	val sourceFile = inputValidPath(scanner, { path -> Files.exists(path) && path.toString().endsWith(".xml") })

	println("Please input the target directory you want to move in: ")
	val targetPath = Paths.get(scanner.nextLine())
	if (targetPath != null && !Files.exists(targetPath)) {
		println("Directory $targetPath not exist, created.")
		Files.createDirectories(targetPath)
	}

	println("Now parsing file $sourceFile ...")
	val playlists = parse(sourceFile)

	println("Found ${playlists.size} playlist(s): ")
	for ((index, playlist) in playlists.withIndex())
		println("\t${index + 1}. ${playlist.name} has ${playlist.items.size} items")
	println("Input those playlist(s) you want to copy out, 0 for all, separated with comma(,): ")
	val toHandleString = scanner.nextLine()
	val toHandle = ArrayList<Int>()
	if (toHandleString.contentEquals("0"))
		toHandle.addAll(playlists.indices)
	else
		toHandle.addAll(toHandleString.trim().replace(" ", "").split(",").map { it.toInt() })

	println("Will copy out playlist No. `${toHandle.joinToString()}`, continue? (y for yes): ")
	val yes = scanner.nextLine()
	if (!(yes.contentEquals("y") || yes.contentEquals("yes"))) {
		println("Canceled.")
		System.exit(0)
	}
	println("Now copy out musics in ${toHandle.size} playlists into `$targetPath`...")
	var errors = 0
	for (handling in toHandle) {
		val handlingPlaylist = playlists[handling - 1]
		val name = handlingPlaylist.name
		val items = handlingPlaylist.items

		println("\tCopying out ${handlingPlaylist.items.size} items in `$name`...")

		val playlistPath = Paths.get("$targetPath/$name")

		if (!Files.exists(playlistPath)) {
			println("\t\tDirectory for `$name` not exist, created.")
			Files.createDirectories(playlistPath)
		}
		println("\t\tCopying out now...")
		items.forEach {
			try {
				Copy.copyFileToDirectory(it.path, playlistPath)
			} catch (e: Exception) {
				System.err.println("\t\tError ` ${e.message}` occurred. Still copying.")
				errors++
			}
		}
		println("\t\tFinish copied items in `$name`.")
	}
	println("Finished all copying task, ${if (errors == 0) "no" else errors.toString()} error(s) occurred.")
}

private inline fun inputValidPath(scanner: Scanner, condition: (Path) -> Boolean): Path {
	var path: Path
	var isValid: Boolean
	do {
		path = Paths.get(scanner.nextLine())
		isValid = path != null && condition(path)
		if (!isValid)
			System.err.println("File (or directory) not exist or not valid! Please retype...")
		else break
	} while (!isValid)
	return path
}
