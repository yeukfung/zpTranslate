package controllers

import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.mvc.Controller
import scalax.file.Path
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results

object TranslateFiles extends Controller {

  val tblQuery = TableQuery[TranslateFilesTable] //see a way to architect your app in the computers-database-slick sample  

  //JSON read/write macro
  implicit val translateFileFormat = Json.format[TranslateFile]

  val inPath = "public/data/in"
  val assetPath = "assets/data/in"
  def init = DBAction { implicit rs =>
    tblQuery.delete(rs.dbSession)
    val allFiles = Path.fromString(inPath).descendants().filter(p => p.isFile)

    allFiles.foreach { p =>
      val urlPath = p.path.replace(inPath, assetPath)

      if (!urlPath.endsWith("DS_Store")) {
        val translateFile = TranslateFile(urlPath, "")
        println(translateFile)

        tblQuery.insert(translateFile)
      }
    }

    Ok("data inited")
  }

  def ajaxList() = DBAction { implicit rs =>

    val q = rs.getQueryString("tag").getOrElse("required") match {
      case "notVerified" => tblQuery.filter(i => i.isVerified == false && i.isRequired == true)
      case "all" => tblQuery
      case "deleted" => tblQuery.filter(_.deleted)
      case "verified" => tblQuery.filter(i => i.isVerified == true && i.isRequired == true).filter(!_.deleted)
      case "required" => tblQuery.filter(_.isRequired).filter(!_.deleted)
      case "notRequired" => tblQuery.filter(!_.isRequired)
      case _ => tblQuery.filter(_.isRequired).filter(!_.deleted)
    }

    val l = q.list()
    Ok(Json.toJson(l))
  }

  def ajaxGet(id: Long) = DBAction { implicit rs =>
    val v = tblQuery.where(t => t.id === id).firstOption
    Ok(Json.toJson(v))
  }

  def ajaxSave(id: Long) = DBAction(parse.json) { implicit rs =>
    rs.body.validate[TranslateFile].map { file =>
      tblQuery.where(_.id === id).update(file)
      Ok(Json.toJson(file))
    }.getOrElse(BadRequest("invalid json"))
  }

  def index = Action {
    Ok(views.html.translate())
  }

}