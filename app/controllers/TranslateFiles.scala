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
import play.api.libs.json.JsArray
import play.api.libs.json.JsString

object TranslateFiles extends Controller {

  val tblQuery = TableQuery[TranslateFilesTable] //see a way to architect your app in the computers-database-slick sample  

  //JSON read/write macro
  implicit val translateFileFormat = Json.format[TranslateFile]

  val inPath = "public/data/in"
  val assetPath = "assets/data/in"
  val outAssetPath = "assets/data/out"
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

    val l = q.list().map { f =>
      f.copy(outPath = Some(f.inPath.replace(assetPath, outAssetPath)))
    }
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

  def dump = DBAction { implicit rs =>
    val l = tblQuery.sortBy(_.id).list()
    Ok(Json.obj("dump" -> Json.toJson(l)))
  }

  def batchImport = DBAction(parse.json(maxLength = 1024 * 1024)) { implicit rs =>
    println("batch import working")
    Ok("batch import done")

    (rs.body \ "dump").asOpt[List[TranslateFile]] map { list =>

      println("list size: " + list.size)
      tblQuery.delete

      list.foreach { f =>

        tblQuery.insert(f)
      }

      Ok("done import")

    } getOrElse (BadRequest("invalid json"))

  }

}