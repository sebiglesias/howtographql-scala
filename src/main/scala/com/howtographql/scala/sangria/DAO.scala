package com.howtographql.scala.sangria
import slick.jdbc.H2Profile.api._
import DBSchema._
import com.howtographql.scala.models

import scala.concurrent.Future

class DAO(db: Database) {
  def allLinks: Future[Seq[models.Link]] = db.run(Links.result)
}
