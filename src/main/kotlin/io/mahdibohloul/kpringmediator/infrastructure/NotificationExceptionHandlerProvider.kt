package io.mahdibohloul.kpringmediator.infrastructure

import io.mahdibohloul.kpringmediator.core.NotificationExceptionHandler
import kotlin.reflect.KClass
import org.springframework.context.ApplicationContext

/**
 * A wrapper around [NotificationExceptionHandler]
 *
 * @author Mahdi Bohloul
 * @property applicationContext ApplicationContext from Spring used to retrieve beans
 * @property type Type of NotificationExceptionHandler
 */
class NotificationExceptionHandlerProvider<T : NotificationExceptionHandler<*, *>>(
    private val applicationContext: ApplicationContext,
    private val type: KClass<T>
) {
    internal val handler: T by lazy {
        applicationContext.getBean(type.java)
    }
}