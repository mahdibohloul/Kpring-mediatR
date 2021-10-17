package io.mb.mediator.mock

import io.mb.mediator.interfaces.Event
import io.mb.mediator.interfaces.EventHandler
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

class EventMock(
) : Event {
    companion object {
        private val logger = LoggerFactory.getLogger(EventMock::class.java)

    }

    private var counter = 0
    fun countUp(java: Class<*>) {
        synchronized(this) {
            logger.info("*************************${java.name}***********$counter*************************")
            counter++
        }
    }

    fun getCount(): Int {
        return counter
    }

}


@Component
class EventFirstMockHandler : EventHandler<EventMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(EventFirstMockHandler::class.java)
    }

    override suspend fun handle(event: EventMock) {
        logger.info("First handler")
        for (i in (0 until 10))
            event.countUp(EventFirstMockHandler::class.java)
    }
}


@Component
class EventThirdMockHandler : EventHandler<EventMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(EventThirdMockHandler::class.java)
    }

    override suspend fun handle(event: EventMock) {
//        throw Exception("*****************HAHAHAHAHAHAHH*****************")
    }
}


@Component
class EventFourthMockHandler : EventHandler<EventMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(EventFourthMockHandler::class.java)
    }

    override suspend fun handle(event: EventMock) {
        logger.info("Fourth handler")
        for (i in (0 until 20)) {
            delay(2000)
            event.countUp(EventFourthMockHandler::class.java)
        }
    }
}

@Component
class EventSecondMockHandler : EventHandler<EventMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(EventSecondMockHandler::class.java)
    }

    override suspend fun handle(event: EventMock) {
        logger.info("Second handler")
        for (i in (0 until 10)) {
            delay(500)
            event.countUp(EventSecondMockHandler::class.java)
        }
    }
}

