package io.mahdibohloul.kpringmediator.builder

import io.github.mahdibohloul.mediator.Mediator
import io.github.mahdibohloul.mediator.factories.ComponentFactory
import io.github.mahdibohloul.mediator.DefaultMediator
import org.springframework.context.ApplicationContext

class MediatorBuilder(private val applicationContext: ApplicationContext) {
  fun build(): Mediator = DefaultMediator(ComponentFactory(applicationContext))
}
