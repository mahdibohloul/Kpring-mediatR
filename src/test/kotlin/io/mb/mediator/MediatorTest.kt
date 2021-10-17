package io.mb.mediator

import io.mb.mediator.interfaces.Mediator
import io.mb.mediator.mock.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import kotlin.test.assertEquals

@SpringBootTest
class MediatorTest {
    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var mediator: Mediator

//    @BeforeEach
//    fun init() {
//    }

    @Test
    fun `request handler should return hello as string`() {
        val request = HelloRequestMock()
        val result = runBlocking { mediator.sendAsync(request) }
        assertEquals("hello", result)
    }

    @Test
    fun `command handler should complete successfully`() = runBlockingTest {
        val command = LoggerCommandMock()
        mediator.sendAsync(command)
        assertEquals("DONE", command.response)
    }

//    @Test
//    fun `event async publishing with exception`() = coroutineScope {
//        val event = EventMock()
//        assertDoesNotThrow { mediator.publishAsync(event) }
//    }

}