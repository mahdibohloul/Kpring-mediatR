package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.command.CommandHandler
import io.github.mahdibohloul.mediator.factories.ComponentFactory
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.notification.NotificationHandler
import io.github.mahdibohloul.mediator.request.Request
import io.github.mahdibohloul.mediator.request.RequestHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

/**
 * Implementation of Mediator that is specific for the Spring Framework. This class requires it be
 * instantiated with the [ApplicationContext] containing the beans for all the handlers.
 * The [ApplicationContext] is used to retrieve all the beans that implement [CommandHandler],
 * [NotificationHandler], and [RequestHandler].
 * @author Mahdi Bohloul
 */
@Suppress("detekt.TooGenericExceptionCaught")
@Component
class DefaultMediator(
  private val factory: ComponentFactory,
) : Mediator {

  override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse {
    val handler = factory.getRequestHandler(request::class)
    logger.debug("Get handler of ${request::class.simpleName} request from factory")
    return handler.handle(request)
  }

  override suspend fun sendAsync(command: Command) {
    val handler = factory.getCommandHandler(command::class)
    logger.debug("Get handler of ${command::class.simpleName} command from factory")
    handler.handle(command)
  }

  override suspend fun publishAsync(notification: Notification) {
    supervisorScope {
      try {
        val notificationHandlers = factory.getNotificationHandlers(notification::class)
        notificationHandlers.forEach { handler ->
          async(handler.getCoroutineDispatcher()) {
            logger.debug(
              "The ${notification::class.simpleName} notification publish async " +
                "and handled by ${handler::class.simpleName} in ${Thread.currentThread().name} thread",
            )
            try {
              handler.handle(notification)
            } catch (e: Exception) {
              publishAsync(notification, e)
            }
          }
        }
      } catch (e: NoNotificationHandlersException) {
        logger.warn("No notification handlers found for notification: ${notification::class.qualifiedName}", e)
      }
    }
  }

  private suspend fun <TNotification : Notification, TException : Exception> publishAsync(
    notification: TNotification,
    exception: TException,
  ) {
    supervisorScope {
      val notificationExceptionHandler =
        factory.getNotificationExceptionHandlers(notification::class, exception::class)
      notificationExceptionHandler.forEach { handler ->
        async(handler.getCoroutineDispatcher()) {
          logger.info(
            "The ${notification::class.simpleName} notification and ${exception::class.simpleName} publish async " +
              "and handled by ${handler::class.simpleName} in ${Thread.currentThread().name} thread",
          )
          handler.handle(notification, exception)
        }
      }
    }
  }

  private companion object {
    val logger: Logger = LoggerFactory.getLogger(DefaultMediator::class.java)
  }
}
