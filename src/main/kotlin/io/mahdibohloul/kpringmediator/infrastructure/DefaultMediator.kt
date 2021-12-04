package io.mahdibohloul.kpringmediator.infrastructure

import io.mahdibohloul.kpringmediator.core.Command
import io.mahdibohloul.kpringmediator.core.CommandHandler
import io.mahdibohloul.kpringmediator.core.Factory
import io.mahdibohloul.kpringmediator.core.Mediator
import io.mahdibohloul.kpringmediator.core.Notification
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
        val handler = factory.get(request::class.java)
        logger.info("Get handler of ${request::class.simpleName} request from factory")
        return handler.handle(request)
    }

    override suspend fun sendAsync(command: Command) {
        val handler = factory.get(command::class.java)
        logger.info("Get handler of ${command::class.simpleName} command from factory")
        handler.handle(command)
    }

    override suspend fun publishAsync(notification: Notification) {
        supervisorScope {
            val notificationHandlers = factory.get(notification::class.java)
            notificationHandlers.forEach { handler ->
                async(handler.getCoroutineDispatcher()) {
                    logger.info("The ${notification::class.simpleName} notification publish async " +
                        "and handled by ${handler::class.simpleName} in ${Thread.currentThread().name} thread")
                    handler.handle(notification)
                }
            }
        }
    }

    private companion object {
        val logger: Logger = LoggerFactory.getLogger(DefaultMediator::class.java)
    }
}
