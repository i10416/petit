// using lib org.http4s::http4s-core:1.0.0-M30
// using lib org.http4s::http4s-ember-core:1.0.0-M30
// using lib org.http4s::http4s-ember-server:1.0.0-M30
// using lib org.typelevel::cats-effect:3.3.4
import org.http4s.ember.server
import org.http4s.HttpApp
import org.http4s.server.staticcontent._
import org.http4s.server.Server
import com.comcast.ip4s.{Port, Host}
import cats.effect.{IO, ExitCode}
import cats.effect.unsafe.implicits.global

object Main {
  def run(args: Array[String] = Array("0.0.0.0", "8080", "dist")): Unit = {
    val host :: port :: dist :: Nil = args.toList
    server.EmberServerBuilder
      .default[IO]
      .withHost(Host.fromString(host))
      .withPort(Port.fromInt(port.toIntOption.getOrElse(8080)).get)
      .withHttpApp(fileService[IO](FileService.Config(dist)).orNotFound)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
      .unsafeRunSync()

  }
}
