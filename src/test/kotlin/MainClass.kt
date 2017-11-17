import java.text.NumberFormat

object MainClass {
	@JvmStatic
	fun main(vararg args: String) {
		val total = 189564.toDouble()
		val step = (total - total / 9) / 3
		val point1 = 0 + step
		val point2 = point1 + step
		val point3 = point2 + step

		if (point1.toInt() == 56167)
			println("yep")

		val percentFormatter = NumberFormat.getPercentInstance()
		percentFormatter.minimumFractionDigits = 1

		println("$point1 $point2 $point3")
		println("${percentFormatter.format(point1 / total)} ${point2 / total} ${point3 / total}")
	}
}