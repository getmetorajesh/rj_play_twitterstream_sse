package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import twitter4j.conf.ConfigurationBuilder
import play.api.libs.iteratee.Enumeratee
import twitter4j._
import twitter4j.{Status => TwitterStatus}
import twitter4j.auth._
import twitter4j.conf._
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import services._
import play.api.libs.EventSource

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {

   val cb = new ConfigurationBuilder()
   cb.setDebugEnabled(true)
     .setOAuthConsumerKey("")
     .setOAuthConsumerSecret("")
     .setOAuthAccessToken("")
     .setOAuthAccessTokenSecret("")
   
   val config =  cb.build()
   val toJson: Enumeratee[(String, TwitterStatus), JsValue] = Enumeratee.map[(String, TwitterStatus)] {
     case (searchQuery, status) =>
       Json.obj("message"->s"$searchQuery: ${status.getText}", 
                 "author" -> status.getUser.getName)
   }
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  
  
  def stream(query: String) = Action {
    val queries = query.split(",")
    val streams = queries.map { query =>  
      val twitterListener = new TwitterStreamListener(query, config)  
      twitterListener.listenAndStream
    }
    
    val mixStreams = streams.reduce((s1, s2) => s1 interleave s2)
    val jsonMixStreams = mixStreams through toJson
    
    Ok.chunked(jsonMixStreams through EventSource()).as("text/event-stream")
  }
  

}
