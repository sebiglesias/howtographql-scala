package com.howtographql.scala.sangria

import com.howtographql.scala.models._
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
import sangria.schema.{Field, ListType, ObjectType}
// #
import sangria.schema._
import sangria.macros.derive._

object GraphQLSchema {


  // definition of ObjectType for our Link class
  val LinkType: ObjectType[Unit, Link] = deriveObjectType[Unit, Link]()
  implicit val linkHasId: HasId[Link, Int] = HasId[Link, Int](_.id)

  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))

  val linksFetcher = Fetcher(
    (ctx: MyContext, ids: Seq[Int]) => ctx.dao.getLinks(ids)
  )

  val Resolver: DeferredResolver[MyContext] = DeferredResolver.fetchers(linksFetcher)

  // 2
  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field("allLinks",
        ListType(LinkType),
        resolve = c => c.ctx.dao.allLinks
      ),
      Field("link",
        OptionType(LinkType),
        arguments = Id :: Nil,
        resolve = c => linksFetcher.deferOpt(c.arg(Id))
      ),
      Field("links", //1
        ListType(LinkType), //2
        arguments = Ids :: Nil, //3
        resolve = c => linksFetcher.deferSeq(c.arg(Ids)) //4
      )
    )
  )

  // 3
  val SchemaDefinition = Schema(QueryType)
}