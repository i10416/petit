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
  val githubSVG =
    """<svg class="petit-plain-icon github" viewBox="0 0 100 100" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;">
      |  <g class="svg-shape">
      |    <path d="M49.995,1c-27.609,-0 -49.995,22.386 -49.995,50.002c-0,22.09 14.325,40.83 34.194,47.444c2.501,0.458 3.413,-1.086 3.413,-2.412c0,-1.185 -0.043,-4.331 -0.067,-8.503c-13.908,3.021 -16.843,-6.704 -16.843,-6.704c-2.274,-5.773 -5.552,-7.311 -5.552,-7.311c-4.54,-3.103 0.344,-3.042 0.344,-3.042c5.018,0.356 7.658,5.154 7.658,5.154c4.46,7.64 11.704,5.433 14.552,4.156c0.454,-3.232 1.744,-5.436 3.174,-6.685c-11.102,-1.262 -22.775,-5.553 -22.775,-24.713c-0,-5.457 1.949,-9.92 5.147,-13.416c-0.516,-1.265 -2.231,-6.348 0.488,-13.233c0,0 4.199,-1.344 13.751,5.126c3.988,-1.108 8.266,-1.663 12.518,-1.682c4.245,0.019 8.523,0.574 12.517,1.682c9.546,-6.47 13.736,-5.126 13.736,-5.126c2.728,6.885 1.013,11.968 0.497,13.233c3.204,3.496 5.141,7.959 5.141,13.416c0,19.209 -11.691,23.436 -22.83,24.673c1.795,1.544 3.394,4.595 3.394,9.26c0,6.682 -0.061,12.076 -0.061,13.715c0,1.338 0.899,2.894 3.438,2.406c19.853,-6.627 34.166,-25.354 34.166,-47.438c-0,-27.616 -22.389,-50.002 -50.005,-50.002"/>
      |  </g>
      |</svg>""".stripMargin
  val githubIcon = InlineSVGIcon(githubSVG, Some("GitHub"))
  val twitterSVG =
    """<svg class="petit-plain-icon twitter" viewBox="0 0 248 204" version="1.1" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" >
      |  <path fill="#1d9bf0" d="M221.95 51.29c.15 2.17.15 4.34.15 6.53 0 66.73-50.8 143.69-143.69 143.69v-.04c-27.44.04-54.31-7.82-77.41-22.64 3.99.48 8 .72 12.02.73 22.74.02 44.83-7.61 62.72-21.66-21.61-.41-40.56-14.5-47.18-35.07 7.57 1.46 15.37 1.16 22.8-.87-23.56-4.76-40.51-25.46-40.51-49.5v-.64c7.02 3.91 14.88 6.08 22.92 6.32C11.58 63.31 4.74 33.79 18.14 10.71c25.64 31.55 63.47 50.73 104.08 52.76-4.07-17.54 1.49-35.92 14.61-48.25 20.34-19.12 52.33-18.14 71.45 2.19 11.31-2.23 22.15-6.38 32.07-12.26-3.77 11.69-11.66 21.62-22.2 27.93 10.01-1.18 19.79-3.86 29-7.95-6.78 10.16-15.32 19.01-25.2 26.16z"/>
      |</svg>""".stripMargin
  val twitterIcon = InlineSVGIcon(twitterSVG,Some("Twitter"))

  val registry: IconRegistry = IconRegistry(
    "menu" -> menu,
    "github" -> githubIcon,
    "twitter" -> twitterIcon
  )
}
