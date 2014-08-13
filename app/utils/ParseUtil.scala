package utils

import scalax.file.Path
import scalax.file.FileSystem
import scalax.file.ImplicitConversions._
import scalax.file.PathSet

object ParseUtil {

  def parseSourceFolder(srcFolder: String, targetFolder: String) = {
    val imgs: PathSet[Path] = srcFolder ** "(?i)(.*(jpg|png|gif))".r

    imgs.foreach { p =>
      val targetPath = Path.fromString(p.path.replace(srcFolder, targetFolder))
      println("copying to targetPath: " + targetPath)
      p.copyTo(targetPath, true, true)
    }
  }
}