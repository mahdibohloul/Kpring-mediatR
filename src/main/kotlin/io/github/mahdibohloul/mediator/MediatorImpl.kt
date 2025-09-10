package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.dispatcher.factories.CommandDispatcherFactory
import io.github.mahdibohloul.mediator.dispatcher.factories.RequestDispatcherFactory
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.publisher.factories.NotificationPublisherFactory
import io.github.mahdibohloul.mediator.request.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MediatorImpl(
  private val defaultMediator: DefaultMediator,
  private val publisherFactory: NotificationPublisherFactory,
  private val requestFactory: RequestDispatcherFactory,
  private val commandFactory: CommandDispatcherFactory,
  @Qualifier("notificationCoroutineScope")
  private val notificationCoroutineScope: CoroutineScope,
) : Mediator {
  override suspend fun sendAsync(
    command: Command,
  ): Unit = commandFactory
    .getCommandDispatcher(command)
    ?.sendAsync(command)
    ?: defaultMediator.sendAsync(command)

  override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(
    request: TRequest,
  ): TResponse = requestFactory
    .getRequestDispatcher(request)
    ?.sendAsync(request)
    ?: defaultMediator.sendAsync(request)

  override suspend fun publishAsync(notification: Notification) {
    try {
      val customPublisher = publisherFactory.getNotificationPublisher(notification)
      if (customPublisher != null) {
        notificationCoroutineScope.launch {
          customPublisher.publishAsync(notification)
        }
      } else {
        defaultMediator.publishAsync(notification)
      }
    } catch (e: Exception) {
      logger.error("Failed to publish notification ${notification::class.simpleName}", e)
    }
  }

  private companion object {
    val logger: Logger = LoggerFactory.getLogger(MediatorImpl::class.java)
  }
}
