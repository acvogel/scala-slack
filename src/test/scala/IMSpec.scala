import com.flyberrycapital.slack.HttpClient
import com.flyberrycapital.slack.Methods.IM
import org.joda.time.{DateTimeZone, DateTime}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import play.api.libs.json.Json

class IMSpec extends FlatSpec with MockitoSugar with Matchers with BeforeAndAfterEach {

   private val testApiKey = "TEST_API_KEY"

   private var mockHttpClient: HttpClient = _
   var im : IM = _

   override def beforeEach() {
      mockHttpClient = mock[HttpClient]
      when(mockHttpClient.get("im.close", Map("channel" -> "D12345", "token" -> testApiKey)))
         .thenReturn(Json.parse(
         """
           |{
           |   "ok": true
           |}
         """.stripMargin))

      when(mockHttpClient.get("im.close", Map("channel" -> "D54321", "token" -> testApiKey)))
         .thenReturn(Json.parse(
         """
           |{
           |    "ok": true,
           |    "no_op": true,
           |    "already_closed": true
           |}
         """.stripMargin))
      when(mockHttpClient.get("im.mark", Map("channel" -> "D12345", "ts" -> "1234567890.123456", "token" -> testApiKey)))
         .thenReturn(Json.parse(
         """
           |{
           |    "ok": true
           |}
         """.stripMargin))

      when(mockHttpClient.get("im.open", Map("user" -> "U12345", "token" -> testApiKey)))
         .thenReturn(Json.parse(
         """
           |{
           |    "ok": true,
           |    "channel": {
           |      "id": "D024BFF1M"
           |    }
           |}
         """.stripMargin))

      when(mockHttpClient.get("im.open", Map("user" -> "U54321", "token" -> testApiKey)))
         .thenReturn(Json.parse(
         """
           |{
           |    "ok": true,
           |    "no_op": true,
           |    "already_open": true,
           |    "channel": {
           |        "id": "D024BFF1M"
           |    }
           |}
         """.stripMargin))

      when(mockHttpClient.get("im.list", Map("token" -> testApiKey)))
         .thenReturn(Json.parse(
         """
           |{
           |    "ok": true,
           |    "ims": [
           |        {
           |           "id": "D024BFF1M",
           |           "is_im": true,
           |           "user": "USLACKBOT",
           |           "created": 1372105335,
           |           "is_user_deleted": false
           |        },
           |        {
           |           "id": "D024BE7RE",
           |           "is_im": true,
           |           "user": "U024BE7LH",
           |           "created": 1356250715,
           |           "is_user_deleted": false
           |        }
           |    ]
           |}
         """.stripMargin))
      im = new IM(mockHttpClient, testApiKey)
   }

   "IM.close()" should "make a call to im.close and return the response in an IMCloseResponse object" in {
      val responseOk = im.close("D12345")
      responseOk.ok shouldBe true

      val responseErr = im.close("D54321")
      responseErr.ok shouldBe true
      responseErr.no_op shouldBe true
      responseErr.already_closed shouldBe true
   }

   "IM.mark()" should "make a call to im.mark and return the response in an IMMarkResponse object" in {
      val response = im.mark("D12345", "1234567890.123456")
      response.ok shouldBe true
   }

   "IM.open()" should "make a call to im.open and return the response in an IMOpenResponse object" in {
      val responseOk = im.open("U12345")
      responseOk.ok shouldBe true
      responseOk.channelId shouldBe "D024BFF1M"
      responseOk.no_op shouldBe false
      responseOk.already_open shouldBe false

      val responseErr = im.open("U54321")
      responseErr.ok shouldBe true
      responseErr.channelId shouldBe "D024BFF1M"
      responseErr.no_op shouldBe true
      responseErr.already_open shouldBe true
   }

   "IM.list()" should "make a call to im.list and return the response in an IMListResponse object" in {
      val response = im.list()
      response.ok shouldBe true
      response.ims should have length 2

      val im1 = response.ims(0)
      im1.id shouldBe "D024BFF1M"
      im1.user shouldBe "USLACKBOT"
      im1.created shouldBe 1372105335
      im1.is_user_deleted shouldBe false

      val im2 = response.ims(1)
      im2.id shouldBe "D024BE7RE"
      im2.user shouldBe "U024BE7LH"
      im2.created shouldBe 1356250715
      im2.is_user_deleted shouldBe false
   }

}
