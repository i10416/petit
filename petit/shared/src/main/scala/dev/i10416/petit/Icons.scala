package dev.i10416.petit

import laika.ast.Icon
import laika.ast.InlineSVGIcon
import laika.rewrite.link.IconRegistry

object PlainIcons {
  val menuSVG =
    """<svg class="petit-plain-icon menu" viewBox="0 0 24 24">
      |  <path fill="currentColor" d="M3,6H21V8H3V6M3,11H21V13H3V11M3,16H21V18H3V16Z" />
      |</svg>""".stripMargin
  val menu: Icon = InlineSVGIcon(menuSVG, Some("Menu"))
  val registry: IconRegistry = IconRegistry(
    "menu" -> menu
  )
}
