package io.mahdibohloul.kpringmediator.core

import io.mahdibohloul.kpringmediator.infrastructure.DefaultFactory

fun DefaultFactory.enableNotificationExceptionHandling(): DefaultFactory {
    this.handleNotificationExceptions = true
    return this
}