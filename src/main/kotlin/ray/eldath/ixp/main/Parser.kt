package ray.eldath.ixp.main

import org.eclipse.jetty.util.UrlEncoded
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.xpath.XPathFactory
import ray.eldath.ixp.util.Dict
import ray.eldath.ixp.util.Playlist
import java.nio.file.Path
import java.nio.file.Paths

object Parser {
	fun parse(path: Path): List<Playlist> {
		val document = SAXBuilder().build(path.toFile())
		val xpathFactory = XPathFactory.instance()
		val root = xpathFactory.compile("/plist/dict").evaluateFirst(document.rootElement) as Element

		val dictsElement: List<Element> = parseElement(root, "Tracks")!!.children
		val dicts = HashMap<Int, Dict>()

		dictsElement.filter { it.name.contentEquals("dict") }
				.map { parseDict(it) }
				.forEach { dicts.put(it.id, it) }

		val playlistsElement: List<Element> = parseElement(root, "Playlists")!!.children
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

	private fun parseInt(input: String): Int = if (input.isEmpty()) 0 else Integer.parseInt(input)

	private fun parseElementString(element: Element, key: String): String {
		val parsed = parseElement(element, key)
		return if (parsed == null) "" else parsed.textTrim
	}

	private val emptyPath = Paths.get("")!!
	private fun parseLocation(location: String): Path {
		if (location.startsWith("http"))
			return emptyPath
		if (location.startsWith("file://localhost/"))
			return Paths.get(UrlEncoded.decodeString(location.replace("file://localhost/", "")))
		return emptyPath
	}

	private fun parseDict(dictElement: Element): Dict {
		return Dict(parseInt(parseElementString(dictElement, "Track ID")),
				parseElementString(dictElement, "Name"),
				parseElementString(dictElement, "Artist"),
				parseElementString(dictElement, "Album"),
				parseElementString(dictElement, "Genre"),
				parseInt(parseElementString(dictElement, "Rating")),
				parseLocation(parseElementString(dictElement, "Location")))
	}

	private fun parsePlaylist(playlistElement: Element, dictMap: Map<Int, Dict>): Playlist {
		val items = ArrayList<Dict>()

		val parsed = parseElement(playlistElement, "Playlist Items")
		val itemsElement =
				if (parsed == null)
					parseElement(playlistElement, "All Items")!!.children
				else
					parsed.children

		itemsElement.mapTo(items) { dictMap[Integer.parseInt(parseElementString(it, "Track ID"))]!! }

		return Playlist(parseInt(parseElementString(playlistElement, "Playlist ID")),
				parseElementString(playlistElement, "Name"),
				parseElementString(playlistElement, "Description"),
				items)
	}
}