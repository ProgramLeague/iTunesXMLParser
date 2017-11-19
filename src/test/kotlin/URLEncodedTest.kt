import org.eclipse.jetty.util.UrlEncoded

object URLEncodedTest {
	@JvmStatic
	fun main(vararg args: String) {
		val string = "MAGIC%20OF%20LiFE%20-%20pain+.mp3".replace("+", "{{<>ADD<>}}")
		println(UrlEncoded.decodeString(string).replace("{{<>ADD<>}}", "+"))
	}
}