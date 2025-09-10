package io.github.mahdibohloul.mediator.publisher

import io.github.mahdibohloul.mediator.notification.Notification

interface Publisher {
  suspend fun publishAsync(notification: Notification)
}
