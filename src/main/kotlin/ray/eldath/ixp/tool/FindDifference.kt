package ray.eldath.ixp.tool

object FindDifference {
	fun <E> findDifference(a: List<E>, b: List<E>): Difference<E> =
			Difference(a.filterNot { it in b }, b.filterNot { it in a })
}

class Difference<out E>(val moreInA: List<E>, val moreInB: List<E>)