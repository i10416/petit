package dev.i10416.petit

case class PetitConfig(
    metadata: SiteMetaData
)

case class SiteMetaData(
    baseURL: String = "localhost",
    title: String = "",
    description: String = "",
    language: Option[String] = None,
    admins: List[String] = Nil,
    copyWrightOwners: List[String] = Nil
)

case class PageConfig(
    lang: String,
    title: String,
    tags: List[String],
    description: Option[String],
    og: OG
)

case class OG(
    title: Option[String],
    description: Option[String]
)
