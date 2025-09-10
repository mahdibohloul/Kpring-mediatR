package io.github.mahdibohloul.mediator

import io.github.mahdibohloul.mediator.factories.ComponentFactory

fun ComponentFactory.enableNotificationExceptionHandling(): ComponentFactory {
  this.handleNotificationExceptions = true
  return this
}
