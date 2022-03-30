// using lib org.planet42::laika-core:0.18.1
// using lib org.planet42::laika-io:0.18.1
// using lib org.typelevel::cats-effect:3.3.4
// using lib dev.i10416::petit:0.0.1
import cats.effect.{IO, Async, Sync, Resource}
import laika.format
import dev.i10416.petit.Petit
import laika.io.api.TreeTransformer
import laika.api._
import cats.effect.unsafe.implicits.global
import laika.io.implicits._

object Transform {
  private def createTransformer[F[_]: Async]: Resource[F, TreeTransformer[F]] =
    Transformer
      .from(format.Markdown)
      .to(format.HTML)
      .parallel[F]
      .withTheme(Petit)
      .build

  def main(arg: Array[String]): Unit = {
    createTransformer[IO]
      .use { transformer =>
        transformer.fromDirectory("docs").toDirectory("dist").transform
      }
      .unsafeRunSync()
  }

}
