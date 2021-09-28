package cab.mb.mediator.infrastructure

import cab.mb.mediator.interfaces.request.RequestHandler
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * A wrapper around [RequestHandler]
 *
 * @author Mahdi Bohloul
 * @property applicationContext ApplicationContext from Spring used to retrieve beans
 * @property type Type of CommandHandler
 */
class RequestHandlerProvider<T : RequestHandler<*, *>>(
    private val applicationContext: ApplicationContext,
    private val type: KClass<T>
) {
    internal val handler: T by lazy {
        applicationContext.getBean(type.java)
    }
}