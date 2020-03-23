package pl.softwareland

import pl.softwareland.photosearch.PhotoSearch._
import org.apache.spark.sql.SparkSession

object SparkAppStarter extends App{

  implicit val spark = SparkSession.builder()
    .appName("photoSearch")
    .getOrCreate()


  val snowLeopardQueries = List("snow leopard")
  val snowLeopardUrls = bingPhotoSearch("snow leopard", snowLeopardQueries, pages=100)
  displayDF(snowLeopardUrls)


}
