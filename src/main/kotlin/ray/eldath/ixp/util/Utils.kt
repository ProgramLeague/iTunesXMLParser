package ray.eldath.ixp.util

import java.nio.file.Path

data class Dict(val id: Int, val name: String, val artist: String, val album: String, val genre: String, val rating: Int, val path: Path) {
	override fun toString() =
			"Dict(id=$id, name='$name', artist='$artist', album='$album', genre='$genre', rating=$rating, path=$path)"
}

data class Playlist(val id: Int, val name: String, val description: String, val items: List<Dict>) {
	override fun toString() =
			"Playlist(id=$id, name='$name', description='$description', size of `items`=${items.size}})"
}