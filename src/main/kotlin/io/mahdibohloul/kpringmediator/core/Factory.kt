package io.mahdibohloul.kpringmediator.core

import kotlin.reflect.KClass

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
    fun <TRequest : Request<TResponse>, TResponse> getRequestHandler(requestClass: KClass<out TRequest>):
        RequestHandler<TRequest, TResponse>

    /**
     * Retrieves all the NotificationHandlers for the provided notification type. If no NotificationHandlers are
     * registered to handle the type provided [NoNotificationHandlersException] will be thrown.
     *
     * @param notificationClass The type of the event
     * @return Set of [NotificationHandler]s for the notificationClass
     * @throws NoNotificationHandlersException When there are no EventHandlers available
     */
    fun <TNotification : Notification> getNotificationHandlers(notificationClass: KClass<out TNotification>): Set<NotificationHandler<TNotification>>

    /**
     * Retrieves a CommandHandler for the provided type. If no CommandHandler
     * is registered for the Command type [NoCommandHandlerException] will be thrown.
     *
     * @param commandClass The type of the command
     * @return The [CommandHandler] for the command
     * @throws NoCommandHandlerException When there isn't a CommandHandler available
     */
    fun <TCommand : Command> getCommandHandler(commandClass: KClass<out TCommand>): CommandHandler<TCommand>

    /**
     * Retrieves all the NotificationExceptionHandlers for the provided notification and exception type. If no NotificationExceptionHandlers are
     * registered to handle the notification and exception type provided an empty set will be returned.
     *
     * @author Mahdi Bohloul
     * @param notificationClass The type of the event
     * @param exceptionClass The type of the exception
     * @return Set of [NotificationExceptionHandler]s for the notificationClass and exceptionClass
     */
    fun <TNotification : Notification, TException : Exception> getNotificationExceptionHandlers(
        notificationClass: KClass<out TNotification>, exceptionClass: KClass<out TException>
    ):
        Set<NotificationExceptionHandler<TNotification, TException>>
}