package io.mahdibohloul.kpringmediator.mock

import io.mahdibohloul.kpringmediator.core.Command
import io.mahdibohloul.kpringmediator.core.CommandHandler
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
