package dev.i10416.petit.static

import dev.i10416.petit.SiteMetaData
import java.time.LocalDateTime
import laika.time.PlatformDateFormat
object RSS {
  private val theme = "Petit theme(github.com/i10416/petit)"

  // NOTE: Should I support music and video RSS too?

  def atomItem(
      guid: String,
      link: String,
      title: String = "",
      author: Option[String] = None
  ) =
    s"""|
        |<entry>
        |  <id>$guid</id>
        |  <title>$title</title>
        |  <link>$link</link>
        |  <updated> ...  </updated>
        |  ${author.fold("")(a => s"<author>$a</author>")}
        |  <summary> ... </summary>
        |</entry>
        |""".stripMargin

  def atom(
      baseURL: String,
      lastBuildDate: LocalDateTime,
      items: Seq[(String, String)] = Nil,
      title: String = "",
      language: Option[String] = None
  ) =
    s"""|<?xml version='1.0' encoding='UTF-8'?>
        |<feed xmlns="http://www.w3.org/2005/Atom">
        |
        |  <id>${baseURL}</id>
        |  <title>${if (title.isEmpty) baseURL else title}</title>
        |  <updated>${lastBuildDate}</updated>
        |  <link rel='self' type='application/atom+xml' href='${baseURL}/atom.xml'> ${baseURL} </link>
        |
        |  <description>Recent contents on ${baseURL}</description>
        |  <generator>Laika(planet42.org/Laika) with $theme </generator>
        |  ${language.fold("")(l => s"<language>$l</language>")}
|  <copyright>${ /*copyWrightOwners.mkString(", ")*/
       } </copyright>
        |  <!-- todo: format datetime correctly -->
        |  <lastBuildDate>${lastBuildDate}</lastBuildDate>
        |  ${items.map { case (title, path) =>
         atomItem(s"${baseURL}/$path", s"${baseURL}/$path", title)
       }.mkString}
        |</feed>
        |""".stripMargin
  def v2(
      baseURL: String,
      lastBuildDate: LocalDateTime,
      paths: Seq[(String, String)] = Nil,
      title: String = "",
      language: Option[String] = None
  ) =
    s"""|<?xml version='1.0' encoding='UTF-8'?>
        |<rss version='2.0'>
        |  <channel>
        |    <title> ${title}</title>
        |    <link> ${baseURL} </link>
        |    <description>Recent contents on ${baseURL}</description>
        |    <generator>Laika(planet42.org/Laika) with $theme </generator>
        |    ${language.fold("")(l => s"<language>$l</language>")}
|    <copyright>${ /*metaData.copyWrightOwners.mkString(", ")*/
       } </copyright>
        |    <!-- todo: format datetime correctly -->
        |    <lastBuildDate>${lastBuildDate}</lastBuildDate>
        |    ...items
        |  </channel>
        |</feed>
        |""".stripMargin
  def v2Item(guid: String, title: String = "", author: Option[String] = None) =
    s"""|
        |<item>
        |  <title>$title</title>
        |  <link> ... </link>
        |  <pubDate> ...  </pubDate>
        |  ${author.fold("")(a => s"<author>$a</author>")}
        |  <guid>$guid</guid>
        |  <description> ... </description>
        |</item>
        |""".stripMargin
}
