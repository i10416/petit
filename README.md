[![Release](https://github.com/i10416/petit/actions/workflows/release.yaml/badge.svg)](https://github.com/i10416/petit/actions/workflows/release.yaml)

## Laika Theme: Petit

Simple blog theme for Scala 2.12,2.13 and 3.

|scala 2.12| scala 2.13|scala 3|
|---|---|---|
|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_2.12.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_2.12/)|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_2.13.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_2.13/)|[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/dev.i10416/petit_3.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/dev/i10416/petit_3/)|

### How to Use

build.sbt
```scala
libraryDependencies ++= Seq(
  "org.planet42" %% "laika-core" % "0.18.1",
  "org.planet42" %% "laika-io" % "0.18.1",
  "dev.i10416" %% "petit" % "<version above>",
  "org.typelevel" %% "cats-effect" % "3.3.4",
)
```

or ammonite scala script

or
```scala
import $ivy.`dev.i10416::petit:<version above>`
import $ivy.`org.planet42::laika-core:0.18.1`
import $ivy.`org.planet42::laika-io:0.18.1`
import $ivy.`org.typelevel::cats-effect:3.3.1`
```

Directory Structure


- `<src>`
  - article0
    - README.md
  - article1
    - README.md
  - ...


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

## How to contribute?
- Give it a star⭐
- Drop the feedback to the author @i10416
- Send a PR with fixes of typos/bugs/etc🐛

## License
Licensed under the Apache License, Version 2.0.


## todo
- publish
  - [x] release snapshot
  - [ ] release 0.0.1
- [x] sitemap generator
- [x] rss generator
- [ ] minify css
- [ ] automatically generate last modified date time
- [ ] link with ogp info directive
- [ ] add search
- [ ] ToC
- [ ] add line numbers, line highlight,emphasize options for code blocks
- [ ] format scala code before transform
- [ ] enable `foo.ext:title` syntax for code blocks
