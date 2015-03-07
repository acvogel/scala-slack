package com.flyberrycapital.slack

/**
 * Class for representing a Slack IM
 * 
 * @param id The IM channel ID.
 * @param user The user ID of the "calling user"
 * @param created A UNIX timestamp corresponding to the IM creation data/time.
 * @param is_user_deleted Denotes if the other user's account has been disabled.
 *
 * TODO:
 * @param is_open Denotes whether the IM is open.
 * @param unread_count The number of messages that the calling user has yet to read.
 * @param latest The latest message in the channel.
 */
case class SlackIM(id: String, user: String, created: Int, is_user_deleted: Boolean)
