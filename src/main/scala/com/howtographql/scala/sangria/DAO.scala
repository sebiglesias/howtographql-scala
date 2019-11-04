package com.howtographql.scala.sangria
import slick.jdbc.H2Profile.api._
import DBSchema._
import models._
import sangria.execution.deferred.{RelationIds, SimpleRelation}

import scala.concurrent.Future

class DAO(db: Database) {
  def allLinks: Future[Seq[Link]] = db.run(Links.result)

  def getLinks(ids: Seq[Int]): Future[Seq[Link]] = db.run(
    Links.filter(_.id inSet ids).result
  )

  def getLinksByUserIds(ids: Seq[Int]): Future[Seq[Link]] = {
    db.run {
      Links.filter(_.postedBy inSet ids).result
    }
  }

  def getUsers(ids: Seq[Int]): Future[Seq[User]] = db.run(
    Users.filter(_.id inSet ids).result
  )

  def getVotes(ids: Seq[Int]): Future[Seq[Vote]] = db.run(
    Votes.filter(_.id inSet ids).result
  )

  def getVotesByUserIds(ids: Seq[Int]): Future[Seq[Vote]] = {
    db.run {
      Votes.filter(_.userId inSet ids).result
    }
  }

  def getVotesByLinkIds(ids: Seq[Int]): Future[Seq[Vote]] = {
    db.run {
      Votes.filter(_.linkId inSet ids).result
    }
  }

  def getVotesByRelationIds(rel: RelationIds[Vote]): Future[Seq[Vote]] =
    db.run(
      Votes.filter { vote =>
        rel.rawIds.collect({
          case (SimpleRelation("byUser"), ids: Seq[Int]) => vote.userId inSet ids
          case (SimpleRelation("byLink"), ids: Seq[Int]) => vote.linkId inSet ids
        }).foldLeft(true: Rep[Boolean])(_ || _)

      } result
    )
}
