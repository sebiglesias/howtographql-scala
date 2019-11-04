package com.howtographql.scala.sangria

import com.howtographql.scala.models._
import sangria.schema.{Field, ListType, ObjectType}
// #
import sangria.schema._
import sangria.macros.derive._

object GraphQLSchema {


  // definition of ObjectType for our Link class
  val LinkType: ObjectType[Unit, Link] = ObjectType[Unit, Link](
    "Link",
    fields[Unit, Link](
      Field("id", IntType, resolve = _.value.id),
      Field("url", StringType, resolve = _.value.url),
      Field("description", StringType, resolve = _.value.description)
    )
  )

  val Id = Argument("id", IntType)
  val Ids = Argument("ids", ListInputType(IntType))


  // 2
  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](
      Field("allLinks",
        ListType(LinkType),
        resolve = c => c.ctx.dao.allLinks
      ),
      Field("link", //1
        OptionType(LinkType), //2
        arguments = Id :: Nil, //3
        resolve = c => c.ctx.dao.getLink(c.arg(Id)) //4
      ),
      Field("links", //1
        ListType(LinkType), //2
        arguments = Ids :: Nil, //3
        resolve = c => c.ctx.dao.getLinks(c.arg(Ids)) //4
      )
    )
  )

  // 3
  val SchemaDefinition = Schema(QueryType)
}