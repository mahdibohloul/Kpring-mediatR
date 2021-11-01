package io.github.mahdibohloul.kpringmediator.infrastructure

import io.github.mahdibohloul.kpringmediator.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver

/**
 * @author Mahdi Bohloul
 */
class DefaultFactory(
    private val applicationContext: ApplicationContext
) : Factory {

    private val registeredRequestHandlers: MutableMap<Class<out Request<*>>, RequestHandlerProvider<*>> = HashMap()
    private val registeredNotificationHandlers: MutableMap<Class<out Notification>, MutableSet<NotificationHandlerProvider<*>>> =
        HashMap()
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

    override fun <TNotification : Notification> get(notificationClass: Class<out TNotification>): Set<NotificationHandler<TNotification>> {
        if (!initialized) {
            initializeHandlers()
        }
        val handlers = mutableSetOf<NotificationHandler<TNotification>>()
        registeredNotificationHandlers[notificationClass]?.let {
            for (provider in it) {
                val handler = provider.handler as NotificationHandler<TNotification>
                handlers.add(handler)
            }
        }
            ?: throw NoNotificationHandlersException("No NotificationHandlers are registered to receive notification of type ${notificationClass.canonicalName}")
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
                applicationContext.getBeanNamesForType(NotificationHandler::class.java)
                    .forEach { registerNotificationHandler(it) }
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

    private fun registerNotificationHandler(notificationHandlerName: String) {
        logger.debug("Registering NotificationHandler with name $notificationHandlerName")
        val notificationHandler: NotificationHandler<*> =
            applicationContext.getBean(notificationHandlerName) as NotificationHandler<*>
        val generics =
            GenericTypeResolver.resolveTypeArguments(notificationHandler::class.java, NotificationHandler::class.java)
        generics?.let {
            val notificationType = it[0] as Class<out Notification>
            val eventProvider = NotificationHandlerProvider(applicationContext, notificationHandler::class)
            registeredNotificationHandlers[notificationType]?.add(eventProvider) ?: kotlin.run {
                registeredNotificationHandlers[notificationType] = mutableSetOf(eventProvider)
            }
            logger.info("Registered NotificationHandler ${notificationHandler::class.simpleName} to receive Notification ${notificationType.simpleName}")
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