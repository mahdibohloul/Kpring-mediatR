package io.mb.mediator.infrastructure

import io.mb.mediator.interfaces.Factory
import io.mb.mediator.interfaces.Mediator
import io.mb.mediator.interfaces.MediatorThreadFactory
import io.mb.mediator.interfaces.Command
import io.mb.mediator.interfaces.CommandHandler
import io.mb.mediator.interfaces.Event
import io.mb.mediator.interfaces.EventHandler
import io.mb.mediator.interfaces.Request
import io.mb.mediator.interfaces.RequestHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.context.ApplicationContext
import java.util.concurrent.Executor
import java.util.concurrent.Executors


/**
 * Implementation of Mediator that is specific for the Spring Framework. This class requires it be
 * instantiated with the [ApplicationContext] containing the beans for all the handlers.
 * The [ApplicationContext] is used to retrieve all the beans that implement [CommandHandler],
 * [EventHandler], and [RequestHandler]. Optionally this class can be instantiated with a
 * [Executor]. If one is not provided a FixedThreadPool will be used with a thread count
 * equal to the count of processor available. The [Executor] is only used on the async variants
 * of the dispatch and emit events.
 *
 *
 * @author Mahdi Bohloul
 */
class DefaultMediator constructor(
    private val factory: Factory,
) : Mediator {

    private var executor: Executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), MediatorThreadFactory())


    /**
     * Creates the Spring specific implementation of MediatR with a custom [Executor] for
     * performing async operations.
     *
     * @param executor The executor to execute asynchronous operations
     */
    constructor(factory: Factory, executor: Executor) : this(factory) {
        this.executor = executor
    }


    override suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse {
        val handler = factory.get(request::class.java)
        return handler.handle(request)
    }

    override suspend fun sendAsync(command: Command) {
        val handler = factory.get(command::class.java)
        handler.handle(command)
    }

    override suspend fun publishAsync(event: Event) {
        coroutineScope {
            val emmitHandlers = factory.get(event::class.java)
            emmitHandlers.forEach { handler ->
                async { handler.handle(event) }
            }
        }
    }
}