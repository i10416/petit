
# Petit: Simple [Laika](https://github.com/planet42/Laika) website theme for Scala 2.12, 2.13 and 3.

|scala 2.12| scala 2.13|scala 3|
|---|---|---|
|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_2.12.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_2.12/)|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_2.13.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_2.13/)|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_3.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_3/)|


## Setup

```sh
mkdir -p docs/article-1
touch docs/article-1/README.md
```

## Directory Structure

(optional file)

```
docs

  ├── (directory.conf)
  ├── article-1/
  |   ├── (directory.conf)
  |   ├── README.md
  ├── article-2/
  |   ├── (directory.conf)
  |   ├── README.md
  |   ....
```

## Run with [Scala CLI](https://scala-cli.virtuslab.org/)

```sh
scala-cli run transform.scala
```

`transform.scala`

```scala
// using lib org.planet42::laika-core:0.18.1
// using lib org.planet42::laika-io:0.18.1
// using lib org.typelevel::cats-effect:3.3.4
// using lib dev.i10416::petit:0.0.1
import cats.effect.{IO,Async,Sync, Resource}
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
```


## Directives Syntax

```
@:callout(info)
message
@:@
```

@:callout(info)
message
@:@

```
@:callout(warn)
message
@:@
```

@:callout(warn)
message
@:@

```
@:callout(error)
message
@:@
```

@:callout(error)
message
@:@
