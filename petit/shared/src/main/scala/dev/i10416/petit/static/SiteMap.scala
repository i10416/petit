package dev.i10416.petit.static

object SiteMap {

  def apply(
      host: String = "localhost",
      baseDirectory: Option[String] = None,
      paths: List[String] = Nil
  ) =
    s"""|
        |<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
        |  xmlns:xhtml="http://www.w3.org/1999/xhtml">
        | ${paths.map { path =>
         s"""|
             |  <url>
             |    <loc> ${host}${baseDirectory
              .fold("")(dir => "/dir")}/$path/index.html</loc>
             |    <!-- <lastmod>???</lastmod> -->
             |    <!-- <changefreq></changefreq>  -->
             |    <!-- <priority></priority>  -->
             |  </url>
             |""".stripMargin
       }.mkString}
        |</urlset>
        |""".stripMargin

}
