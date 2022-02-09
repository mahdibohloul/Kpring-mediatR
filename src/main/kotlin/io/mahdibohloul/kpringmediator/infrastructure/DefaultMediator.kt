package io.mahdibohloul.kpringmediator.infrastructure

import io.mahdibohloul.kpringmediator.core.Command
import io.mahdibohloul.kpringmediator.core.CommandHandler
import io.mahdibohloul.kpringmediator.core.Factory
import io.mahdibohloul.kpringmediator.core.Mediator
import io.mahdibohloul.kpringmediator.core.Notification
import io.mahdibohloul.kpringmediator.core.NotificationExceptionHandler
import io.mahdibohloul.kpringmediator.core.NotificationHandler
import io.mahdibohloul.kpringmediator.core.Request
import io.mahdibohloul.kpringmediator.core.RequestHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext

/**
 * Implementation of Mediator that is specific for the Spring Framework. This class requires it be
 * instantiated with the [ApplicationContext] containing the beans for all the handlers.
 * The [ApplicationContext] is used to retrieve all the beans that implement [CommandHandler],
 * [NotificationHandler], and [RequestHandler].
 * @author Mahdi Bohloul
 */
class DefaultMediator constructor(
    private val factory: Factory,
) : Mediator {

    override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse {
        val handler = factory.getRequestHandler(request::class)
        logger.info("Get handler of ${request::class.simpleName} request from factory")
        return handler.handle(request)
    }

    override suspend fun sendAsync(command: Command) {
        val handler = factory.getCommandHandler(command::class)
        logger.info("Get handler of ${command::class.simpleName} command from factory")
        handler.handle(command)
    }

    override suspend fun publishAsync(notification: Notification) {
        supervisorScope {
            val notificationHandlers = factory.getNotificationHandlers(notification::class)
            notificationHandlers.forEach { handler ->
                async(handler.getCoroutineDispatcher()) {
                    logger.info(
                        "The ${notification::class.simpleName} notification publish async " +
                            "and handled by ${handler::class.simpleName} in ${Thread.currentThread().name} thread"
                    )
                    try {
                        handler.handle(notification)
                    } catch (e: Exception) {
                        publishAsync(notification, e)
                    }
                }
            }
        }
    }

    private suspend fun <TNotification : Notification, TException : Exception> publishAsync(
        notification: TNotification,
        exception: TException
    ) {
        supervisorScope {
            val notificationExceptionHandler =
                factory.getNotificationExceptionHandlers(notification::class, exception::class)
            notificationExceptionHandler.forEach { handler ->
                async(handler.getCoroutineDispatcher()) {
                    logger.info(
                        "The ${notification::class.simpleName} notification and ${exception::class.simpleName} publish async " +
                            "and handled by ${handler::class.simpleName} in ${Thread.currentThread().name} thread"
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
