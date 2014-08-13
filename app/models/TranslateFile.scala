package models

import play.api.db.slick.Config.driver.simple._

case class TranslateFile(
  inPath: String,
  translatedText: String,
  editMode: String = "text",
  deleted: Boolean = false,
  isRequired: Boolean = true,
  isVerified: Boolean = false,
  outPath: Option[String] = None,
  id: Option[Long] = None)

/* Table mapping
 */
class TranslateFilesTable(tag: Tag) extends Table[TranslateFile](tag, "TranslateFile") {

  def inPath = column[String]("inPath", O.NotNull)
  def translatedText = column[String]("translatedText", O.NotNull)
  def editMode = column[String]("editMode", O.NotNull)
  def deleted = column[Boolean]("deleted", O.NotNull)
  def isRequired = column[Boolean]("isRequired", O.NotNull)
  def isVerified = column[Boolean]("isVerified", O.NotNull)
  def outPath = column[Option[String]]("outPath", O.Nullable)

  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)

  def * = (inPath, translatedText, editMode, deleted, isRequired, isVerified, outPath, id) <> (TranslateFile.tupled, TranslateFile.unapply _)
}
  