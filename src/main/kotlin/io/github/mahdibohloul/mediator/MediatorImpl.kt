package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.dispatcher.factories.CommandDispatcherFactory
import io.github.mahdibohloul.mediator.dispatcher.factories.RequestDispatcherFactory
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.publisher.factories.NotificationPublisherFactory
import io.github.mahdibohloul.mediator.request.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MediatorImpl(
  private val defaultMediator: DefaultMediator,
  private val publisherFactory: NotificationPublisherFactory,
  private val requestFactory: RequestDispatcherFactory,
  private val commandFactory: CommandDispatcherFactory,
) : Mediator {
  override suspend fun sendAsync(command: Command): Unit =
    commandFactory.getCommandDispatcher(command)?.sendAsync(command) ?: defaultMediator.sendAsync(command)

  override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse =
    requestFactory.getRequestDispatcher(request)?.sendAsync(request) ?: defaultMediator.sendAsync(request)

  override suspend fun publishAsync(notification: Notification) {
    CoroutineScope(Dispatchers.Default).launch {
      publisherFactory.getNotificationPublisher(notification)?.publishAsync(notification)
        ?: defaultMediator.publishAsync(notification)
    }
  }
}
