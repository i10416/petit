package dev.i10416.petit
import laika.directive.{DirectiveRegistry, Templates}
import laika.ast.DocumentCursor
import laika.ast.{
  InternalTarget,
  Deleted,
  SpanLink,
  SpanSequence,
  Text,
  PathBase,
  TemplateElement,
  TemplateSpan
}
import laika.directive.Spans
import laika.ast.Image
import laika.ast.Remove
import laika.ast.Span
import laika.ast.Paragraph
import laika.ast.TextContainer
private[this] object PetitDirectives extends DirectiveRegistry {


  private def docLink(d: DocumentCursor) = SpanLink(
    d.target.title.getOrElse(SpanSequence(Text(d.path.toString) +: Nil)) +: Nil,
    InternalTarget(PathBase.parse(d.path.toString))
  )

  val toc = Templates.create("toc") {
    import Templates.dsl._
    cursor.map { cursor =>
      ???
    }
  }


  private val EmptyTemplateElement = TemplateElement(Deleted(Nil))
  val prevDoc = Templates.create("prevDoc") {
    import Templates.dsl._
    cursor.map { cursor =>
      cursor.previousDocument.fold[TemplateSpan](EmptyTemplateElement) { d =>
        TemplateElement(docLink(d))
      }
    }
  }

  val nextDoc = Templates.create("nextDoc") {
    import Templates.dsl._
    cursor.map { cursor =>
      cursor.nextDocument.fold[TemplateSpan](EmptyTemplateElement) { d =>
        TemplateElement(docLink(d))
      }
    }
  }
  val spanDirectives = Seq()
  val blockDirectives = Seq()
  val templateDirectives = Seq(prevDoc, nextDoc)
  val linkDirectives = Seq()
}
