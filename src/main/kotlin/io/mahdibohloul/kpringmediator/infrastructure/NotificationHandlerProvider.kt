package io.mahdibohloul.kpringmediator.infrastructure

import io.mahdibohloul.kpringmediator.core.NotificationHandler
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass


/**
 * A wrapper around [NotificationHandler]
 *
 * @author Mahdi Bohloul
 * @property applicationContext ApplicationContext from Spring used to retrieve beans
 * @property type Type of CommandHandler
 */
internal class NotificationHandlerProvider<T : NotificationHandler<*>>(
    private val applicationContext: ApplicationContext,
    private val type: KClass<T>
) {
    internal val handler: T by lazy {
        applicationContext.getBean(type.java)
    }
}