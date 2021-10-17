package io.mb.mediator.mock

import io.mb.mediator.interfaces.Command
import io.mb.mediator.interfaces.CommandHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

class LoggerCommandMock : Command {
    lateinit var response: String
}

@Component
class LoggerCommandMockHandler : CommandHandler<LoggerCommandMock> {

    companion object {
        private val logger = LoggerFactory.getLogger(LoggerCommandMockHandler::class.java)
    }

    override suspend fun handle(command: LoggerCommandMock) {
        logger.warn("Hello")
        command.response = "DONE"
    }
}