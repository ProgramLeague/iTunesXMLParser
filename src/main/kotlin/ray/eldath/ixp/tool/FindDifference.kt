package ray.eldath.ixp.tool

object FindDifference {
	fun <E> findDifference(a: List<E>, b: List<E>): Difference<E> =
			Difference(a.filterNot { b.contains(it) }, b.filterNot { a.contains(it) })
}

class Difference<out E>(val moreInA: List<E>, val moreInB: List<E>)