[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_3.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_3/)

## Laika Theme: Petit

Simple blog theme for Scala 2.12,2.13 and 3.

|scala 2.12| scala 2.13|scala 3|
|---|---|---|
|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_2.12.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_2.12/)|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_2.13.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_2.13/)|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_3.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_3/)|

### How to Use


```sh
git clone git@github.com:i10416/petit.git
cd petit
sbt +publishLocal
```


Directory Structure


- `<src>`
  - article0
    - README.md
  - article1
    - README.md
  - ...


Dependencies

```scala
val laikaVersion = "0.18.1"
libraryDependencies ++=Seq(
  "dev.i10416" %% "petit" % "0.1.0-SNAPSHOT",
  "org.planet42" %% "laika-core" % laikaVersion,
  "org.planet42" %% "laika-io" % laikaVersion,
  "org.typelevel" %% "cats-effect" % "3.3.1",
)
```

or
```scala
import $ivy.`dev.i10416::petit:0.1.0-SNAPSHOT`
import $ivy.`org.planet42::laika-core:0.18.1`
import $ivy.`org.planet42::laika-preview:0.18.1`
import $ivy.`org.planet42::laika-io:0.18.1`
import $ivy.`org.typelevel::cats-effect:3.3.1`
import $ivy.`dev.i10416::petit:0.1.0-SNAPSHOT-2.0`
```

Script

```scala
import dev.i10416.petit.Petit
import cats.effect.{IO, Async, Sync, Resource}
import laika.format
import laika.io.api.TreeTransformer
import laika.directive.std.StandardDirectives
import laika.markdown.github.GitHubFlavor
import laika.parse.code.SyntaxHighlighting
import laika.api._
import cats.effect.unsafe.implicits.global
import laika.io.implicits._

def createTransformer[F[_]: Async]: Resource[F, TreeTransformer[F]] =
  Transformer
    .from(format.Markdown)
    .to(format.HTML)
    .parallel[F]
    .withTheme(Petit)
    .build
createTransformer[IO].use {
  _.fromDirectory("<src>").toDirectory("<dist>").transform
}.unsafeRunSync()
```

## todo
- [ ] add ToC
- [ ] add line numbers, line highlight,emphasize options for code blocks
- [ ] format scala code before transform
- [ ] enable `foo.ext:title` syntax for code blocks
- [ ] publish
