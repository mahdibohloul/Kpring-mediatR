package cab.mb.mediator.interfaces

import cab.mb.mediator.interfaces.command.Command
import cab.mb.mediator.interfaces.command.CommandHandler
import cab.mb.mediator.interfaces.event.Event
import cab.mb.mediator.interfaces.event.EventHandler
import cab.mb.mediator.interfaces.request.Request
import cab.mb.mediator.interfaces.request.RequestHandler

/**
 * Basic exception type of the Spring Reactive MediatR
 *
 * @author Mahdi Bohloul
 * @param message Exception message
 */
open class ReactiveSpringMediatorException(message: String?) : RuntimeException(message)


/**
 * Exception thrown when there is not a [RequestHandler] available for a [Request]
 *
 * @author Mahdi Bohloul
 */
class NoRequestHandlerException(message: String?) : ReactiveSpringMediatorException(message)


/**
 * Exception thrown when there is an attempt to register a [RequestHandler] for a
 * [Request] that already has a [RequestHandler] registered.
 *
 * @author Mahdi Bohloul
 */
class DuplicateRequestHandlerRegistrationException(message: String?) : ReactiveSpringMediatorException(message)


/**
 * Exception thrown when there are no [EventHandler]s available for an [Event]
 *
 * @author Mahdi Bohloul
 */
class NoEventHandlersException(message: String?) : ReactiveSpringMediatorException(message)


/**
 * Exception thrown when there is not a [CommandHandler] available for a [Command]
 *
 * @author Mahdi Bohloul
 */
class NoCommandHandlerException(message: String?) : ReactiveSpringMediatorException(message)

/**
 * Exception thrown when there is an attempt to register a [CommandHandler] for a
 * [Command] that already has a [CommandHandler] registered.
 *
 * @author Mahdi Bohloul
 */
class DuplicateCommandHandlerRegistrationException(message: String?) : ReactiveSpringMediatorException(message)