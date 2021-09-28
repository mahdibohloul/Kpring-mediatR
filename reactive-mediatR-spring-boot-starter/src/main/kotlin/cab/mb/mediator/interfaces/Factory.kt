package cab.mb.mediator.interfaces

import cab.mb.mediator.interfaces.command.Command
import cab.mb.mediator.interfaces.command.CommandHandler
import cab.mb.mediator.interfaces.event.Event
import cab.mb.mediator.interfaces.event.EventHandler
import cab.mb.mediator.interfaces.request.Request
import cab.mb.mediator.interfaces.request.RequestHandler

/**
 * A factory for handlers for messages that can be sent or published in Spring Reactive MediatR
 *
 * @author Mahdi Bohloul
 */
interface Factory {

    /**
     * Retrieves the RequestHandler for the provided type. If not RequestHandler is
     * registered to handle the type provided [NoRequestHandlerException] will be thrown
     *
     * @param requestClass The type of the request
     * @return The [RequestHandler] for the request
     * @throws NoRequestHandlerException When there is not a RequestHandler available for the request
     */
    fun <TRequest : Request<TResponse>, TResponse> get(requestClass: Class<out TRequest>):
            RequestHandler<TRequest, TResponse>

    /**
     * Retrieves all the EventHandlers for the provided event type. If no EventHandlers are
     * registered to handle the type provided [NoEventHandlersException] will be thrown.
     *
     * @param eventClass The type of the event
     * @return Set of [EventHandler]s for the eventClass
     * @throws NoEventHandlersException When there are no EventHandlers available
     */
    fun <TEvent : Event> get(eventClass: Class<out TEvent>): Set<EventHandler<TEvent>>

    /**
     * Retrieves a CommandHandler for the provided type. If no CommandHandler
     * is registered for the Command type [NoCommandHandlerException] will be thrown.
     *
     * @param commandClass The type of the command
     * @return The [CommandHandler] for the command
     * @throws NoCommandHandlerException When there isn't a CommandHandler available
     */
    fun <TCommand : Command> get(commandClass: Class<out TCommand>): CommandHandler<TCommand>
}