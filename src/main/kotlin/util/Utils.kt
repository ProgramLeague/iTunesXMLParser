package util

import java.nio.file.Path

class Dict(val id: Int, val name: String, val artist: String, val album: String, val genre: String, val rating: Int, val path: Path) {
	override fun toString(): String {
		return "Dict(id=$id, name='$name', artist='$artist', album='$album', genre='$genre', rating=$rating, path=$path)"
	}
}

class Playlist(val id: Int, val name: String, val description: String, val items: List<Dict>) {
	override fun toString(): String {
		return "Playlist(id=$id, name='$name', description='$description', size of `items`=${items.size}})"
	}
}