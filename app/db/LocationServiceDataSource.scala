package db

import com.typesafe.conductr.bundlelib.play.LocationService
import com.typesafe.conductr.bundlelib.play.ConnectionContext.Implicits.defaultContext
import com.typesafe.conductr.bundlelib.scala.{URI, LocationCache}
import com.typesafe.dynamicdatasource.DynamicDataSource
import scala.beans.BeanProperty
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * A DynamicDataSource that will use ConductR's service lookup if the program has been
 * started by ConductR; otherwise a local host and port will be used as configured
 * via the respective BeanProperty properties.
 */
class LocationServiceDataSource extends DynamicDataSource {
  @BeanProperty var localHost: String = _
  @BeanProperty var localPort: Int = _

  private val cache = LocationCache()

  override def lookup(serviceName: String): Future[Option[(String, Int)]] =
    LocationService.lookup(serviceName, URI(s"tcp://$localHost:$localPort"), cache).map {
      case Some(uri) =>
        Some((uri.getHost, uri.getPort))
      case None =>
        None
    }
}
