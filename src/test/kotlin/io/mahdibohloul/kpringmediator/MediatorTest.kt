package io.mahdibohloul.kpringmediator

import io.mahdibohloul.kpringmediator.builder.MediatorBuilder
import io.mahdibohloul.kpringmediator.core.Mediator
import io.mahdibohloul.kpringmediator.mock.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [ApplicationContext::class, HelloMockRequestHandler::class, LoggerMockCommandHandler::class,
        FirstNotificationMockHandler::class, SecondNotificationMockHandler::class, ThirdNotificationMockHandler::class,
        FourthNotificationMockHandler::class, MockNotificationExceptionHandler::class]
)
class MediatorTest {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    private lateinit var mediator: Mediator

    @BeforeEach
    fun init() {
        mediator = MediatorBuilder(applicationContext).build()
    }

    @Test
    fun `request handler should return hello as string`() {
        val request = HelloMockRequest()
        val result = runBlocking { mediator.sendAsync(request) }
        assertEquals("hello", result)
    }

    @Test
    fun `command handler should complete successfully`() {
        assertDoesNotThrow {
            runBlocking {
                val command = LoggerMockCommand()
                mediator.sendAsync(command)
            }
        }

    }

    @Test
    fun `events executed asynchronously and in one of them an exception thrown and expect the others running without any interruption`() {
        assertDoesNotThrow {
            runBlocking {
                val event = NotificationMock()
                mediator.publishAsync(event)
            }
        }
    }

}