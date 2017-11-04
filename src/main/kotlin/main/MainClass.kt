package main

import main.Parser.parseDict
import main.Parser.parseElement
import main.Parser.parsePlaylist
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.xpath.XPathFactory
import util.Dict
import util.Playlist
import java.io.File

fun main(args: Array<String>) {
	val document = SAXBuilder().build(File("G:\\library.xml"))
	val xpathFactory = XPathFactory.instance()
	val root = xpathFactory.compile("/plist/dict").evaluateFirst(document.rootElement) as Element

	val dictsElement: List<Element> = parseElement(root, "Tracks")!!.children
	val dicts = HashMap<Int, Dict>()

	for (dictElement in dictsElement) {
		if (dictElement.name.contentEquals("dict")) {
			val dict = parseDict(dictElement)
			println(dict)
			dicts.put(dict.id, dict)
		}
	}

	val playlistsElement: List<Element> = parseElement(root, "Playlists")!!.children
	val playlists = ArrayList<Playlist>()

	for (playlistElement in playlistsElement) {
		if (playlistElement.name.contentEquals("dict")) {
			val playlist = parsePlaylist(playlistElement, dicts)
			println(playlist)
			playlists.add(playlist)
		}
	}
}