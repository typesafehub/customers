package db

import java.net.{URI, InetAddress, InetSocketAddress}

import com.typesafe.conductr.bundlelib.play.{Env, LocationService}
import com.typesafe.conductr.bundlelib.play.ConnectionContext.Implicits.defaultContext
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ProxyDataSource extends slick.jdbc.ProxyDataSource {
  override def lookup(serviceName: String): Future[Option[(InetSocketAddress, Option[FiniteDuration])]] =
    if (Env.isRunByConductR)
      LocationService.lookup(serviceName).map {
        case Some(uriString) =>
          val uri = new URI(uriString)
          Some((new InetSocketAddress(InetAddress.getByName(uri.getHost), uri.getPort), None))
        case None =>
          None
      }
    else
      Future.successful(Some((new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 5432), None)))
}
