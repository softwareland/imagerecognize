package pl.softwareland.photosearch

import com.microsoft.ml.spark.FluentAPI._
import com.microsoft.ml.spark.cognitive._
import javax.swing.{JEditorPane, JFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.annotation.tailrec


object PhotoSearch {

  def bingPhotoSearch(name: String, queries: List[String], pages: Int)(implicit spark: SparkSession) = {
    val offsets = for {offset <- Range(0, pages)} yield offset * 10
    val parameters = for {offset <- offsets; query <- queries} yield Photo(query, offset)
    import spark.implicits._
    parameters.toDF("queries", "offsets")
      .mlTransform(
        new BingImageSearch()
          .setSubscriptionKey("your key")
          .setOffsetCol("offsets")
          .setQueryCol("queries")
          .setCount(10)
          .setImageType("photo")
          .setOutputCol("images")
      ).mlTransform(BingImageSearch.getUrlTransformer("images", "urls"))
      .withColumn("labels", lit(name))
      .limit(400)
  }

  def displayDF(df: DataFrame, n: Int = 5, image_cols: Set[String] = Set("urls")) = {
    val rows = df.take(n)
    val cols = df.columns
    import Html._
    val tableHTML = buildHtmlTable(rows.toList, List.empty[String]).mkString
    val html = style + body(header(cols.toList), tableHTML)
    GUI.
      builder("text/html", html)
      .title("Images")
      .setSize(600, 600)
      .setContent()
      .setVisible()
  }

  def getRandomWords(implicit spark:SparkSession) = {
   spark
      .read
      .parquet("random_words.parquet")
  }

  def getRandomLinks(randomWords:DataFrame) = {
    randomWords
      .mlTransform(new BingImageSearch()
      .setSubscriptionKey("your key")
      .setCount(10)
      .setQueryCol("words")
      .setOutputCol("images"))
    .mlTransform(BingImageSearch.getUrlTransformer("images", "urls"))
    .withColumn("label", lit("other"))
    .limit(400)
  }

  def getImages(snowLeopardUrls: DataFrame, randomLinks:DataFrame) = {
    snowLeopardUrls
      .union(randomLinks)
      .distinct()
      .repartition(100)
    .mlTransform(BingImageSearch.downloadFromUrls("urls", "image", concurrency=5, timeout=5000))
  }

  def trainTestImages(images:DataFrame) = {
    images.randomSplit(Array(.7,.3), seed=1)
  }
}

case class Photo(query: String, offset: Int)

object GUI {
  val builder: (String, String) => Builder = (typ, text) => new Builder(typ, text)

  class Builder(typ: String, text: String) {

    val frame = new JFrame()
    val ed1 = new JEditorPane(typ, text)

    def title(title: String): Builder = {
      frame.setTitle(title)
      this
    }

    def setVisible(): Builder = {
      frame.setVisible(true)
      this
    }

    def setSize(width: Int, height: Int): Builder = {
      frame.setSize(width, height)
      this
    }

    def setContent(): Builder = {
      frame.setContentPane(ed1)
      this
    }

    def setUnVisible(): Builder = {
      Thread.sleep(10000)
      frame.setVisible(false)
      this
    }
  }

}

object Html {

  val style =
    """
<!DOCTYPE html>
<html>
<head>
<style>
table {
    font-family: arial, sans-serif;
    border-collapse: collapse;
    width: 300;
}

td, th {
    border: 1px solid #dddddd;
    text-align: left;
    padding: 8px;
}

tr:nth-child(even) {
    background-color: #dddddd;
}
</style>
</head>"""

  @tailrec
  def buildHtmlTable(rows: List[Row], concatTable: List[String]): List[String] = {
    rows match {
      case Nil => concatTable
      case headRow :: tail =>
        val values = headRow.toSeq.toList.map(_.toString)
        buildHtmlTable(tail, ("<tr>" :: buildTh(values, List.empty[String]) ::: "</tr>" :: concatTable))
    }

  }

  @tailrec
  private def buildTh(values: Seq[String], ths: List[String]): List[String] = {
    values match {
      case Nil => ths
      case head :: tail =>
        if (head.endsWith(".jpg")) {
          val th = s"<td><img src=${head} width=100</td>"
          buildTh(tail, ths :+ th)
        } else {
          val th = s"<td>${head}</td>"
          buildTh(tail, ths :+ th)
        }
    }

  }

  val body: (String, String) => String = (header, tableHTML) => {
    s"""
             <body>
                <table>
                  <tr>
                    ${header}
                  </tr>
                  ${tableHTML}
              </table>
            </body>
      </html>
  """
  }

  val header: List[String] => String = cols => "" concat (for {c <- cols} yield "<th>" + c + "</th>").mkString
}

