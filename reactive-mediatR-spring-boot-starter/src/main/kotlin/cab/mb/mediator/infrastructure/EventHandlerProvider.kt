package cab.mb.mediator.infrastructure

import cab.mb.mediator.interfaces.event.EventHandler
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass


/**
 * A wrapper around [EventHandler]
 *
 * @author Mahdi Bohloul
 * @property applicationContext ApplicationContext from Spring used to retrieve beans
 * @property type Type of CommandHandler
 */
internal class EventHandlerProvider<T : EventHandler<*>>(
    private val applicationContext: ApplicationContext,
    private val type: KClass<T>
) {
    internal val handler: T by lazy {
        applicationContext.getBean(type.java)
    }
}