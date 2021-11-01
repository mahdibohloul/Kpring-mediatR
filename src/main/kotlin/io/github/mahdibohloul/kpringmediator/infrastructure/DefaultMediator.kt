package io.github.mahdibohloul.kpringmediator.infrastructure

import io.github.mahdibohloul.kpringmediator.core.*
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
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
        return handler.handle(request)
    }

    override suspend fun sendAsync(command: Command) {
        val handler = factory.get(command::class.java)
        handler.handle(command)
    }

    override suspend fun publishAsync(notification: Notification) {
        supervisorScope {
            val notificationHandlers = factory.get(notification::class.java)
            notificationHandlers.forEach { handler ->
                async { handler.handle(notification) }
            }
        }
    }

}
