package io.kpring.mediator.mock

import io.kpring.mediator.core.Command
import io.kpring.mediator.core.CommandHandler
import io.kpring.mediator.core.Notification
import io.kpring.mediator.core.NotificationHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

class LoggerMockCommand : Command

@Component
class LoggerMockCommandHandler : CommandHandler<LoggerMockCommand> {

    companion object {
        private val logger = LoggerFactory.getLogger(LoggerMockCommandHandler::class.java)
    }

    override suspend fun handle(command: LoggerMockCommand) {
        logger.info("Command executed asynchronously in ${this::class.simpleName}")
    }
}
