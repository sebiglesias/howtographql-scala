package com.howtographql.scala.sangria
import slick.jdbc.H2Profile.api._
import DBSchema._
import com.howtographql.scala.models._

import scala.concurrent.Future

class DAO(db: Database) {
  def allLinks: Future[Seq[Link]] = db.run(Links.result)

  def getLinks(ids: Seq[Int]): Future[Seq[Link]] = db.run(
    Links.filter(_.id inSet ids).result
  )
}
