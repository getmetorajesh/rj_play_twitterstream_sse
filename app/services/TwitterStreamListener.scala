
package services

import play.api.libs.iteratee._
import scala.concurrent.duration._
import play.api.libs.json._
import play.api.Logger
import twitter4j._
import twitter4j.{Status => TwitterStatus}
import twitter4j.auth._
import twitter4j.conf._


class TwitterStreamListener(searchQuery: String, config: Configuration) {
   val query = new FilterQuery(0, Array(), Array(searchQuery))
   val twitterStream = new TwitterStreamFactory(config).getInstance
   
   def listenAndStream = {
     Logger.info(s"start a listener for $searchQuery")
     
     val (enum, channel) = Concurrent.broadcast[(String, TwitterStatus)]
     val statusListener = new StatusListener() {
       override def onStatus(status: TwitterStatus) = {
         Logger.debug(status.getText)
         channel push (searchQuery, status)
       }
       
      override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) = {}
      override def onTrackLimitationNotice(numberOfLimitedStatuses: Int) = {}
      override def onException(ex: Exception) = ex.printStackTrace()
      override def onScrubGeo(userId: Long, upToStatusId: Long) = {}
      override def onStallWarning(warning: StallWarning) = {}
     }
     
     twitterStream.addListener(statusListener)
     twitterStream.filter(query)
     enum
   }
   
   
   
}