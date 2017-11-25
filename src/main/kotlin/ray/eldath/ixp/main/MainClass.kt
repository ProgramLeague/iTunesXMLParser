@file:JvmName("MainClass")

package ray.eldath.ixp.main

import ray.eldath.ixp.tool.*
import ray.eldath.ixp.util.Constants
import ray.eldath.ixp.util.Dict
import java.nio.file.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.streams.toList

fun main(args: Array<String>) {
	val scanner = Scanner(System.`in`)
	println("iTunesXMLParser - v${Constants.VERSION}")

	println("Please input the location of the XML file you want to parse: ")
	val sourceFile = inputValidPath(scanner, { path -> Files.exists(path) && path.toString().endsWith(".xml") })
	println("Please input the target directory you want to move in: ")
	val targetPath = Paths.get(scanner.nextLine())?.openOrCreateDirectories()

	println("Now parsing file $sourceFile ...")
	val playlists = parse(sourceFile)

	println("Found ${playlists.size} playlist(s): ")
	playlists.forEachIndexed { index, playlist -> println("\t${index + 1}. ${playlist.name} has ${playlist.items.size} items") }

	// input ID of playlist(s)
	println("Input those playlist(s) you want to copy out, 0 for all, separated with comma(,): ")
	val toHandleString = scanner.nextLine()
	val toHandle = ArrayList<Int>()
	if (toHandleString.contentEquals("0"))
		toHandle.addAll(playlists.indices)
	else
		toHandle.addAll(toHandleString.trim().replace(" ", "").split(",").map(String::toInt))

	// confirmed
	println("Will copy out playlist No. `${toHandle.joinToString()}`, continue? (y for yes): ")
	val yes = scanner.nextLine()
	if (!(yes.equals("y", true) || yes.equals("yes", true))) {
		println("Canceled.")
		System.exit(0)
	}

	// make exactly the same?
	println("Do you want to ensure that XML files and folders are exactly the same? (Not only copy out the missing files, but also deletes the extra files in the folder) (y for yes, other for no): ")
	val sameString = scanner.nextLine()
	val same = sameString.equals("y", true) || sameString.equals("yes", true)

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

		items.forEachIndexed { index, item ->
			ignoreException(
					{ copyFileToDirectory(item.path, playlistPath, Files.list(playlistPath).toList()) },
					{ "\t\tError ` ${it.message}` occurred. Still copying." },
					{ errors++ }
			)
			if (index in points)
				println("\t\t(${percentFormatter.format(index / total)}) finished. Still copying.")
		}
		println("\t\tFinish copied items in `$name`.")

		if (same) {
			println("Finding difference between the folder $playlistPath and the playlist $name...")
			// A: playlist, B: folder
			val difference = FindDifference.findDifference(items.map(Dict::path), Files.list(playlistPath).toList())
			print("Found ${difference.moreInA.size} file(s) exist in the playlist but not in the folder, ")
			println("found ${difference.moreInB.size} file(s) exist in the folder but not in the playlist.")
			println("Now differentiating...")

			for (element in difference.moreInA)
				ignoreException(
						{ copyFileToDirectory(element, playlistPath) },
						{ "\t\tError ` ${it.message}` occurred. Still copying." }
				)

			for (element in difference.moreInB)
				ignoreException(
						{ copyFileToDirectory(element, playlistPath) },
						{ "\t\tError ` ${it.message}` occurred. Still copying." }
				)
		}
	}

	println("Finished all copying task, ${if (errors == 0) "no" else "$errors"} error(s) occurred.")
	println("Type ANYTHING and type ENTER to exit...")
	scanner.next()
}

inline fun ignoreException(lambda: () -> Unit, message: (Exception) -> String) = ignoreException(lambda, message) { }
inline fun ignoreException(lambda: () -> Unit, message: (Exception) -> String, operation: () -> Unit) = try {
	lambda()
} catch (e: Exception) {
	System.err.println(message(e))
	operation.invoke()
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
