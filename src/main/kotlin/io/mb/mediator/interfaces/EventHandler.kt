package io.mb.mediator.interfaces

/**
 * @author Mahdi Bohloul
 * @param TEvent the type of event to be emitted
 */
interface EventHandler<in TEvent : Event> {

    /**
     * @author Mahdi Bohloul
     * @param event the type of emitted event
     */
    suspend fun handle(event: TEvent)
}