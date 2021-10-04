package io.mb.mediator.interfaces

/**
 * Defines a mediator to encapsulate sending and publishing interaction patterns.
 *
 *
 * @author Mahdi Bohloul
 */
interface Mediator {

//    /**
//     * Send a [Request] to a single [RequestHandler] synchronously
//     *
//     * @author Mahdi Bohloul
//     * @param request the request to be sent
//     * @return
//     */
//    fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse

    /**
     * Send a [Request] to a single [RequestHandler] asynchronously.
     *
     * @author Mahdi Bohloul
     * @param request the request to be sent
     * @return
     */
    suspend fun <TRequest : Request<TResponse>, TResponse> sendAsync(request: TRequest): TResponse

//    /**
//     * Publish an [Event] to all registered [EventHandler]s for the particular event synchronously.
//     *
//     * @author Mahdi Bohloul
//     * @param event the event to publishAsync
//     */
//    fun publishAsync(event: Event)

    /**
     * Publish an [Event] to all registered [EventHandler]s for the particular event.
     * The event will be sent asynchronously to all the events handlers.
     *
     * @author Mahdi Bohloul
     * @param event the event to publishAsync
     *
     */
    suspend fun publishAsync(event: Event)

//    /**
//     * Send a [Command] to a single [CommandHandler] synchronously
//     *
//     * @author Mahdi Bohloul
//     * @param command the command to be sent
//     */
//    fun sendAsync(command: Command)

    /**
     * Send a [Command] to a single [CommandHandler] asynchronously.
     *
     * @author Mahdi Bohloul
     * @param command the command to be sent
     */
    suspend fun sendAsync(command: Command)
}