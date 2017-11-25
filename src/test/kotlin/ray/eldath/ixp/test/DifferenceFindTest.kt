package ray.eldath.ixp.test

object DifferenceFindTest {
	@JvmStatic
	fun main(vararg args: String) {
		val a = listOf("a", "b", "d", "e")
		val b = listOf("a", "b", "c", "d", "F")
		val difference = findDifference(a, b)
		println("moreInA: ${difference.moreInA}")
		println("moreInB: ${difference.moreInB}")
	}

	private fun <E> findDifference(a: List<E>, b: List<E>): Difference<E> =
			Difference(a.filterNot { b.contains(it) }, b.filterNot { a.contains(it) })

	private class Difference<out E>(val moreInA: List<E>, val moreInB: List<E>)
}