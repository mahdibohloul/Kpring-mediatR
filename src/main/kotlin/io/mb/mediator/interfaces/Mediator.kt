package io.mb.mediator.interfaces

import io.mb.mediator.interfaces.command.Command
import io.mb.mediator.interfaces.command.CommandHandler
import io.mb.mediator.interfaces.event.Event
import io.mb.mediator.interfaces.event.EventHandler
import io.mb.mediator.interfaces.request.Request
import io.mb.mediator.interfaces.request.RequestHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Defines a mediator to encapsulate sending and publishing interaction patterns.
 *
 *
 * @author Mahdi Bohloul
 */
interface Mediator {

    /**
     * Send a [Request] to a single [RequestHandler] synchronously
     *
     * @author Mahdi Bohloul
     * @param request the request to be sent
     * @return
     */
    fun <TRequest : Request<TResponse>, TResponse> send(request: TRequest): TResponse

    /**
     * Send a [Request] to a single [RequestHandler] and return a cold Mono which should be subscribed to be executed
     *
     * @author Mahdi Bohloul
     * @param request the request to be sent
     * @return
     */
    fun <TRequest : Request<TResponse>, TResponse> sendReactive(request: TRequest): Mono<TResponse>

    /**
     * Publish an [Event] to all registered [EventHandler]s for the particular event synchronously.
     *
     * @author Mahdi Bohloul
     * @param event the event to publish
     */
    fun publish(event: Event)

    /**
     * Publish an [Event] to all registered [EventHandler]s for the particular event
     * and return a cold flux which should be subscribed to be executed
     *
     * @author Mahdi Bohloul
     * @param event the event to publish
     */
    fun publishReactive(event: Event): Flux<Void>

    /**
     * Send a [Command] to a single [CommandHandler] synchronously
     *
     * @author Mahdi Bohloul
     * @param command the command to be sent
     */
    fun send(command: Command)

    /**
     * Send a [Command] to a single [CommandHandler] and return a cold Mono which should be subscribed to be executed
     *
     * @author Mahdi Bohloul
     * @param command the command to be sent
     */
    fun sendReactive(command: Command): Mono<Void>
}