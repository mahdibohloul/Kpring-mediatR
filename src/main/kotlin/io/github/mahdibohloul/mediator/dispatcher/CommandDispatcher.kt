package io.github.mahdibohloul.mediator.dispatcher

import io.github.mahdibohloul.mediator.command.Command

interface CommandDispatcher {
  suspend fun sendAsync(command: Command)
}
