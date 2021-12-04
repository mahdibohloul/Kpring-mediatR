package io.mahdibohloul.kpringmediator.mock

import io.mahdibohloul.kpringmediator.core.Notification
import io.mahdibohloul.kpringmediator.core.NotificationHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

class NotificationMock : Notification


@Component
class FirstNotificationMockHandler : NotificationHandler<NotificationMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(FirstNotificationMockHandler::class.java)
    }

    override suspend fun handle(notification: NotificationMock) {
        logger.info("First handler executed asynchronously in ${this::class.simpleName}")
    }

    override fun getCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}

@Component
class SecondNotificationMockHandler : NotificationHandler<NotificationMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(SecondNotificationMockHandler::class.java)
    }

    override suspend fun handle(notification: NotificationMock) {
        logger.info("Second handler executed asynchronously in ${this::class.simpleName}")
    }
}


@Component
class ThirdNotificationMockHandler : NotificationHandler<NotificationMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(ThirdNotificationMockHandler::class.java)
    }

    override suspend fun handle(notification: NotificationMock) {
        logger.error("Exception thrown and expected to won't be propagated to the parent")
        throw Exception()
    }
}


@Component
class FourthNotificationMockHandler : NotificationHandler<NotificationMock> {
    companion object {
        private val logger = LoggerFactory.getLogger(FourthNotificationMockHandler::class.java)
    }

    override suspend fun handle(notification: NotificationMock) {
        logger.info("Fourth handler executed asynchronously in ${this::class.simpleName}")
    }
}



