package io.mahdibohloul.kpringmediator.core

/**
 * A factory for handlers for messages that can be sent or published in Kpring MediatR
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
     * Retrieves all the NotificationHandlers for the provided notification type. If no NotificationHandlers are
     * registered to handle the type provided [NoNotificationHandlersException] will be thrown.
     *
     * @param notificationClass The type of the event
     * @return Set of [NotificationHandler]s for the notificationClass
     * @throws NoNotificationHandlersException When there are no EventHandlers available
     */
    fun <TNotification : Notification> get(notificationClass: Class<out TNotification>): Set<NotificationHandler<TNotification>>

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