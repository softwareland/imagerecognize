package pl.softwareland

import pl.softwareland.photosearch.PhotoSearch._
import org.apache.spark.sql.SparkSession

object SparkAppStarter extends App{

  implicit val spark = SparkSession
    .builder()
    .appName("photoSearch")
    .getOrCreate()

  val snowLeopardQueries = List("snow leopard")
  val snowLeopardUrls = bingPhotoSearch("snow leopard", snowLeopardQueries, pages=100)
  displayDF(snowLeopardUrls)

  val randomWords =   getRandomWords
  randomWords.show(false)

  val randomLinks = getRandomLinks(randomWords)

  val images = getImages(snowLeopardUrls, randomLinks)

  val train, test = trainTestImages(images)
}
