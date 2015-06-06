package db

import java.net.{InetAddress, Inet4Address, InetSocketAddress}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class ProxyDataSource extends slick.jdbc.ProxyDataSource {
  override def lookup(serviceName: String): Future[Option[(InetSocketAddress, Option[FiniteDuration])]] =
    Future.successful(Some((new InetSocketAddress(InetAddress.getByName("192.168.59.103"), 5432), None)))
}
