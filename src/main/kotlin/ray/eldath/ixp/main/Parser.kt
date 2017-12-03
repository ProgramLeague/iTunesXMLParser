@file:JvmName("Parser")

package ray.eldath.ixp.main

import org.eclipse.jetty.util.UrlEncoded
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.xpath.XPathFactory
import ray.eldath.ixp.util.Dict
import ray.eldath.ixp.util.Playlist
import java.nio.file.Path
import java.nio.file.Paths

fun parse(path: Path): List<Playlist> {
	val document = SAXBuilder().build(path.toFile())
	val xpathFactory = XPathFactory.instance()
	val root = xpathFactory.compile("/plist/dict").evaluateFirst(document.rootElement) as Element

	val dictsElement: List<Element> = parseElement(root, "Tracks")!!.children
	val dicts = HashMap<Int, Dict>()

	dictsElement.filter { it.name.contentEquals("dict") }
			.map(::parseDict)
			.forEach { dicts.put(it.id, it) }

	val playlistsElement: List<Element> = parseElement(root, "Playlists")?.children ?: throw Exception("parse failed")
	val playlists = ArrayList<Playlist>()

	playlistsElement.filter { it.name.contentEquals("dict") }
			.mapTo(playlists) { parsePlaylist(it, dicts) }

	return playlists
}

private fun parseElement(element: Element, key: String): Element? {
	val children = element.children
	for ((index, child) in children.withIndex()) {
		if (child.name.contentEquals("key") && child.textTrim.contentEquals(key))
			return children[index + 1]
	}
	return null
}

/// overwrites String.toInt in stdlib
private fun String.toInt(): Int = if (isEmpty()) 0 else Integer.parseInt(this)

private fun parseElementString(element: Element, key: String) = parseElement(element, key)?.textTrim ?: ""

private val emptyPath: Path = Paths.get("")

private val add: String = UrlEncoded.encodeString("{{<>ADD<>}}")

private fun parseLocation(location: String): Path = when {
	location.startsWith("http") -> emptyPath
	location.startsWith("file://localhost/") -> {
		val tmp = UrlEncoded.decodeString(location.replace("file://localhost/", "").replace("+", "{{<>ADD<>}}"))
		Paths.get(tmp.replace(add, "+"))
	}
	else -> emptyPath
}

fun String.toLocation() = parseLocation(this)

private fun parseDict(dictElement: Element) = Dict(parseElementString(dictElement, "Track ID").toInt(),
		parseElementString(dictElement, "Name"),
		parseElementString(dictElement, "Artist"),
		parseElementString(dictElement, "Album"),
		parseElementString(dictElement, "Genre"),
		parseElementString(dictElement, "Rating").toInt(),
		parseElementString(dictElement, "Location").toLocation())

private fun parsePlaylist(playlistElement: Element, dictMap: Map<Int, Dict>): Playlist {
	val items = arrayListOf<Dict>()

	val parsed = parseElement(playlistElement, "Playlist Items")
	val itemsElement = parsed?.children ?: parseElement(playlistElement, "All Items")!!.children

	itemsElement.mapTo(items) { dictMap[parseElementString(it, "Track ID").toInt()]!! }

	return Playlist(parseElementString(playlistElement, "Playlist ID").toInt(),
			parseElementString(playlistElement, "Name"),
			parseElementString(playlistElement, "Description"),
			items)
}