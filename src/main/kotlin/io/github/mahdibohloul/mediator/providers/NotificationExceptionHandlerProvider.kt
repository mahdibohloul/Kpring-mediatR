package io.github.mahdibohloul.mediator.providers

import io.github.mahdibohloul.mediator.notification.NotificationExceptionHandler
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

/**
 * A wrapper around [NotificationExceptionHandler]
 *
 * @author Mahdi Bohloul
 * @property applicationContext ApplicationContext from Spring used to retrieve beans
 * @property type Type of NotificationExceptionHandler
 */
class NotificationExceptionHandlerProvider<T : NotificationExceptionHandler<*, *>>(
  private val applicationContext: ApplicationContext,
  private val type: KClass<T>,
) {
  internal val handler: T by lazy {
    applicationContext.getBean(type.java)
  }
}
