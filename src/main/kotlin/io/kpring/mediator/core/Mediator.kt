package io.kpring.mediator.core

/**
 * Defines a mediator to encapsulate sending and publishing mediator patterns.
 *
 *
 * @author Mahdi Bohloul
 */
interface Mediator {

    /**
     * Send a [Request] to a single [RequestHandler] asynchronously.
     *
     * @author Mahdi Bohloul
     * @param request the request to be sent
     * @return [TResponse]
     */
    suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse

    /**
     * Publish an [Notification] to all registered [NotificationHandler]s for the particular notification.
     * The notification will be sent asynchronously to all the notification handlers.
     * Note that the exception which throws in the notification handlers will be ignored and doesn't propagate to the parent or cancel siblings
     *
     * @author Mahdi Bohloul
     * @param notification the notification to publishAsync
     *
     */
    suspend fun publishAsync(notification: Notification)

    /**
     * Send a [Command] to a single [CommandHandler] asynchronously.
     *
     * @author Mahdi Bohloul
     * @param command the command to be sent
     */
    suspend fun sendAsync(command: Command)
}