package io.github.mahdibohloul.kpringmediator.core

/**
 * Basic exception type of the Kpring MediatR
 *
 * @author Mahdi Bohloul
 * @param message Exception message
 */
open class KpringMediatorException(message: String?) : RuntimeException(message)


/**
 * Exception thrown when there is not a [RequestHandler] available for a [Request]
 *
 * @author Mahdi Bohloul
 */
class NoRequestHandlerException(message: String?) : KpringMediatorException(message)


/**
 * Exception thrown when there is an attempt to register a [RequestHandler] for a
 * [Request] that already has a [RequestHandler] registered.
 *
 * @author Mahdi Bohloul
 */
class DuplicateRequestHandlerRegistrationException(message: String?) : KpringMediatorException(message)


/**
 * Exception thrown when there are no [NotificationHandler]s available for an [Notification]
 *
 * @author Mahdi Bohloul
 */
class NoNotificationHandlersException(message: String?) : KpringMediatorException(message)


/**
 * Exception thrown when there is not a [CommandHandler] available for a [Command]
 *
 * @author Mahdi Bohloul
 */
class NoCommandHandlerException(message: String?) : KpringMediatorException(message)

/**
 * Exception thrown when there is an attempt to register a [CommandHandler] for a
 * [Command] that already has a [CommandHandler] registered.
 *
 * @author Mahdi Bohloul
 */
class DuplicateCommandHandlerRegistrationException(message: String?) : KpringMediatorException(message)