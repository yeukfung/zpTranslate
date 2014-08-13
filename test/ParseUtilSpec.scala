package test

import org.specs2.mutable._
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._
import utils.ParseUtil

class ParseUtilSpec extends Specification {

  "ParseUtil" should {

    "fetch the directory listing" in new WithApplication {
      ParseUtil.parseSourceFolder("data/src", "public/data/in")
    }

  }

}
