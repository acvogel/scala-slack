/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flyberrycapital.slack.Methods

import com.flyberrycapital.slack.{HttpClient, SlackIM}
import play.api.libs.json.JsObject

/**
 * The container for Slack's 'im' methods (https://api.slack.com/methods).
 *
 * <i>Note: This is a partial implementation, and some (i.e. most) methods are unimplemented.</i>
 */
class IM(httpClient: HttpClient, apiToken: String) {

   import com.flyberrycapital.slack.Responses._



   /**
    * https://api.slack.com/methods/im.close
    *
    * @param channel The channel ID for the direct message history to close.
    * @return IMCloseResponse
    */
   def close(channel: String): IMCloseResponse = {
      val params = Map("channel" -> channel, "token" -> apiToken)

      val responseDict = httpClient.get("im.close", params)

      IMCloseResponse(
         (responseDict \ "ok").as[Boolean],
         (responseDict \ "no_op").asOpt[Boolean].getOrElse(false),
         (responseDict \ "already_closed").asOpt[Boolean].getOrElse(false)
      )
   }

   /**
    * https://api.slack.com/methods/im.mark
    *
    * @param channel The channel ID for the direct message history to set reading cursor in.
    * @param params ts Timestamp of the most recently seen message.
    * @return IMMarkResponse
    */
   def mark(channel: String, ts: String): IMMarkResponse = {
      val params = Map("channel" -> channel, "ts" -> ts, "token" -> apiToken)

      val responseDict = httpClient.get("im.mark", params)

      IMMarkResponse(
         (responseDict \ "ok").as[Boolean]
      )
   }

   /**
    * https://api.slack.com/methods/im.open
    *
    * @param user The user ID for the user to open a direct message channel with.
    * @return IMOpenResponse
    */
   def open(user: String): IMOpenResponse = {
      val params = Map("user" -> user, "token" -> apiToken)

      val responseDict = httpClient.get("im.open", params)

      IMOpenResponse(
         (responseDict \ "ok").as[Boolean],
         (responseDict \ "channel" \ "id").as[String],
         (responseDict \ "no_op").asOpt[Boolean].getOrElse(false),
         (responseDict \ "already_open").asOpt[Boolean].getOrElse(false)
      )
   }

   def list(): IMListResponse = {
      val params = Map("token" -> apiToken)

      val responseDict = httpClient.get("im.list", params)

      val ims = (responseDict \ "ims").as[List[JsObject]] map { (im) =>
         SlackIM(
            (im \ "id").as[String],
            (im \ "user").as[String],
            (im \ "created").as[Int],
            (im \ "is_user_deleted").as[Boolean]
         )
      }

      IMListResponse(
         (responseDict \ "ok").as[Boolean],
         ims
      )
   }
}
