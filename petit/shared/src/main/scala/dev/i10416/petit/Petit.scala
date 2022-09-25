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
import laika.config.ConfigError
import dev.i10416.petit.static.SiteMap
import dev.i10416.petit.static.RSS
import java.time.LocalDateTime
import cats.effect.IO
import laika.ast._
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat

object Petit extends ThemeProvider {

  /** enable access css and js with link directives from templates
    *
    * e.g. `@:linkCSS { paths = ${petit.site.includeCSS} }` at
    * default.template.html
    */
  private def conf = ConfigBuilder.empty
    .withValue(
      "petit.site.includeCSS",
      (Path.Root / "petit" / "css") +: Nil
    )
    .withValue(
      "petit.site.includeJS",
      (Path.Root / "petit" / "js") +: Nil
    )
    .withValue(
      "petit.site.generator",
      MetaProps.generator
    )
    .withValue(PlainIcons.registry)

  /** inject thml template and css, js assets into InputTree
    */
  private def styles[F[_]: Sync] = InputTree
    .apply[F]
    .addClasspathResource(
      "default.template.html",
      Path.Root / "default.template.html"
    )
    .addClasspathResource(
      "index.template.html",
      Path.Root / "index.template.html"
    )
    .addClasspathResource(
      "theme.css",
      Path.Root / "petit" / "css" / "theme.css"
    )
    .addClasspathResource(
      "main.js",
      Path.Root / "petit" / "js" / "main.js"
    )
  private def commonSEOPropsProvider[F[_]: Sync]: Theme.TreeProcessor[F] =
    Kleisli { tree =>
      val withSEOProps = tree.root.mapDocuments {
        case doc @ Document(_, _, _, config, _) =>
          doc.copy(
            config = config
              .withValue(
                "petit.seo.og.title",
                doc.title.getOrElse(SpanSequence.empty).extractText
              )
              .withValue(
                "petit.seo.og.description",
                "..."
              )
              .build
          )
      }
      Sync[F].pure(tree.copy(root = withSEOProps))
    }
  // note: 同様にして tree.root.allDocuments.map(document => document.withSEOConfigs(...))
  private def addListPage[F[_]: Sync]: Theme.TreeProcessor[F] = Kleisli {
    tree =>
      // tree.staticDocuments(*).sourceFile.name endsWith .css =>
      // doc.input.map(_.through(fs2.text.utf9.decode).through(minifyCSS))
      val entries = tree.root.allDocuments.map { doc =>
        BlockSequence(
          Seq(
            Header(
              3,
              SpanLink
                .internal(doc.path)
                .apply(doc.title.getOrElse(SpanSequence(doc.path.name)))
            ),
            Paragraph("...."),
            Paragraph.apply(
              doc.config
                .get[String]("laika.metadata.date")
                .getOrElse("No Publish Date Available")
            ),
            Rule()
          ),
          NoOpt
        )
      }
      val articleListDocument =
        TitleDocumentConfig.inputName(tree.root.config).map { inputName =>
          Document(
            path = Path.Root / inputName,
            content = RootElement(entries),
            fragments = Map.empty,
            config =
              tree.root.config.withValue(LaikaKeys.versioned, false).build
          )
        }
      val withTemplate = articleListDocument.map {
        case doc @ Document(_, _, _, config, _) =>
          doc.copy(config =
            config
              .withValue(
                LaikaKeys.template,
                "index.template.html"
              )
              .build
          )
      }

      val transformed = tree.copy(root =
        tree.root.copy(tree =
          tree.root.tree.copy(
            titleDocument = withTemplate.toOption,
            content = tree.root.tree.content
          )
        )
      )

      Sync[F].pure(transformed)
  }
  /*private def enrichLinkIfPossible: Theme.TreeProcessor[IO] =
    Kleisli { tree =>
      import cats.syntax.traverse._
      val docsWithRichLinksRulesF = tree.root.allDocuments.toList.traverse {
        doc =>
          val url2OGPInfoListF = doc.content
            .collect {
              // case RichLink(_,url,_,_) if !excludeRules.contains(url) => url
              case SpanLink(a, ExternalTarget(url), c, d) =>
                url
            }
            .traverse(u => /* fetchOGPInfo(url) >> */ IO(u -> u /*info*/ ))

          for {
            url2OGPInfoList <- url2OGPInfoListF
          } yield {
            val ruleList = url2OGPInfoList.map { case (url, ogpInfo) =>
              RewriteRules.forBlocks {
                // case RichLink(_,`url`,_,_) =>
                // case SpanLink(_, ExternalTarget(`url`), _, _) =>
                // Replace(OgpInfoElement(ogpInfo))
                // Replace(???)
                // case RichLink(_,url,_,_) => Rewrite(Block(SpanLink(_, ExternalTarget(url), _, _)))
                case _ => Retain
              }
            }
            doc -> ruleList.reduce { case (f, g) => f ++ g }
          }
      }
      for {
        docsWithRichLinksRules <- docsWithRichLinksRulesF
      } yield {
        val m = docsWithRichLinksRules.toMap
        val r = tree.root.rewrite { cursor =>
          m.get(cursor.target) match {
            case Some(rules) =>
              Right(rules)
            case None => Right(RewriteRules.empty)
          }
        }
        r.fold(_ => tree, tree.copy(_))
      }
    }*/

  /** Add rss in document tree
    */
  private def addRSS[F[_]: Sync]: Theme.TreeProcessor[F] = Kleisli { tree =>
    val host =
      tree.root.config.get[String]("petit.site.host").getOrElse("localhost")
    val paths = tree.root.allDocuments
      .map(doc =>
        (
          doc.title.getOrElse(SpanSequence.apply("")).extractText,
          doc.path.relativeTo(Path.Root).parent.name
        )
      )
      .distinct
    val xml = RSS.atom(host, LocalDateTime.now, paths)
    val rss = BinaryInput
      .fromString[F](
        Path.Root / "atom.xml",
        xml,
        TargetFormats.Selected("html", "HTML")
      )
    Sync[F].pure(tree.addStaticDocuments(Seq(rss)))
  }

  /** Add static resource in document tree
    */
  private def addSiteMap[F[_]: Sync]: Theme.TreeProcessor[F] = Kleisli { tree =>
    val host =
      tree.root.config.get[String]("petit.site.host").getOrElse("localhost")
    val paths = tree.root.allDocuments
      .map(doc => doc.path.relativeTo(Path.Root).parent.name)
      .distinct
    val xml = SiteMap.apply(host, None, paths.toList)
    val sitemap = BinaryInput
      .fromString[F](
        Path.Root / "sitemap.xml",
        xml,
        TargetFormats.Selected("html", "HTML")
      )
    Sync[F].pure(tree.addStaticDocuments(Seq(sitemap)))
  }

  def injectOgp = RewriteRules.forSpans {
    case SpanLink(content, target, title, opt) =>
      Replace(???)
      ???
  } /*RewriteRule[Block] = {

      ???
  }*/

  /** Build Petit theme. This theme also contains Laika Standard Directives,
    * GithubMarkdown Support and SyntaxHighlighting.
    */
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
    .processTree(addRSS, HTML)
    .processTree(commonSEOPropsProvider, HTML)
    .build
}
