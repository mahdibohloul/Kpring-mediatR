package io.github.mahdibohloul.mediator.builder

import io.github.mahdibohloul.mediator.DefaultMediator
import io.github.mahdibohloul.mediator.Mediator
import io.github.mahdibohloul.mediator.factories.ComponentFactory
import io.github.mahdibohloul.mediator.notification.NotificationProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.ApplicationContext

class MediatorBuilder(private val applicationContext: ApplicationContext) {
  fun build(): Mediator {
    // Create a test scope for the builder
    val testScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    return DefaultMediator(
      ComponentFactory(applicationContext, notificationProperties = NotificationProperties()),
      testScope,
    )
  }
}
