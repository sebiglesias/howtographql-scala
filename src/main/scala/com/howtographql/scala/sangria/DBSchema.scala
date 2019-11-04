package com.howtographql.scala.sangria

import akka.http.scaladsl.model.DateTime
import models._
import java.sql.Timestamp

import slick.ast.BaseTypedType
import slick.jdbc.H2Profile.api._
import slick.jdbc.JdbcType
import slick.lifted.ProvenShape

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.language.postfixOps


object DBSchema {

  implicit val dateTimeColumnType: JdbcType[DateTime] with BaseTypedType[DateTime] =
    MappedColumnType.base[DateTime, Timestamp](
      dt => new Timestamp(dt.clicks),
      ts => DateTime(ts.getTime)
    )

  /**
    * Load schema and populate sample data withing this Sequence od DBActions
    */
  class LinksTable(tag: Tag) extends Table[Link](tag, _tableName = "LINKS"){

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def url: Rep[String] = column[String]("URL")
    def description: Rep[String] = column[String]("DESCRIPTION")
    def createdAt: Rep[DateTime] = column[DateTime]("CREATED_AT")

    def * : ProvenShape[Link] = (id, url, description, createdAt).mapTo[Link]
  }
  val Links = TableQuery[LinksTable]

  class UsersTable(tag: Tag) extends Table[User](tag, _tableName = "USERS"){

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("NAME")
    def email: Rep[String] = column[String]("EMAIL")
    def password: Rep[String] = column[String]("PASSWORD")
    def createdAt: Rep[DateTime] = column[DateTime]("CREATED_AT")

    def * : ProvenShape[User] = (id, name, email, password, createdAt).mapTo[User]
  }

  val Users = TableQuery[UsersTable]

  class VotesTable(tag: Tag) extends Table[Vote](tag, _tableName = "VOTES"){

    def id: Rep[Int] = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId: Rep[Int] = column[Int]("USER_ID")
    def linkId: Rep[Int] = column[Int]("LINK_ID")
    def createdAt: Rep[DateTime] = column[DateTime]("CREATED_AT")

    def * : ProvenShape[Vote] = (id, userId, linkId, createdAt).mapTo[Vote]
  }

  val Votes = TableQuery[VotesTable]

  val databaseSetup = DBIO.seq(
    Links.schema.create,
    Users.schema.create,
    Votes.schema.create,

    Links forceInsertAll Seq(
      Link(1, "http://howtographql.com", "Awesome community driven GraphQL tutorial", DateTime(2017,9,12)),
      Link(2, "http://graphql.org", "Official GraphQL web page", DateTime(2017,10,1)),
      Link(3, "https://facebook.github.io/graphql/", "GraphQL specification", DateTime(2017,10,2))
    ),

    Users forceInsertAll Seq(
      User(1, "mario", "mario@example.com", "s3cr3t"),
      User(2, "Fred", "fred@flinstones.com", "wilmalove")
    ),

    Votes forceInsertAll Seq(
      Vote(id = 1, userId = 1, linkId = 1),
      Vote(id = 2, userId = 1, linkId = 2),
      Vote(id = 3, userId = 1, linkId = 3),
      Vote(id = 4, userId = 2, linkId = 2),
    )
  )

  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)

    new DAO(db)

  }

}
