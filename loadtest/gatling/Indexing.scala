
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import bootstrap._
import assertions._


class Indexing extends Simulation {

val myCustomFeeder = new Feeder[String] {
  import org.joda.time.DateTime
  import scala.util.Random

  private val RNG = new Random

  import java.util.concurrent.atomic.AtomicInteger
  val index = new AtomicInteger(0);

  val channels = Array("uk", "de", "com")

  val mask = 2;

  // random number in between [a...b]
  private def randChannel() = channels(index.incrementAndGet()&mask)

  // random number in between [a...b]
  private def randInt(a:Int, b:Int) = RNG.nextInt(b-a) + a

  // always return true as this feeder can be polled infinitively
  override def hasNext = true

  override def next: Map[String, String] = {
    val id1 = ""+ randInt(0, 10000)
    val id2 = ""+randInt(0, 10000)
    val id3 = ""+randInt(0, 10000)

    Map("id1" -> id1,
        "id2" -> id2,
        "id3" -> id3,
	"channel" -> randChannel)
    }
}


		val httpConf = http
			.baseURL("http://10.0.1.29:8080")
			//.baseURL("http://localhost:8080")
			.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.3")
			.acceptLanguageHeader("en-US,en;q=0.8,fr;q=0.6")
			.acceptEncodingHeader("gzip,deflate,sdch")
			.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11")

		val headers_1 = Map(
  			"Connection" -> "Keep-Alive"
		)

		val scn = scenario("Scenario Name").feed(myCustomFeeder)
			.during(600 seconds) {
				exec(http("request_1")
					.post("/indexing/index")
					.headers(headers_1)
					.header("Content-Type","application/json")
					.body(StringBody("""{ "channel" : "${channel}", "site" : "amazon", "items" : [ { "id" : "${id1}","type":"map"}, { "id" : "${id2}","type":"compass"}, { "id" : "${id3}","type":"torch"} ]  }"""))
				).pause(250 milliseconds)
			}

		setUp(scn.inject(ramp(1000 users) over (100 seconds))).protocols(httpConf)
}

