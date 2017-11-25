@file:JvmName("MainClass")

package ray.eldath.ixp.main

import ray.eldath.ixp.tool.copyFileToDirectory
import ray.eldath.ixp.tool.openOrCreateDirectories
import ray.eldath.ixp.util.VERSION
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.streams.toList

fun main(args: Array<String>) {
	val scanner = Scanner(System.`in`)
	println("iTunesXMLParser - v$VERSION")

	println("Please input the location of the XML file you want to parse: ")
	val sourceFile = inputValidPath(scanner, { path -> Files.exists(path) && path.toString().endsWith(".xml") })

	println("Please input the target directory you want to move in: ")
	val targetPath = Paths.get(scanner.nextLine())?.openOrCreateDirectories()

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

		playlistPath.openOrCreateDirectories("\t\t")
		println("\t\tCopying out now... This may takes few minutes...")

		val points = ArrayList<Int>()
		val percentFormatter = NumberFormat.getPercentInstance()
		percentFormatter.minimumFractionDigits = 1

		val total = items.size.toDouble()
		val step = (total - total / 9) / 3
		points.add(step.toInt())
		points.add((step * 2).toInt())
		points.add((step * 3).toInt())

		for ((index, item) in items.withIndex()) {
			try {
				copyFileToDirectory(item.path, playlistPath, Files.list(playlistPath).toList())
			} catch (e: Exception) {
				System.err.println("\t\tError ` ${e.message}` occurred. Still copying.")
				errors++
			}
			if (index in points)
				println("\t\t(${percentFormatter.format(index / total)}) finished. Still copying.")
		}
		println("\t\tFinish copied items in `$name`.")
	}
	println("Finished all copying task, ${if (errors == 0) "no" else errors.toString()} error(s) occurred.")
	println("Type ANYTHING and type ENTER to exit...")
	scanner.next()
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
