package services

import javax.inject.{Singleton, Inject}

import akka.pattern.ask
import akka.actor.{ActorSystem, Props, ActorLogging, Actor}
import akka.cluster.Cluster
import akka.contrib.datareplication.{GCounter, DataReplication}
import akka.contrib.datareplication.Replicator.{Changed, WriteLocal, Update, Subscribe}
import akka.pattern.pipe
import models.Customer
import repository.CustomerRepository

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration


@Singleton
class CustomerService @Inject() (actorSystem: ActorSystem, customerRepository: CustomerRepository) {

  val customerServiceState =
    actorSystem.actorOf(CustomerServiceState.props(customerRepository), "customer-service-state")

  def getCustomers()(implicit timeout: FiniteDuration): Future[Seq[Customer]] =
    customerServiceState
      .ask(CustomerServiceState.GetCustomers)(timeout)
      .mapTo[Seq[Customer]]

  def insertCustomer(name: String)(implicit timeout: FiniteDuration): Future[Int] =
    customerServiceState
      .ask(CustomerServiceState.InsertCustomer(name))(timeout)
      .mapTo[Int]
}


private[services] object CustomerServiceState {
  def props(customerRepository: CustomerRepository): Props =
    Props(new CustomerServiceState(customerRepository))

  case object GetCustomers
  case class InsertCustomer(name: String)
  case object RefreshCache
  case object SignalCacheExpiry
  
  final private val CustomerUpdateCounter = "CustomerUpdateCounter"
}

private[services] class CustomerServiceState(customerRepository: CustomerRepository) extends Actor with ActorLogging {

  val replicator = DataReplication(context.system).replicator
  implicit val cluster = Cluster(context.system)

  import CustomerServiceState._
  import context.dispatcher

  override def preStart(): Unit = {
    replicator ! Subscribe(CustomerUpdateCounter, self)
    self ! RefreshCache
  }

  override def receive: Receive =
    receiveRequests(Seq.empty)

  private def receiveRequests(customers: Seq[Customer]): Receive = {
    case GetCustomers =>
      sender() ! customers

    case InsertCustomer(name) =>
      log.info(s"Inserting customer {} into database", name)
      customerRepository
        .insert(name)
        .map(_.id)
        .pipeTo(sender())
        .andThen {
          case _ => self ! SignalCacheExpiry
        }

    case SignalCacheExpiry =>
      log.info(s"Notifying cluster that the cache is now outdated")
      replicator ! Update(CustomerUpdateCounter, GCounter(), WriteLocal)(_ + 1)

    case Changed(CustomerUpdateCounter, _: GCounter) =>
      self ! RefreshCache

    case RefreshCache =>
      log.info(s"Retrieving customers from database")
      customerRepository
        .all()
        .pipeTo(self)

    case customers: Seq[Customer] @unchecked =>
      context.become(receiveRequests(customers))
  }
}
