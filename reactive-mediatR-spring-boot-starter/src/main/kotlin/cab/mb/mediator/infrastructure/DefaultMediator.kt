package cab.mb.mediator.infrastructure

import cab.mb.mediator.interfaces.Factory
import cab.mb.mediator.interfaces.Mediator
import cab.mb.mediator.interfaces.MediatorThreadFactory
import cab.mb.mediator.interfaces.command.Command
import cab.mb.mediator.interfaces.command.CommandHandler
import cab.mb.mediator.interfaces.event.Event
import cab.mb.mediator.interfaces.event.EventHandler
import cab.mb.mediator.interfaces.request.Request
import cab.mb.mediator.interfaces.request.RequestHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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
        logger.info("${executor::class.java.simpleName} will be used for asynchronous operations instead of the default Executor")
    }

    override fun <TRequest : Request<TResponse>, TResponse> send(request: TRequest): TResponse {
        val handler = factory.get(request::class.java)
        logger.debug("Sending ${request::class.simpleName} to handler ${handler::class.simpleName}")
        return handler.handle(request)
    }

    override fun send(command: Command) {
        val handler = factory.get(command::class.java)
        logger.debug("Sending ${command::class.simpleName} to handler ${handler::class.simpleName}")
        return handler.handle(command)
    }

    override fun <TRequest : Request<TResponse>, TResponse> sendReactive(request: TRequest): Mono<TResponse> {
        return Mono.fromCallable {
            val handler = factory.get(request::class.java)
            logger.debug("Sending ${request::class.simpleName} to handler ${handler::class.simpleName}")
            return@fromCallable handler.handle(request)
        }
    }

    override fun sendReactive(command: Command): Mono<Void> {
        return Mono.from {
            val handler = factory.get(command::class.java)
            logger.debug("Sending ${command::class.simpleName} to handler ${handler::class.simpleName}")
            handler.handle(command)
        }
    }

    override fun publish(event: Event) {
        val emmitHandlers = factory.get(event::class.java)
        emmitHandlers.forEach { handler ->
            logger.debug("Sending ${event::class.simpleName} to handler ${handler::class.simpleName}")
            handler.handle(event)
        }
    }

    override fun publishReactive(event: Event): Flux<Void> {
        val emmitHandlers = factory.get(event::class.java)
        return Flux.from { emmitHandlers.map { it.handle(event) } }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DefaultMediator::class.java)
    }
}