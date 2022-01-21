package dev.i10416.petit
import cats.data.Kleisli
import cats.effect.{Resource, Sync}
import laika.theme.{Theme, ThemeBuilder, ThemeProvider}
import laika.ast.{Path, RootElement, Element, Document}
import laika.directive.std.{StandardDirectives}
import laika.markdown.github.GitHubFlavor
import laika.parse.code.SyntaxHighlighting
import laika.format.HTML
import laika.config.{ConfigBuilder, LaikaKeys}
import laika.io.model.InputTree
import laika.rewrite.nav.TitleDocumentConfig
import laika.io.model.BinaryInput
import laika.rewrite.nav.TargetFormats

object Petit extends ThemeProvider {
  private def conf = ConfigBuilder.empty
    .withValue(
      "petit.site.includeCSS",
      (Path.Root / "petit" / "css") +: Nil
    )
    .withValue(
      "petit.site.includeJS",
      (Path.Root / "petit" / "js") +: Nil
    )
    .withValue(PlainIcons.registry)

  private def styles[F[_]: Sync] = InputTree
    .apply[F]
    .addClasspathResource(
      "default.template.html",
      Path.Root / "default.template.html"
    )
    .addClasspathResource(
      "list.template.html",
      Path.Root / "list.template.html"
    )
    .addClasspathResource(
      "theme.css",
      Path.Root / "petit" / "css" / "theme.css"
    )
    .addClasspathResource(
      "main.js",
      Path.Root / "petit" / "js" / "main.js"
    )

  private def addListPage[F[_]: Sync]: Theme.TreeProcessor[F] = Kleisli {
    tree =>
      val (landingPageContent, fragments, landingPageConfig) =
        (RootElement.empty, Map.empty[String, Element], tree.root.config)
      val titleDoc =
        TitleDocumentConfig.inputName(tree.root.config).map { inputName =>
          Document(
            path = Path.Root / inputName,
            content = landingPageContent,
            fragments = fragments,
            config =
              landingPageConfig.withValue(LaikaKeys.versioned, false).build
          )
        }

      val withTemplate = titleDoc.map(doc =>
        doc.copy(config =
          doc.config.withValue(LaikaKeys.template, "list.template.html").build
        )
      )
      val transformed = tree.copy(root =
        tree.root.copy(tree =
          tree.root.tree.copy(
            titleDocument = Some(withTemplate.right.get),
            content = tree.root.tree.content
          )
        )
      )

      Sync[F].pure(transformed)
  }
  private def addSiteMap[F[_]: Sync]: Theme.TreeProcessor[F] = Kleisli { tree =>
    val paths = tree.root.allDocuments
      .map(doc => (doc.config.get[String]("petit.site.host"),doc.path.relativeTo(Path.Root).parent.name))
      .distinctBy(_._2)
    val baseURL: String = "test"
    val xml = s"""
      |<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
      | ${paths
      .map{case (url,p) => s"""
                           |  <url>
                           |    <loc> ${url.getOrElse("localhost")}/$p/index.html</loc>
                           |    <!-- <lastmod>???</lastmod> -->
                           |  </url>
                           |""".stripMargin}
      .mkString}
      |</urlset>
      |""".stripMargin
    val sitemap = BinaryInput
      .fromString[F](Path.Root / "sitemap.xml", xml, TargetFormats.Selected("html","HTML"))
    Sync[F].pure(tree.addStaticDocuments(Seq(sitemap)))
  }
  def build[F[_]: Sync]: Resource[F, Theme[F]] = ThemeBuilder
    .apply[F]("Petit")
    .addExtensions(
      StandardDirectives,
      GitHubFlavor,
      SyntaxHighlighting,
      PetitDirectives
    )
    .addInputs(styles)
    .addBaseConfig(conf.build)
    .processTree(addListPage, HTML)
    .processTree(addSiteMap, HTML)
    .build
}
