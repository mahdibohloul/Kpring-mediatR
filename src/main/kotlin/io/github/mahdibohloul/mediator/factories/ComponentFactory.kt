package io.github.mahdibohloul.mediator.factories

import io.github.mahdibohloul.mediator.command.Command
import io.github.mahdibohloul.mediator.command.CommandHandler
import io.github.mahdibohloul.mediator.DuplicateCommandHandlerRegistrationException
import io.github.mahdibohloul.mediator.DuplicateRequestHandlerRegistrationException
import io.github.mahdibohloul.mediator.NoCommandHandlerException
import io.github.mahdibohloul.mediator.NoNotificationHandlersException
import io.github.mahdibohloul.mediator.NoRequestHandlerException
import io.github.mahdibohloul.mediator.notification.Notification
import io.github.mahdibohloul.mediator.notification.NotificationExceptionHandler
import io.github.mahdibohloul.mediator.notification.NotificationHandler
import io.github.mahdibohloul.mediator.providers.CommandHandlerProvider
import io.github.mahdibohloul.mediator.providers.NotificationExceptionHandlerProvider
import io.github.mahdibohloul.mediator.providers.NotificationHandlerProvider
import io.github.mahdibohloul.mediator.providers.RequestHandlerProvider
import io.github.mahdibohloul.mediator.request.Request
import io.github.mahdibohloul.mediator.request.RequestHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.core.GenericTypeResolver
import kotlin.reflect.KClass
import org.springframework.stereotype.Component

/**
 * @author Mahdi Bohloul
 */
@Component
class ComponentFactory(
  private val applicationContext: ApplicationContext,
) {

  private val registeredRequestHandlers: MutableMap<KClass<out Request<*>>, RequestHandlerProvider<*>> = HashMap()
  private val registeredNotificationHandlers: MutableMap<
    KClass<out Notification>,
    MutableSet<NotificationHandlerProvider<*>>,
    > = HashMap()

  private val registeredNotificationExceptionHandlers:
    MutableMap<
      KClass<out Notification>,
      MutableMap<
        KClass<out Exception>,
        MutableSet<NotificationExceptionHandlerProvider<*>>,
        >,
      > =
    HashMap()
  private val registeredCommandHandler: MutableMap<KClass<out Command>, CommandHandlerProvider<*>> = HashMap()
  private var initialized: Boolean = false

  internal var handleNotificationExceptions: Boolean = false

  fun <TRequest : Request<TResponse>, TResponse> getRequestHandler(
    requestClass: KClass<out TRequest>,
  ): RequestHandler<TRequest, TResponse> {
    if (!initialized) {
      initializeHandlers()
    }
    registeredRequestHandlers[requestClass]?.let {
      return it.handler as RequestHandler<TRequest, TResponse>
    }
      ?: throw NoRequestHandlerException(
        "No RequestHandler " +
          "is registered to handle request of type ${requestClass.simpleName}",
      )
  }

  fun <TNotification : Notification> getNotificationHandlers(
    notificationClass: KClass<out TNotification>,
  ): Set<NotificationHandler<TNotification>> {
    if (!initialized) {
      initializeHandlers()
    }
    val handlers = mutableSetOf<NotificationHandler<TNotification>>()
    registeredNotificationHandlers[notificationClass]?.let {
      it.forEach { provider ->
        val handler = provider.handler as NotificationHandler<TNotification>
        handlers.add(handler)
      }
    }
      ?: throw NoNotificationHandlersException(
        "No NotificationHandlers are " +
          "registered to receive notification of type ${notificationClass.simpleName}",
      )
    return handlers
  }

  fun <TCommand : Command> getCommandHandler(commandClass: KClass<out TCommand>): CommandHandler<TCommand> {
    if (!initialized) {
      initializeHandlers()
    }
    registeredCommandHandler[commandClass]?.let { provider ->
      return provider.handler as CommandHandler<TCommand>
    }
      ?: throw NoCommandHandlerException(
        "No CommandHandler is " +
          "registered to handle request of type ${commandClass.simpleName}",
      )
  }

  fun <TNotification : Notification, TNotificationException : Exception> getNotificationExceptionHandlers(
    notificationClass: KClass<out TNotification>,
    exceptionClass: KClass<out TNotificationException>,
  ): Set<NotificationExceptionHandler<TNotification, TNotificationException>> {
    if (!handleNotificationExceptions) {
      logger.warn("Notification exception handling is disabled")
      return emptySet()
    }
    if (!initialized) {
      initializeHandlers()
    }
    val handlers = mutableSetOf<NotificationExceptionHandler<TNotification, TNotificationException>>()
    registeredNotificationExceptionHandlers[notificationClass]?.get(exceptionClass)?.let {
      it.forEach { provider ->
        val handler = provider.handler as NotificationExceptionHandler<TNotification, TNotificationException>
        handlers.add(handler)
      }
    }
      ?: logger.warn(
        "No NotificationExceptionHandlers are registered to " +
          "receive notification exception of type ${notificationClass.simpleName}",
      )
    return handlers
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
        if (handleNotificationExceptions) {
          applicationContext.getBeanNamesForType(NotificationExceptionHandler::class.java)
            .forEach { registerNotificationExceptionHandler(it) }
        }
        initialized = true
      }
    }
  }

  private fun registerRequestHandler(requestHandlerName: String) {
    logger.debug("Registering RequestHandler with name $requestHandlerName")
    val handler: RequestHandler<*, *> = applicationContext.getBean(requestHandlerName) as RequestHandler<*, *>
    val generics = GenericTypeResolver.resolveTypeArguments(handler::class.java, RequestHandler::class.java)
    generics?.let {
      val requestType = (it[0] as Class<out Request<*>>).kotlin
      if (registeredRequestHandlers.contains(requestType)) {
        throw DuplicateRequestHandlerRegistrationException(
          "${requestType.simpleName} already has a registered handler. Each request must have a single request handler",
        )
      }

      val requestProvider = RequestHandlerProvider(applicationContext, handler::class)
      registeredRequestHandlers[requestType] = requestProvider
      logger.debug("Registered RequestHandler ${handler::class.simpleName} to handle request ${requestType.simpleName}")
    }
  }

  private fun registerNotificationHandler(notificationHandlerName: String) {
    logger.debug("Registering NotificationHandler with name $notificationHandlerName")
    val notificationHandler: NotificationHandler<*> =
      applicationContext.getBean(notificationHandlerName) as NotificationHandler<*>
    val generics =
      GenericTypeResolver.resolveTypeArguments(notificationHandler::class.java, NotificationHandler::class.java)
    generics?.let {
      val notificationType = (it[0] as Class<out Notification>).kotlin
      val eventProvider = NotificationHandlerProvider(applicationContext, notificationHandler::class)
      registeredNotificationHandlers[notificationType]?.add(eventProvider) ?: kotlin.run {
        registeredNotificationHandlers[notificationType] = mutableSetOf(eventProvider)
      }
      logger.debug(
        "Registered NotificationHandler ${notificationHandler::class.simpleName} " +
          "to receive Notification ${notificationType.simpleName}",
      )
    }
  }

  private fun registerNotificationExceptionHandler(notificationExceptionHandlerName: String) {
    logger.debug("Registering NotificationExceptionHandler with name $notificationExceptionHandlerName")
    val notificationExceptionHandler: NotificationExceptionHandler<*, *> =
      applicationContext.getBean(notificationExceptionHandlerName) as NotificationExceptionHandler<*, *>
    val generics =
      GenericTypeResolver.resolveTypeArguments(
        notificationExceptionHandler::class.java,
        NotificationExceptionHandler::class.java,
      )
    generics?.let {
      val notificationType = (it[0] as Class<out Notification>).kotlin
      val exceptionType = (it[1] as Class<out Exception>).kotlin
      val eventProvider =
        NotificationExceptionHandlerProvider(applicationContext, notificationExceptionHandler::class)
      registeredNotificationExceptionHandlers[notificationType]?.let { exceptionTypes ->
        exceptionTypes[exceptionType]?.add(eventProvider) ?: kotlin.run {
          exceptionTypes[exceptionType] = mutableSetOf(eventProvider)
        }
      } ?: kotlin.run {
        registeredNotificationExceptionHandlers[notificationType] =
          mutableMapOf(exceptionType to mutableSetOf(eventProvider))
      }
    }
  }

  private fun registerCommandHandler(commandHandlerName: String) {
    logger.debug("Registering CommandHandler with name $commandHandlerName")
    val handler: CommandHandler<*> = applicationContext.getBean(commandHandlerName) as CommandHandler<*>
    val generics = GenericTypeResolver.resolveTypeArguments(handler::class.java, CommandHandler::class.java)
    generics?.let {
      val requestType = (it[0] as Class<out Command>).kotlin
      if (registeredCommandHandler.contains(requestType)) {
        throw DuplicateCommandHandlerRegistrationException(
          "${requestType.simpleName} already has a registered handler. Each request must have a single request handler",
        )
      }

      val requestProvider = CommandHandlerProvider(applicationContext, handler::class)
      registeredCommandHandler[requestType] = requestProvider
      logger.debug("Registered CommandHandler ${handler::class.simpleName} to handle request ${requestType.simpleName}")
    }
  }

  companion object {
    val logger: Logger = LoggerFactory.getLogger(ComponentFactory::class.java)
  }
}
