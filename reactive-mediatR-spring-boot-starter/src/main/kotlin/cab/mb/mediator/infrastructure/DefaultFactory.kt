package cab.mb.mediator.infrastructure

import cab.mb.mediator.interfaces.*
import cab.mb.mediator.interfaces.command.Command
import cab.mb.mediator.interfaces.command.CommandHandler
import cab.mb.mediator.interfaces.event.Event
import cab.mb.mediator.interfaces.event.EventHandler
import cab.mb.mediator.interfaces.request.Request
import cab.mb.mediator.interfaces.request.RequestHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver

class DefaultFactory(
    private val applicationContext: ApplicationContext
) : Factory {

    private val registeredRequestHandlers: MutableMap<Class<out Request<*>>, RequestHandlerProvider<*>> = HashMap()
    private val registeredEventHandlers: MutableMap<Class<out Event>, MutableSet<EventHandlerProvider<*>>> = HashMap()
    private val registeredCommandHandler: MutableMap<Class<out Command>, CommandHandlerProvider<*>> = HashMap()
    private var initialized: Boolean = false


    override fun <TRequest : Request<TResponse>, TResponse> get(requestClass: Class<out TRequest>): RequestHandler<TRequest, TResponse> {
        if (!initialized) {
            initializeHandlers()
        }
        registeredRequestHandlers[requestClass]?.let {
            return it.handler as RequestHandler<TRequest, TResponse>
        }
            ?: throw NoRequestHandlerException("No RequestHandler is registered to handle request of type ${requestClass.canonicalName}")
    }

    override fun <TEvent : Event> get(eventClass: Class<out TEvent>): Set<EventHandler<TEvent>> {
        if (!initialized) {
            initializeHandlers()
        }
        val handlers = mutableSetOf<EventHandler<TEvent>>()
        registeredEventHandlers[eventClass]?.let {
            for (provider in it) {
                val handler = provider.handler as EventHandler<TEvent>
                handlers.add(handler)
            }
        }
            ?: throw NoEventHandlersException("No EventHandlers are registered to handle event of type ${eventClass.canonicalName}")
        return handlers
    }

    override fun <TCommand : Command> get(commandClass: Class<out TCommand>): CommandHandler<TCommand> {
        if (!initialized) {
            initializeHandlers()
        }
        registeredCommandHandler[commandClass]?.let { provider ->
            return provider.handler as CommandHandler<TCommand>
        }
            ?: throw NoCommandHandlerException("No CommandHandler is registered to handle request of type ${commandClass.canonicalName}")

    }

    private fun initializeHandlers() {
        synchronized(this) {
            if (!initialized) {
                applicationContext.getBeanNamesForType(RequestHandler::class.java)
                    .forEach { registerRequestHandler(it) }
                applicationContext.getBeanNamesForType(EventHandler::class.java)
                    .forEach { registerEventHandler(it) }
                applicationContext.getBeanNamesForType(CommandHandler::class.java)
                    .forEach { registerCommandHandler(it) }
                initialized = true
            }
        }
    }

    private fun registerRequestHandler(requestHandlerName: String) {
        logger.debug("Registering RequestHandler with name $requestHandlerName")
        val handler: RequestHandler<*, *> = applicationContext.getBean(requestHandlerName) as RequestHandler<*, *>
        val generics = GenericTypeResolver.resolveTypeArguments(handler::class.java, RequestHandler::class.java)
        generics?.let {
            val requestType = it[0] as Class<out Request<*>>
            if (registeredRequestHandlers.contains(requestType)) {
                throw DuplicateRequestHandlerRegistrationException(
                    "${requestType.canonicalName} already has a registered handler. Each request must have a single request handler"
                )
            }

            val requestProvider = RequestHandlerProvider(applicationContext, handler::class)
            registeredRequestHandlers[requestType] = requestProvider
            logger.info("Registered RequestHandler ${handler::class.simpleName} to handle request ${requestType.simpleName}")
        }
    }

    private fun registerEventHandler(eventHandlerName: String) {
        logger.debug("Registering EventHandler with name $eventHandlerName")
        val eventHandler: EventHandler<*> = applicationContext.getBean(eventHandlerName) as EventHandler<*>
        val generics = GenericTypeResolver.resolveTypeArguments(eventHandler::class.java, EventHandler::class.java)
        generics?.let {
            val eventType = it[0] as Class<out Event>
            val eventProvider = EventHandlerProvider(applicationContext, eventHandler::class)
            registeredEventHandlers[eventType]?.add(eventProvider) ?: kotlin.run {
                registeredEventHandlers[eventType] = mutableSetOf(eventProvider)
            }
            logger.info("Registered EventHandler ${eventHandler::class.simpleName} to handle Event ${eventType.simpleName}")
        }
    }

    private fun registerCommandHandler(commandHandlerName: String) {
        logger.debug("Registering CommandHandler with name $commandHandlerName")
        val handler: CommandHandler<*> = applicationContext.getBean(commandHandlerName) as CommandHandler<*>
        val generics = GenericTypeResolver.resolveTypeArguments(handler::class.java, CommandHandler::class.java)
        generics?.let {
            val requestType = it[0] as Class<out Command>
            if (registeredCommandHandler.contains(requestType)) {
                throw DuplicateCommandHandlerRegistrationException(
                    "${requestType.canonicalName} already has a registered handler. Each request must have a single request handler"
                )
            }

            val requestProvider = CommandHandlerProvider(applicationContext, handler::class)
            registeredCommandHandler[requestType] = requestProvider
            logger.info("Registered CommandHandler ${handler::class.simpleName} to handle request ${requestType.simpleName}")
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DefaultFactory::class.java)
    }
}