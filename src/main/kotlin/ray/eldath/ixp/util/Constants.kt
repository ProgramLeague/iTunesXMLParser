package ray.eldath.ixp.util

import java.io.File

object Constants {
	const val VERSION = "1.0.3"

	val CURRENT_PATH: String = File("").canonicalPath
}
